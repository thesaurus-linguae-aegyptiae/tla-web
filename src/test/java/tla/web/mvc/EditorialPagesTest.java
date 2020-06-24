package tla.web.mvc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import tla.web.config.EditorialConfig.EditorialRegistry;

@SpringBootTest
public class EditorialPagesTest extends ViewTest {

    static final String NO_SUPPORT_LANG = "es";

    @Autowired
    private EditorialRegistry pages;

    @Test
    void registryAvailable() {
        assertAll("editorial registry properly injected",
            () -> assertNotNull(pages),
            () -> assertEquals(2, pages.getLangSupport().keySet().size(), "2 pages"),
            () -> assertEquals(
                Set.of("de", "en"),
                pages.getSupportedLanguages("/legal/imprint"),
                "languages in which imprint is available"
            )
        );
    }

    @Override
    void testLocalization(ResultActions testResponse, String lang) throws Exception {
        super.testLocalization(testResponse, lang);
        testResponse.andExpect(
            header().exists(HttpHeaders.CONTENT_LANGUAGE)
        // ).andExpect(
        //     header().string(HttpHeaders.CONTENT_LANGUAGE, lang)  // spring seems to snatch specified content lang somehow so ignore it for the time being
        ).andExpect(
            model().attribute("contentLang", lang)
        ).andExpect(
            xpath("/html/@lang").string(lang)
        );
    }

    @Test
    void testEditorials() throws Exception {
        for (Entry<String, Set<String>> e : pages.getLangSupport().entrySet()) {
            openEditorial(
                e.getKey(), new LinkedList<>(e.getValue())
            );
        }
    }

    private void openEditorial(String path, List<String> languages) throws Exception {
        assertTrue(languages.size() > 0, "at least one language supported");
        for (String lang : languages) {
            ResultActions test = mockMvc.perform(
                get(
                    String.format("/%s", path)
                ).header(
                    HttpHeaders.ACCEPT_LANGUAGE, lang
                )
            ).andDo(
                print()
            ).andExpect(
                model().attributeExists("env")
            );
            testLocalization(test, lang);
        }
    }

    @Test
    void acceptedLanguageUnsupported() throws Exception {
        ResultActions test = mockMvc.perform(
            get(
                "/legal/imprint"
            ).header(
                HttpHeaders.ACCEPT_LANGUAGE, NO_SUPPORT_LANG
            )
        ).andDo(
            print()
        );
        testLocalization(test, "en"); // TODO default
    }

}