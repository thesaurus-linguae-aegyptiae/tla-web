package tla.web.service;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;
import tla.domain.command.SearchCommand;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.TLADTO;
import tla.web.config.DetailsProperties;
import tla.web.config.ObjectDetailsProperties;
import tla.web.config.SearchProperties;
import tla.web.model.mappings.MappingConfig;
import tla.web.model.meta.BTSObject;
import tla.web.model.meta.ModelClass;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.SearchResults;
import tla.web.model.meta.TLAObject;
import tla.web.repo.TlaClient;
import tla.error.ObjectNotFoundException;
 
/**
 * Generic TLA domain model type object operations component.
 *
 * Subclasses are expected to specify their resptive domain model types in a {@link ModelClass}
 * annotation, so that it can be registered with automatic DTO model mapping and the
 * TLA backend API client.
 */
@Slf4j
public abstract class ObjectService<T extends TLAObject> {

    protected final static ObjectDetailsProperties DETAILS_UNCONFIGURED = new ObjectDetailsProperties();
    protected final static LinkedHashMap<String, List<Passport>> EMPTY_MAP = new LinkedHashMap<>();
	 
	 public static final String BTS_ID_PATTERN = "(dm?)?\\d{1,6}|\\w{26,28}(\\-\\d{2})?";

    @Autowired
    protected TlaClient backend;

    /**
     * domain model type of the objects which are accessible via this service.
     */
    private Class<T> modelClass;

    @Autowired
    private DetailsProperties detailsProperties;

    /**
     * search-specific configuration for the domain model type represented by this service.
     * such search properties are being made available to the service layer under these conditions:
     * <ol>
     *   <li> specified in <code>application.yml</code> under the <code>search.{type}</code> path</li>
     *   <li> defined in a {@link SearchProperties} subclass with a {@link ModelClass} annotation</li>
     * </ol>
     */
    private SearchProperties searchProperties;

    ResponseEntity<?> EMPTY_RESPONSE = ResponseEntity.of(Optional.empty());

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
     * Return search properties registered for this service's model class, if there
     * are any.
     */
    public SearchProperties getSearchProperties() {
        if (this.searchProperties == null) {
            this.searchProperties = SearchProperties.getPropertiesFor(
                this.getModelClass()
            );
        }
        return this.searchProperties;
    }

    /**
     * Looks up the details view configuration for a service's domain model class.
     *
     * @see DetailsProperties
     */
    public ObjectDetailsProperties getDetailsProperties() {
        return this.detailsProperties.getOrDefault(
            TlaClient.getBackendPathPrefix(
                this.getModelClass()
            ),
            DETAILS_UNCONFIGURED
        );
    }

    /**
     * Extract an object's values for the passport properties configured for its domain model type.
     *
     * @see #getDetailsProperties()
     */
    public LinkedHashMap<String, List<Passport>> getDetailsPassportPropertyValues(T object) {
        if (object instanceof BTSObject && ((BTSObject) object).getPassport() != null) {
            LinkedHashMap<String, List<Passport>> passportValues = new LinkedHashMap<>();
            this.getDetailsProperties().getPassportProperties().stream().forEach(
                path -> {
                    if (!((BTSObject) object).getPassport().extractProperty(path).isEmpty()) {
                        passportValues.put(
                            path,
                            ((BTSObject) object).getPassport().extractProperty(path)
                        );
                    }
                }
            );
            return passportValues;
        } else {
            return EMPTY_MAP;
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
     * Call appropriate autocomplete endpoint for this service's model class.
     */
    public ResponseEntity<?> autoComplete(String term, String type) {
        if (term.trim().length() > 2) {
            return backend.autoComplete(
                this.getModelClass(), term, type
            );
        } else {
            return EMPTY_RESPONSE;
        }
    }

    /**
     * Retrieve document details (document itself plus related objects) from backend.
     *
     * @param id document ID
     * @return DTO wrapped inside a {@link SingleDocumentWrapper} container
     */
    public SingleDocumentWrapper<AbstractDto> retrieveSingleDocument(String id) {
		 if (!id.matches(BTS_ID_PATTERN)) {
			 log.warn("Tried to retrieve object by id without valid BTS id pattern: '{}'", id);
		  }
        try {
			  return backend.retrieveObject(
					this.getModelClass(), id
			  );
        } catch (Exception e) {
            //throw new ObjectNotFoundException(id);
            //log.warn("Failed to retrieve object by ID '{}'!", id);
            log.error("Failed to retrieve object by ID '{}'!", id);
            log.error("cause: ", e);
            return null;
        }
    }

    /**
     * Retrieve a container with a single domain model class instance.
     * @param id object ID
     * @return model object wrapped inside an {@link ObjectDetails} container
     */
    @SuppressWarnings("unchecked")
    public Optional<ObjectDetails<T>> getDetails(String id) {
		 if (!id.matches(BTS_ID_PATTERN)) {
			 log.warn("Tried to retrieve details of object by id without valid BTS id pattern: '{}'.", id);
			 //return Optional.empty();
		  }
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
            //throw new ObjectNotFoundException(id);
            //log.warn("Failed to obtain details for object ID '{}'!", id);
            log.error("Failed to obtain details for object ID '{}'!", id);
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