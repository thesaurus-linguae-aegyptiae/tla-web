package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import tla.web.config.SearchProperties;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map.Entry;

import static org.hamcrest.Matchers.*;

import org.springframework.http.HttpHeaders;

import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchFormTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SearchProperties searchConf;

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

    @Test
    void searchPage() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/search").header(HttpHeaders.ACCEPT_LANGUAGE, "en")
            ).andDo(print());
        result.andExpect(status().isOk())
            .andExpect(
                content().string(containsString("<option value=\"any\""))
            )
            .andExpect(
                content().string(not(containsString("_en??")))
            )
            .andExpect(
                xpath("//select[@id='word-class-type-hidden-options-entity_name']").exists()
            );
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