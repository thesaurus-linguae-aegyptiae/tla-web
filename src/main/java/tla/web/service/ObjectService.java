package tla.web.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import tla.domain.dto.meta.AbstractDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.model.ObjectReference;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.web.repo.TlaClient;

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
    public ObjectDetails<T> get(String id) {
        ObjectDetails<TLAObject> container = ObjectDetails.from(
            retrieveSingleDocument(id)
        );
        return new ObjectDetails<T>(
            (T) container.getObject(),
            container.getRelated()
        );
    }

}