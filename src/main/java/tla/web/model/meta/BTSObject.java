package tla.web.model.meta;

import java.util.List;
import java.util.TreeMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import tla.domain.model.EditorInfo;
import tla.domain.model.Passport;
import tla.web.model.parts.ExternalReference;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BTSObject extends TLAObject {

    private Passport passport;

    private EditorInfo edited;

    private String reviewState;

    private String subtype;

    private String name;

    private TreeMap<String, List<ExternalReference>> externalReferences;

}
