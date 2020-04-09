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
        Word word = lemma.getWords().get(0);
        assertAll("test lemma mapping",
            () -> assertNotNull(lemma, "lemma DTO should be converted"),
            () -> assertEquals(1, lemma.getWords().size(), "expect 1 word"),
            () -> assertNotNull(word.getTranscription(), "expect transcription"),
            () -> assertNotNull(word.getGlyphs(), "expect glyphs"),
            () -> assertEquals("N35", word.getGlyphs().getMdc(), "mdc correct?"),
            () -> assertTrue(
                word.getGlyphs().getSvg().startsWith("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>"),
                "check svg xml JSesh rendering result"
            )
        );
    }
    
}
