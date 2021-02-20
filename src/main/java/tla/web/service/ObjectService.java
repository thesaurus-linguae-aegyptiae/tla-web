package tla.web.service;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import tla.domain.command.SearchCommand;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.domain.model.meta.BTSeClass;
import tla.web.model.mappings.MappingConfig;
import tla.web.model.meta.ModelClass;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.SearchResults;
import tla.web.model.meta.TLAObject;
import tla.web.repo.TlaClient;

/**
 * Generic TLA domain model type object operations component.
 *
 * Subclasses are expected to specify their resptive domain model types in a {@link ModelClass}
 * annotation, so that it can be registered with automatic DTO model mapping and the
 * TLA backend API client.
 */
@Slf4j
public abstract class ObjectService<T extends TLAObject> {

    @Autowired
    protected TlaClient backend;

    private Class<T> modelClass;

    @SuppressWarnings("unchecked")
    public ObjectService() {
        for (Annotation a : this.getClass().getAnnotationsByType(ModelClass.class)) {
            Class<? extends TLAObject> modelClass = ((ModelClass) a).value();
            this.modelClass = (Class<T>) modelClass;
            log.info("register {}", modelClass.getSimpleName());
            MappingConfig.registerModelClass(modelClass);
            TlaClient.registerModelclass(modelClass);
        }
    }

    /**
     * Returns the domain model class of which a service is taking care of and which has been
     * specified via a {@link ModelClass} annotation on top of that service.
     */
    public Class<T> getModelClass() {
        return this.modelClass;
    }

    /**
     * Returns {@link BTSeClass} value of a service's domain model class or the {@link TLADTO}
     * it is annotated with.
     *
     * @see #getModelClass()
     */
    public String getModelEClass() {
        return MappingConfig.extractEclass(this.getModelClass());
    }

    /**
     * Retrieve document details (document itself plus related objects) from backend.
     *
     * @param id document ID
     * @return DTO wrapped inside a {@link SingleDocumentWrapper} container
     */
    public SingleDocumentWrapper<AbstractDto> retrieveSingleDocument(String id) {
        return backend.retrieveObject(
            this.getModelClass(), id
        );
    }

    /**
     * Retrieve a container with a single domain model class instance.
     * @param id object ID
     * @return model object wrapped inside an {@link ObjectDetails} container
     */
    @SuppressWarnings("unchecked")
    public Optional<ObjectDetails<T>> getDetails(String id) {
        try {
            ObjectDetails<?> container = ObjectDetails.from(
                retrieveSingleDocument(id)
            );
            return Optional.of(
                new ObjectDetails<T>(
                    (T) container.getObject(),
                    container.getRelated()
                )
            );
        } catch (Exception e) {
            log.error("failed to obtain details for object {}!", id);
            log.error("cause: ", e);
            return Optional.empty();
        }
    }

    /**
     * Generate a label for an object (used as caption in object detail pages).
     */
    public abstract String getLabel(T object);

    /**
     * Override this to do whatever necessary to search results container before passing it
     * to view controller. Gets called by {@link #search(SearchCommand, Integer)} before
     * returning the results to the view controller.
     */
    protected SearchResults preProcess(SearchResults searchResults) {
        return searchResults;
    }

    /**
     * Send search form to backend and convert results from DTO to frontend model objects.
     * Passes the results to {@link #preProcess(SearchResults)} before returning them,
     * the default implementation of which does nothing to them at all, but any enhancements
     * of the search results container right before it returns to the view controller can be
     * done by overriding this method.
     */
    public SearchResults search(SearchCommand<?> command, Integer page) {
        SearchResultsWrapper<?> response = backend.searchObjects(
            this.getModelClass(), command, page
        );
        return this.preProcess(
            SearchResults.from(response)
        );
    }

}