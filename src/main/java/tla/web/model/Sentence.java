package tla.web.model;

import java.util.List;
import java.util.SortedMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import tla.domain.dto.SentenceDto.SentenceContext;
import tla.domain.model.Language;
import tla.domain.model.meta.BTSeClass;
import tla.web.model.meta.BackendPath;
import tla.web.model.meta.TLAObject;
import tla.web.model.parts.Transcription;
import tla.web.model.parts.Word;

@Getter
@NoArgsConstructor
@BackendPath("sentence")
@BTSeClass("BTSSentence")
@EqualsAndHashCode(callSuper = true)
public class Sentence extends TLAObject {

    private SentenceContext context;

    private List<Word> tokens;

    private int wordCount;

    private Transcription transcription;

    @Singular
    private SortedMap<Language, List<String>> translations;

}
