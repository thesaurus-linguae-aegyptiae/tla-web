package tla.web.model.meta;

import java.util.List;
import java.util.TreeMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import tla.domain.model.ObjectReference;
import tla.domain.model.meta.AbstractBTSBaseClass;
import tla.domain.model.meta.Relatable;
import tla.domain.model.meta.Resolvable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class TLAObject extends AbstractBTSBaseClass implements Relatable<List<ObjectReference>> {

    private String id;

    private String type;
    
    private String name;

    private TreeMap<String, List<ObjectReference>> relations;
    
    public <T extends TLAObject> int compareObjects(T b) {
    	return 0;
    }

    /**
     * Creates an {@link ObjectReference} instance identifying this object.
     */
    public Resolvable toObjectReference() {
        return ObjectReference.builder()
            .id(this.id)
            .eclass(this.getEclass())
            .type(this.getType())
            .name(
                this instanceof BTSObject ? ((BTSObject) this).getName() : null
            ).build();
    }

}