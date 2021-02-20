package tla.web.model.meta;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tla.domain.dto.extern.DocumentWrapper;
import tla.domain.dto.meta.DocumentDto;
import tla.domain.model.meta.Resolvable;
import tla.web.model.mappings.MappingConfig;

@Getter
@AllArgsConstructor
public class ObjectsContainer {

    /**
     * Related objects are to be grouped by eclass and stored under their ID.
     */
    protected Map<String, Map<String, TLAObject>> related;

    public ObjectsContainer() {
        this.related = new HashMap<>();
    }

    public ObjectsContainer(DocumentWrapper dtoContainer) {
        this.related = this.importRelatedObjects(dtoContainer.getRelated());
    }

    /**
    * Looks up an object identified by a {@link ObjectReference} in this container's
    * <code>related</code> map. Returns null if the <code>related</code>
    * map does not contain such an object.
    */
    protected TLAObject expandRelatedObject(Resolvable reference) {
        if (this.related.containsKey(reference.getEclass())) {
            return this.related.get(reference.getEclass()).getOrDefault(
                reference.getId(),
                null
            );
        }
        return null;
    }

    /**
     * Goes through an entire related objects map as contained by incoming
     * {@link DocumentWrapper} DTO containers, and converts all DTO in there to their respective
     * UI model counterparts ({@link TLAObject} instances).
     */
    protected Map<String, Map<String, TLAObject>> importRelatedObjects(
        Map<String, Map<String, DocumentDto>> dtoRelated
    ) {
        if (dtoRelated != null) {
            return dtoRelated.entrySet().stream().collect(
                Collectors.toMap(
                    Entry::getKey,
                    dtoEclassesEntry -> dtoEclassesEntry.getValue().values().stream().collect(
                        Collectors.toMap(
                            DocumentDto::getId,
                            dto -> MappingConfig.convertDTO(dto)
                        )
                    )
                )
            );
        } else {
            return new HashMap<>();
        }
    }

}