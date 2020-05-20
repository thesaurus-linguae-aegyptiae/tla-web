package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
public class ViewTest {

    public static enum Language {
        en;
    }

    @Autowired
    protected MockMvc mockMvc;

    void testLocalization(ResultActions testResponse, Language lang) throws Exception {
        testResponse.andExpect(
            content().string(
                not(containsString(
                    String.format("_%s??", lang)
                ))
            )
        );
    }

}