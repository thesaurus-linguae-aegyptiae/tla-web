package tla.web.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.jooq.lambda.Seq;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import tla.web.config.EditorialConfig.EditorialRegistry;

public class TestEditorialsRegistry {

    private EditorialRegistry pages = new EditorialConfig().editorialRegistry();

    static List<Arguments> urlPathTestParams() {
        return List.of(
            Arguments.of("en/home.html", "home", "en"),
            Arguments.of("en/info/project.html", "info/project", "en"),
            Arguments.of("en/legal/imprint.html", "legal/imprint", "en"),
            Arguments.of("de/legal/imprint.html", "legal/imprint", "de")
        );
    }

    @ParameterizedTest
    @MethodSource("urlPathTestParams")
    void extract_urlPath_langId(String filePath, String urlPath, String lang) {
        String[] segm = filePath.split("/");
        Path nativePath = Path.of(
            segm[0],
            Seq.of(segm).slice(1, segm.length).toArray(String[]::new)
        );
        assertEquals(filePath.charAt(0), nativePath.toString().charAt(0));
        assertAll("URL path & lang ID extraction from file path",
            () -> assertTrue(
                nativePath.toString().contains(File.separator),
                String.format("native path be native (contains %s separator)", File.separator)
            ),
            () -> assertEquals(
                urlPath, pages.createURLPath(nativePath), "extracted URL path"
            ),
            () -> assertEquals(
                lang, pages.extractLanguageID(nativePath), "extracted language ID"
            )
        );
    }

}