package tla.web.model.meta;

import java.util.List;
import java.util.TreeMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import tla.domain.model.ObjectReference;
import tla.domain.model.meta.AbstractBTSBaseClass;
import tla.domain.model.meta.Relatable;
import tla.web.model.parts.ExternalReference;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class TLAObject extends AbstractBTSBaseClass implements Relatable<List<ObjectReference>> {

    private String id;

    private String type;

    private TreeMap<String, List<ObjectReference>> relations;

    private TreeMap<String, List<ExternalReference>> externalReferences;

}
