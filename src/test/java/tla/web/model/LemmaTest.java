package tla.web.model;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LemmaTest {

    @Test
    void glyphConstructionTest() {
        Glyphs g1 = Glyphs.of(null);
        assertAll("creator should accept null value",
            () -> assertNotNull(g1, "should return object"),
            () -> assertTrue(g1.isEmpty(), "should be considered empty tho")
        );
        Glyphs g2 = Glyphs.of(" ");
        assertAll("creator should accept empty value",
            () -> assertNotNull(g2, "should return object"),
            () -> assertTrue(g2.isEmpty(), "should be considered empty tho")
        );
    }

    @Test
    void getHieroglyphs() throws Exception {
        List<String> wordGlyphs = List.of("N35", "", "G43");
        Lemma l = Lemma.builder()
            .id("1")
            .words(
                wordGlyphs.stream().map(
                    mdc -> Word.builder().glyphs(Glyphs.of(mdc)).build()
                ).collect(Collectors.toList())
            ).build();
        List<Glyphs> glyphs = l.getHieroglyphs();
        assertAll("test hieroglyphs from lemma extraction",
            () -> assertEquals(3, glyphs.size(), "item count"),
            () -> assertTrue(glyphs.get(1).isEmpty(), "second item empty"),
            () -> assertEquals(Glyphs.of("N35"), glyphs.get(0), "first item")
        );
    }

}