package tla.web.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import tla.domain.dto.AnnotationDto;
import tla.domain.dto.LemmaDto;
import tla.domain.model.EditorInfo;
import tla.domain.model.ExternalReference;
import tla.domain.model.Language;
import tla.domain.model.LemmaWord;
import tla.domain.model.Passport;
import tla.domain.model.Transcription;
import tla.web.model.mappings.MappingConfig;

@SpringBootTest
public class MappingTest {

    @Test
    void lemma() throws Exception {
        Passport p = new Passport();
        p.add("key", new Passport("value"));
        LemmaDto dto = LemmaDto.builder()
            .id("ID")
            .editors(EditorInfo.builder().author("author").build())
            .word(new LemmaWord(new Transcription("nfr", "nfr"), "N35"))
            .translation(Language.FR, List.of("traduction"))
            .passport(p)
            .externalReference("cfeetk", new TreeSet<>(List.of(new ExternalReference("1", null))))
            .build();
        TLAObject object = MappingConfig.convertDTO(dto);
        assertTrue(object instanceof Lemma);
        Lemma lemma = (Lemma) object;
        Word word = lemma.getWords().get(0);
        assertAll("test lemma mapping",
            () -> assertNotNull(lemma, "lemma DTO should be converted"),
            () -> assertNotNull(lemma.getEdited(), "edit info expected"),
            () -> assertNotNull(lemma.getPassport(), "metadata expected"),
            () -> assertEquals("value", lemma.getPassport().extractValues().get(0).get(), "check single only metadata value"),
            () -> assertEquals(1, lemma.getWords().size(), "expect 1 word"),
            () -> assertNotNull(word.getTranscription(), "expect transcription"),
            () -> assertNotNull(word.getGlyphs(), "expect glyphs"),
            () -> assertEquals("N35", word.getGlyphs().getMdc(), "mdc correct?"),
            () -> assertTrue(
                word.getGlyphs().getSvg().startsWith("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>"),
                "check svg xml JSesh rendering result"
            ),
            () -> assertEquals(
                "http://sith.huma-num.fr/vocable/1",
                lemma.getExternalReferences().get("cfeetk").get(0).getHref()
            )
        );
    }

    @Test
    void annotation() throws Exception {
        AnnotationDto dto = new AnnotationDto();
        Passport p = new Passport();
        Passport annotationNode = new Passport();
        annotationNode.add("lemma", new Passport("comment"));
        p.add("annotation", annotationNode);
        assertAll("test assumptions about passport",
            () -> assertEquals(1, p.size()),
            () -> assertTrue(p.containsKey("annotation")),
            () -> assertEquals(1, p.extractPaths().size()),
            () -> assertTrue(p.extractPaths().contains("annotation.lemma"))
        );
        dto.setPassport(p);
        dto.setEditors(EditorInfo.builder().author("author").build());
        dto.setEclass("BTSAnnotation");
        TLAObject object = MappingConfig.convertDTO(dto);
        assertTrue(object instanceof Annotation);
        Annotation a = (Annotation) object;
        assertAll("test annotation mapping",
            () -> assertNotNull(a, "model object should not be null"),
            () -> assertEquals("BTSAnnotation", a.getEclass(), "eclass should match"),
            () -> assertNotNull(a.getEdited(), "edit info should be there"),
            () -> assertEquals("author", a.getEdited().getAuthor(), "author should be as expected"),
            () -> assertNotNull(a.getPassport(), "metadata should be present"),
            () -> assertEquals(1, a.getPassport().size(), "expect 1 metadata root key"),
            () -> assertEquals(1, a.getPassport().extractProperty("annotation.lemma").size(), "expect 1 leaf node under annotation.lemma"),
            () -> assertNotNull(a.getBody(), "lemma annotation body should be extractable from metadata"),
            () -> assertEquals("comment", a.getBody(), "see if lemma annotation body gets extracted")
        );
    }
    
}
