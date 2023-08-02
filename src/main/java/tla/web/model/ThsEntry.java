package tla.web.model;

import java.util.List;
import java.util.SortedMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import tla.domain.dto.ThsEntryDto;
import tla.domain.model.Language;
import tla.domain.model.ObjectPath;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.Hierarchic;
import tla.domain.model.meta.TLADTO;
import tla.web.model.meta.BTSObject;
import tla.web.model.meta.BackendPath;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("ths")
@BTSeClass("BTSThsEntry")
@JsonIgnoreProperties(ignoreUnknown = true)
@TLADTO(ThsEntryDto.class)
public class ThsEntry extends BTSObject implements Hierarchic {

    @Singular
    private SortedMap<Language, List<String>> translations;

    private List<ObjectPath> paths;

}