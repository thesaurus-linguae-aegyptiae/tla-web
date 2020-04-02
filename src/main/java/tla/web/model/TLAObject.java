package tla.web.model;

import java.util.List;
import java.util.TreeMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tla.domain.model.EditorInfo;
import tla.domain.model.Passport;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class TLAObject {

    private String id;

    private String name;

    private String type;

    private String subtype;

    private Passport passport;

    private EditorInfo edited;

    private String reviewState;

    private TreeMap<String, List<ExternalReference>> externalReferences;

}
