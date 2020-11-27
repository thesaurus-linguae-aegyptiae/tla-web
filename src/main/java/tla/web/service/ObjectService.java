package tla.web.service;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.domain.model.meta.BTSeClass;
import tla.web.model.mappings.MappingConfig;
import tla.web.model.meta.ModelClass;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.TLAObject;
import tla.web.repo.TlaClient;

import lombok.extern.slf4j.Slf4j;

/**
 * Generic TLA domain model type object operations component.
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
     * Returns {@link BTSeClass} value of a service's domain model class.
     *
     * @see #getModelClass()
     */
    public String getModelEClass() {
        for (Annotation a : getModelClass().getAnnotationsByType(BTSeClass.class)) {
            return ((BTSeClass) a).value();
        }
        return null;
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
            ObjectDetails<TLAObject> container = ObjectDetails.from(
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

}
