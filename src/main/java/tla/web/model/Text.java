package tla.web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.domain.dto.TextDto;
import tla.domain.model.meta.BTSeClass;
import tla.web.model.meta.BackendPath;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("text")
@BTSeClass("BTSText")
public class Text extends CorpusObject {

    private TextDto.WordCount wordCount;

}
