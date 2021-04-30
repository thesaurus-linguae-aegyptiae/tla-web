package tla.web.mvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import tla.web.config.LemmaSearchProperties;

public class SearchFormTest extends ViewTest {

    @Autowired
    private LemmaSearchProperties searchConf;

    @Value("${search.config.default}")
    private String defaultForm;

    @Test
    void root() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    private String getWordClassWithoutSubtypes() {
        for (Entry<String, List<String>> e : searchConf.getWordClasses().entrySet()) {
            if (e.getValue().isEmpty()) {
                return e.getKey();
            }
        }
        return null;
    }

    @Test
    void controllerAdvice() throws Exception {
        mockMvc.perform(get("/search")).andDo(print())
            .andExpect(
                model().attributeExists("env")
            ).andExpect(
                model().attribute("env", notNullValue())
            );
    }

    @ParameterizedTest
    @EnumSource(Language.class)
    void searchPage(Language lang) throws Exception {
        ResultActions result = mockMvc.perform(
                get("/search").header(HttpHeaders.ACCEPT_LANGUAGE, lang)
            ).andDo(print());
        result.andExpect(status().isOk())
            .andExpect(
                content().string(containsString("<option value=\"any\""))
            )
            .andExpect(
                xpath("//select[@id='wordClass-type-hidden-options-entity_name']").exists()
            );
        testLocalization(result, lang);
        String emptyWordClass = getWordClassWithoutSubtypes();
        if (emptyWordClass != null) {
            result.andExpect(
                xpath(
                    String.format(
                        "//select[@id='wordClass-type-hidden-options-%s']",
                        emptyWordClass
                    )
                ).doesNotExist()
            ).andExpect(
                xpath(
                    String.format(
                        "//option[@value='%s']", emptyWordClass
                    )
                ).exists()
            );
        }
    }

    @Test
    void searchPageSelectForm_default() throws Exception {
        ResultActions result = mockMvc.perform(
            get("/search")
        ).andDo(print());
        var elemClasses = List.of(
            List.of(
                String.format(
                    "//button[@id='toggle-%s-search-form-button']/span[1]/@class",
                    defaultForm
                ), "icon expanded"
            ),
            List.of(
                String.format(
                    "//div[@id='%s-search-collapsible']/@class",
                    defaultForm
                ), "collapse show"
            ),
            List.of(
                String.format(
                    "//button[@id='toggle-%s-search-form-button']/span[1]/@class",
                    "sentence"
                ), "icon collapsed"
            ),
            List.of(
                String.format(
                    "//div[@id='%s-search-collapsible']/@class",
                    "sentence"
                ), "collapse"
            )
        );
        for (List<String> item : elemClasses) {
            result.andExpect(
                xpath(item.get(0)).string(item.get(1))
            );
        }
    }

    @Test
    void searchPageSelectForm_byParam() throws Exception {
        ResultActions result = mockMvc.perform(
            get("/search").param("sentence", "")
        ).andDo(print());
        result.andExpect(
            xpath(
                "//button[@id='toggle-dict-search-form-button']/span[1]/@class"
            ).string("icon collapsed")
        ).andExpect(
            xpath(
                "//button[@id='toggle-dict-search-form-button']/@aria-expanded"
            ).booleanValue(false)
        );
    }

}