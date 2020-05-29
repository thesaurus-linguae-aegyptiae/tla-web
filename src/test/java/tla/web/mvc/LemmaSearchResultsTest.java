package tla.web.mvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import tla.domain.dto.meta.DocumentDto;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.web.repo.TlaClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SpringBootTest
public class LemmaSearchResultsTest extends ViewTest {

    @MockBean
    private TlaClient backendClient;

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @EnumSource(Language.class)
    void countSearchResults(Language lang) throws Exception {
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
            get("/lemma/search").header(HttpHeaders.ACCEPT_LANGUAGE, lang)
        ).andDo(print()).andExpect(
            status().isOk()
        );
        testLocalization(testResponse, lang);
        testResponse.andExpect(
            xpath("//div[contains(@class,'result-list-item')]").nodeCount(dto.getResults().size())
        ).andExpect(
            xpath("//p[contains(@class,'result-page-desc')]").exists()
        ).andExpect(
            xpath("//p[contains(@class,'result-page-desc')]/b[1]/text()").string("21 - 38")
        );
    }


}