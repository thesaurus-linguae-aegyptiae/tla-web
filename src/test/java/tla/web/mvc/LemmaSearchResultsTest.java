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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SpringBootTest
public class LemmaSearchResultsTest extends ViewTest {

    @MockBean
    private TlaClient backendClient;

    /**
     * load search results transfer object from file.
     */
    @SuppressWarnings("unchecked")
    SearchResultsWrapper<DocumentDto> loadDto(String filename) throws Exception {
        return tla.domain.util.IO.loadFromFile(
            String.format(
                "src/test/resources/sample/data/lemma/search/%s",
                filename
            ),
            SearchResultsWrapper.class
        );
    }


    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @EnumSource(Language.class)
    void testSearchResults(Language lang) throws Exception {
        SearchResultsWrapper<DocumentDto> dto = loadDto("demotic_translation_de.json");
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
            xpath("//div[contains(@class,'result-page-desc')]").exists()
        ).andExpect(
            xpath("//div[contains(@class,'result-page-desc')]/b[1]/text()").string("21 - 38")
        ).andExpect(
            xpath("//div[@id='dm2434']//span[contains(@class,'subcorpus')]").exists()
        ).andExpect(
            xpath("//div[@id='dm2434']//span[contains(@class,'hieroglyphs')]").doesNotExist()
        );
    }

    @Test
    void testFulltypeLabels() throws Exception {
        SearchResultsWrapper<DocumentDto> dto = loadDto("demotic_translation_de.json");
        when(
            backendClient.lemmaSearch(any(), anyInt())
        ).thenReturn(
            dto
        );
        ResultActions testResponse = mockMvc.perform(
            get("/lemma/search").header(HttpHeaders.ACCEPT_LANGUAGE, "en")
        ).andDo(print()).andExpect(
            status().isOk()
        );
        testResponse.andExpect(
            xpath("//div[@id='dm2254']//span[contains(@class,'type-subtype')/span/text()").string("Animal Name")
        );
    }

}