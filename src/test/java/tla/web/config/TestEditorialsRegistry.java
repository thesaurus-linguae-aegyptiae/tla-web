package tla.web.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
            Arguments.of("/en/legal/imprint.html", "legal/imprint"),
            Arguments.of("de/legal/imprint.html", "legal/imprint"),
            Arguments.of("\\en\\legal\\imprint.html", "legal/imprint"),
            Arguments.of("fr\\legal\\imprint.HTML", "legal/imprint"),
            Arguments.of("de\\legal\\imprint.htm", "legal/imprint"),
            Arguments.of("en/help/search/dictionary.html", "help/search/dictionary")
        );
    }

    @ParameterizedTest
    @MethodSource("urlPathTestParams")
    void urlPathExtraction(String filePath, String urlPath) {
        assertAll("come up with proper URL paths for editorial file paths",
            () -> assertEquals(
                urlPath,
                pages.createURLPath(Path.of(filePath)),
                "extracted URL path"
            )
        );
    }
    
}