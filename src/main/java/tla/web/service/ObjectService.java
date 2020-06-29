package tla.web.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.web.repo.TlaClient;

/**
 * Generic TLA domain model type object operations component.
 */
@Slf4j
public abstract class ObjectService<T extends TLAObject> {

    @Autowired
    protected TlaClient backend;

    /**
     * Subclasses must implement this method by calling:
     * <code>
     * backend.retrieveObject(ModelClass.class, id)
     * </code>
     * where <code>ModelClass</code> is the model class they are typed with.
     * @param id document ID
     * @return DTO wrapped inside a {@link SingleDocumentWrapper} container
     */
    protected abstract SingleDocumentWrapper<AbstractDto> retrieveSingleDocument(String id);

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
            log.warn("mapping of object details DTO failed!", e);
            return Optional.empty();
        }
    }

}
