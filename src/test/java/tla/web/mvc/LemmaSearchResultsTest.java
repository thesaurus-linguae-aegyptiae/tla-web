package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import tla.domain.dto.DocumentDto;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.web.repo.TlaClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
public class LemmaSearchResultsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TlaClient backendClient;

    @Test
    @SuppressWarnings("unchecked")
    void countSearchResults() throws Exception {
        SearchResultsWrapper<DocumentDto> dto = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/lemma/search/demotic_translation_de.json",
            SearchResultsWrapper.class
        );
        when(
            backendClient.lemmaSearch(any(), anyInt())
        ).thenReturn(
            dto
        );
        ResultActions testResponse = mockMvc.perform(
            get("/lemma/search")
        ).andDo(print()).andExpect(
            status().isOk()
        );
        testResponse.andExpect(
            xpath("//div[@class='search-result']").nodeCount(dto.getContent().size())
        );
    }


}