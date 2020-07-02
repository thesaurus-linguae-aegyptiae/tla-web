package tla.web.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tla.domain.dto.meta.AbstractDto;
import tla.domain.dto.meta.DocumentDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.model.ObjectReference;
import tla.web.model.mappings.MappingConfig;

@Getter
@AllArgsConstructor
public class ObjectDetails<T extends TLAObject> {

    private T object;

    /**
     * Related objects are to be grouped by eclass and stored under their ID.
     */
    private Map<String, Map<String, TLAObject>> related;

    public ObjectDetails(T object) {
        this.object = object;
    }

    /**
     * Map payload and related objects from DTO to domain model types.
     */
    public static ObjectDetails<TLAObject> from(SingleDocumentWrapper<? extends AbstractDto> wrapper) {
        ObjectDetails<TLAObject> container = new ObjectDetails<TLAObject>(
            MappingConfig.convertDTO(
                wrapper.getDoc()
            )
        );
        container.related = new HashMap<String, Map<String, TLAObject>>();
        if (wrapper.getRelated() != null) {
            for (Entry<String, Map<String, DocumentDto>> eclassEntry : wrapper.getRelated().entrySet()) {
                String eclass = eclassEntry.getKey();
                container.related.put(
                    eclass,
                    eclassEntry.getValue().values().stream().collect(
                        Collectors.toMap(
                            DocumentDto::getId,
                            dto -> MappingConfig.convertDTO(dto)
                        )
                    )
                );
            }
        }
        return container;
    }

    /**
    * Looks up an object identified by a {@link ObjectReference} in this container's
    * <code>related</code> map. Returns null if the <code>related</code>
    * map does not contain such an object.
    */
    private TLAObject expandRelatedObject(ObjectReference reference) {
        if (this.related.containsKey(reference.getEclass())) {
            return this.related.get(reference.getEclass()).getOrDefault(
                reference.getId(),
                null
            );
        }
        return null;
    }

    /**
    * Projects objects in a single object details container's <code>related</code>
    * map to the wrapped object's <code>relations</code> (which are {@link TLAObject} stubs
    * in objects freshly converted from DTO).
    */
    public Map<String, List<TLAObject>> extractRelatedObjects() {
        return this.getObject().getRelations().entrySet().stream().collect(
            Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().stream().map(
                    reference -> this.expandRelatedObject(reference)
                ).filter(
                    o -> o != null
                ).collect(
                    Collectors.toList()
                )
            )
        );
    }

}