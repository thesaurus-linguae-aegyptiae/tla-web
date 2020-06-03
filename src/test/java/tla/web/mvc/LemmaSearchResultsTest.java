package tla.web.mvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import tla.domain.dto.LemmaDto;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.web.repo.TlaClient;

@SpringBootTest
public class LemmaSearchResultsTest extends ViewTest {

    @MockBean
    private TlaClient backendClient;

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @EnumSource(Language.class)
    void countSearchResults(Language lang) throws Exception {
        SearchResultsWrapper<LemmaDto> dto = tla.domain.util.IO.loadFromFile(
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
            xpath("//div[contains(@class,'result-page-desc')]").exists()
        ).andExpect(
            xpath("//div[contains(@class,'result-page-desc')]/b[1]/text()").string("21 - 38")
        ).andExpect(
            xpath("//div[@id='dm2434']//span[contains(@class,'subcorpus')]").exists()
        ).andExpect(
            xpath("//div[@id='dm2434']//span[contains(@class,'hieroglyphs')]").doesNotExist()
        ).andExpect(
            xpath("//div[@id='dm529']/a[contains(@class,'review-state')]//span[contains(@class,'notok')]").exists()
        );
    }


}