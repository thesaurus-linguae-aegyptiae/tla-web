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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import tla.web.config.LemmaSearchProperties;

@SpringBootTest
public class SearchFormTest extends ViewTest {

    @Autowired
    private LemmaSearchProperties searchConf;

    @Test
    void root() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isNotFound());
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
                xpath("//select[@id='word-class-type-hidden-options-entity_name']").exists()
            );
        testLocalization(result, lang);
        String emptyWordClass = getWordClassWithoutSubtypes();
        if (emptyWordClass != null) {
            result.andExpect(
                xpath(
                    String.format(
                        "//select[@id='word-class-type-hidden-options-%s']",
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


}