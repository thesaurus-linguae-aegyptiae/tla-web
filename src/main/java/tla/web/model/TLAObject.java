package tla.web.model;

import java.util.List;
import java.util.TreeMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tla.domain.model.EditorInfo;
import tla.domain.model.Passport;
import tla.domain.model.meta.AbstractBTSBaseClass;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class TLAObject extends AbstractBTSBaseClass {

    private String id;

    private String name;

    private String type;

    private String subtype;

    private Passport passport;

    private EditorInfo edited;

    private String reviewState;

    private TreeMap<String, List<ExternalReference>> externalReferences;

}
