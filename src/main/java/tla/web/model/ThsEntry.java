package tla.web.model;

import java.util.List;
import java.util.SortedMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import tla.domain.model.Language;
import tla.domain.model.ObjectPath;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.Hierarchic;
import tla.web.model.meta.BackendPath;
import tla.web.model.meta.BTSObject;

@Data
@BackendPath("ths")
@BTSeClass("BTSThsEntry")
@EqualsAndHashCode(callSuper = true)
public class ThsEntry extends BTSObject implements Hierarchic {

    @Singular
    private SortedMap<Language, List<String>> translations;

    private List<ObjectPath> paths;

}