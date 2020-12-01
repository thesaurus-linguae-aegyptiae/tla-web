package tla.web.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import tla.domain.dto.AnnotationDto;
import tla.domain.dto.meta.DocumentDto;
import tla.domain.dto.LemmaDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.model.EditorInfo;
import tla.domain.model.ExternalReference;
import tla.domain.model.Language;
import tla.domain.model.ObjectReference;
import tla.domain.model.Passport;
import tla.domain.model.SentenceToken;
import tla.domain.model.Transcription;
import tla.web.model.mappings.MappingConfig;
import tla.web.model.mappings.Util;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.TLAObject;
import tla.web.model.parts.Token;

@SpringBootTest
public class MappingTest {

    @Test
    void lemma() throws Exception {
        Passport p = new Passport();
        p.add("key", new Passport("value"));
        LemmaDto dto = LemmaDto.builder()
            .id("ID")
            .editors(EditorInfo.builder().author("author").build())
            .word(new SentenceToken(new Transcription("nfr", "nfr"), "N35"))
            .translation(Language.FR, List.of("traduction"))
            .passport(p)
            .externalReference("cfeetk", new TreeSet<>(List.of(new ExternalReference("1", null))))
            .build();
        TLAObject object = MappingConfig.convertDTO(dto);
        assertTrue(object instanceof Lemma);
        Lemma lemma = (Lemma) object;
        Token word = lemma.getWords().get(0);
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
                word.getGlyphs().getSvg().startsWith("<svg xmlns"),
                "<?xml> tag removed from jsesh svg export"
            ),
            () -> assertTrue(
                word.getGlyphs().getSvg().contains("viewBox=\""),
                "jsesh svg export patched with viewBox attribute"
            ),
            () -> assertTrue(
                lemma.getExternalReferences().get("cfeetk").get(0) instanceof tla.web.model.parts.ExternalReference
            ),
            () -> assertEquals(
                "http://sith.huma-num.fr/vocable/1",
                lemma.getExternalReferences().get("cfeetk").get(0).getHref()
            )
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void ths() throws Exception {
        SingleDocumentWrapper<DocumentDto> dto = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/ths/details/KQY2F5SJVBBN7GRO5WUXKG5M6M.json",
            SingleDocumentWrapper.class
        );
        ObjectDetails<TLAObject> objectDetails = ObjectDetails.from(dto);
        assertTrue(objectDetails.getObject() instanceof ThsEntry);
        ThsEntry t = (ThsEntry) objectDetails.getObject();
        assertAll("test mapping from DTO to thesaurus object",
            () -> assertNotNull(t.getTranslations()),
            () -> assertTrue(t.getTranslations().containsKey(Language.FR)),
            () -> assertEquals(1, t.getTranslations().get(Language.FR).size()),
            () -> assertNotNull(t.getExternalReferences()),
            () -> assertNotNull(t.getEdited())
        );
        Map<String, Map<String, TLAObject>> related = objectDetails.getRelated();
        ObjectReference ref = t.getRelations().get("partOf").get(0);
        assertAll("expect related objects",
            () -> assertNotNull(t.getRelations(), "wrapped dto has relations"),
            () -> assertTrue(t.getRelations().containsKey("partOf"), "partOf relation"),
            () -> assertEquals(1, t.getRelations().get("partOf").size(), "1 related object"),
            () -> assertNotNull(ref, "objectreference present"),
            () -> assertNotNull(related, "related objects reified"),
            () -> assertTrue(related.containsKey("BTSThsEntry"), "related ths entries"),
            () -> assertEquals(1, related.get("BTSThsEntry").size(), "1 related object"),
            () -> assertNotNull(related.get("BTSThsEntry").get(ref.getId()), "related object"),
            () -> assertTrue(related.get("BTSThsEntry").get(ref.getId()) instanceof ThsEntry, "rel obj is ths entry")
        );
        Map<String, List<TLAObject>> objects = objectDetails.extractRelatedObjects();
        assertAll("expect related objects in order",
            () -> assertNotNull(objects.get("partOf").get(0), "parent object present"),
            () -> assertTrue(objects.get("partOf").get(0) instanceof ThsEntry, "parent is ths entry")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void thsRelatedObjects() throws Exception {
        SingleDocumentWrapper<DocumentDto> dto = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/ths/details/IMBHKBIKV5AUHEAAU2DL2K2GN4.json",
            SingleDocumentWrapper.class
        );
        ObjectDetails<TLAObject> details = ObjectDetails.from(dto);
        Map<String, List<TLAObject>> related = details.extractRelatedObjects();
        assertDoesNotThrow(
            () -> {
                related.entrySet().forEach(
                    e -> {
                        e.getValue().forEach(
                            v -> v.getEclass()
                        );
                    }
                );
            },
            "no null elements should be in structure"
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
        dto.setName("Annotation zu $jzr$");
        TLAObject object = MappingConfig.convertDTO(dto);
        assertTrue(object instanceof Annotation);
        Annotation a = (Annotation) object;
        assertAll("test annotation mapping",
            () -> assertNotNull(a, "model object should not be null"),
            () -> assertEquals("BTSAnnotation", a.getEclass(), "eclass should match"),
            () -> assertTrue(a.getName().endsWith("span>"), "markup in name should be escaped"),
            () -> assertNotNull(a.getEdited(), "edit info should be there"),
            () -> assertEquals("author", a.getEdited().getAuthor(), "author should be as expected"),
            () -> assertNotNull(a.getPassport(), "metadata should be present"),
            () -> assertEquals(1, a.getPassport().size(), "expect 1 metadata root key"),
            () -> assertEquals(1, a.getPassport().extractProperty("annotation.lemma").size(), "expect 1 leaf node under annotation.lemma"),
            () -> assertNotNull(a.getBody(), "lemma annotation body should be extractable from metadata"),
            () -> assertEquals("comment", a.getBody(), "see if lemma annotation body gets extracted")
        );
    }

    @Test
    void escapeMarkup() {
        String escaped = Util.escapeMarkup("$jzr$: Die Identifizierung $nfr$ etc pp");
        assertAll("test markup replacement",
            () -> assertTrue(escaped.startsWith("<span"), "should start with HTML tag"),
            () -> assertEquals(
                4,
                StringUtils.countOccurrencesOf(escaped, "span"),
                "result should contain 4 html tags"
            ),
            () -> assertTrue(!escaped.contains("$"), "no $ markup delimiter should remain"),
            () -> assertNull(Util.escapeMarkup(null), "nothing should happen if null"),
            () -> assertEquals("input", Util.escapeMarkup("input")),
            () -> assertEquals("$input", Util.escapeMarkup("$input"))
        );
    }

    @Test
    void sentenceFromJSON() throws Exception {
        var w = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/sentence/details/IBcCBpKz4FWJo0yOhxfTNEhx5J0.json",
            SingleDocumentWrapper.class
        );
        assertAll("DTO deserialized",
            () -> assertNotNull(w, "sentence wrapper deserialized"),
            () -> assertNotNull(w.getDoc(), "payload received")
        );
        TLAObject o = MappingConfig.convertDTO(w.getDoc());
        Sentence s = (Sentence) o;
        assertAll("DTO import into UI model",
            () -> assertNotNull(s, "sentence instantiated"),
            () -> assertNotNull(s.getContext(), "sentence context"),
            () -> assertEquals(5, s.getContext().getPosition(), "sentence position"),
            () -> assertNotNull(s.getRelations(), "sentence object references"),
            () -> assertEquals(2, s.getRelations().size(), "2 relation types"),
            () -> assertNotNull(s.getTranslations(), "sentence translations"),
            () -> assertNotNull(s.getTranslations().get(Language.DE).get(0), "german translation"),
            () -> assertNotNull(s.getTranscription(), "sentence transcription"),
            () -> assertNotNull(s.getTranscription().getUnicode(), "sentence transcription"),
            () -> assertNotNull(s.getTokens(), "sentence tokens"),
            () -> assertFalse(s.getTokens().isEmpty(), "token received"),
            () -> assertTrue(s.getWordCount() <= s.getTokens().size(), "sentence proper word count")
        );
        Token t = s.getTokens().get(0);
        assertAll("UI model sentence first token",
            () -> assertNotNull(t.getId(), "ID"),
            () -> assertNotNull(t.getLabel(), "label"),
            () -> assertNotNull(t.getType(), "type"),
            () -> assertNotNull(t.getFlexion(), "flexion information"),
            () -> assertNotNull(t.getTranslations(), "translation"),
            () -> assertNotNull(t.getLemma(), "lemmatization information"),
            () -> assertTrue(t.getAnnoTypes().contains("subtext"), "annotation types")
        );
    }
}
