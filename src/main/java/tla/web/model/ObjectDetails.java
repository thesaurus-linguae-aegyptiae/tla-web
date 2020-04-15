package tla.web.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tla.domain.dto.DocumentDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.mappings.MappingConfig;

@Getter
@AllArgsConstructor
public class ObjectDetails<T extends TLAObject> {

    @Autowired
    private static ModelMapper mapper;

    private T object;

    /**
     * Related objects are to be grouped by eclass and stored under their ID.
     */
    private Map<String, Map<String, TLAObject>> relatedObjects;

    public ObjectDetails(T object) {
        this.object = object;
    }

    /**
     * Map payload and related objects from DTO to domain model types.
     */
    public static ObjectDetails<TLAObject> from(SingleDocumentWrapper<DocumentDto> wrapper) {
        ObjectDetails<TLAObject> container = new ObjectDetails<TLAObject>(
            mapper.map(
                wrapper.getDoc(),
                TLAObject.class
            )
        );
        container.relatedObjects = new HashMap<String, Map<String, TLAObject>>();
        for (Entry<String, Map<String, DocumentDto>> eclassEntry : wrapper.getRelated().entrySet()) {
            String eclass = eclassEntry.getKey();
            container.relatedObjects.put(
                eclass,
                eclassEntry.getValue().values().stream().collect(
                    Collectors.toMap(
                        DocumentDto::getId,
                        dto -> MappingConfig.convertDTO(dto)
                    )
                )
            );
        }
        return container;
    }

}