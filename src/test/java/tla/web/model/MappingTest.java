package tla.web.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tla.domain.dto.LemmaDto;
import tla.domain.model.Language;
import tla.domain.model.LemmaWord;
import tla.domain.model.Transcription;

@SpringBootTest
public class MappingTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void lemma() throws Exception {
        LemmaDto dto = LemmaDto.builder()
            .id("ID")
            .word(new LemmaWord(new Transcription("nfr", "nfr"), "N35"))
            .translation(Language.FR, List.of("traduction"))
            .build();
        Lemma lemma = modelMapper.map(dto, Lemma.class);
        assertAll("test lemma mapping",
            () -> assertNotNull(lemma, "lemma DTO should be converted"),
            () -> assertEquals(1, lemma.getWords().size(), "expect 1 word"),
            () -> assertNotNull(lemma.getWords().get(0).getTranscription(), "expect transcription")
        );
    }
    
}
