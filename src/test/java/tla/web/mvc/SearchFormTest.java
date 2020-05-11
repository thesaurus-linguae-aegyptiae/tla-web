package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.springframework.http.HttpHeaders;

import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchFormTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void root() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isNotFound());
    }

    @Test
    void searchPage() throws Exception {
        mockMvc.perform(
                get("/search").header(HttpHeaders.ACCEPT_LANGUAGE, "en")
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(
                content().string(containsString("<option value=\"any\""))
            )
            .andExpect(
                content().string(not(containsString("_en??")))
            );
    }


}