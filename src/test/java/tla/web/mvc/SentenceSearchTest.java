package tla.web.mvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tla.domain.dto.extern.SearchResultsWrapper;
import tla.web.model.Sentence;
import tla.web.repo.TlaClient;

@SpringBootTest
public class SentenceSearchTest extends ViewTest {

    @MockBean
    private TlaClient backend;

    @Test
    @SuppressWarnings("unchecked")
    void sentenceSearchResultsViewModelTest() throws Exception {
        var dto = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/sentence/search/occurrences-145700.json",
            SearchResultsWrapper.class
        );
        assertEquals(3, dto.getResults().size());
        when(
            backend.searchObjects(
                eq(Sentence.class), any(), anyInt()
            )
        ).thenReturn(
            dto
        );
        var response = mockMvc.perform(
            get("/sentence/search").param("token[0].id", new String[]{"145700"})
        ).andDo(print());

        testLocalization(response, Language.en);

        response.andExpect(
            status().isOk()
        ).andExpect(
            xpath("//head/title/text()").string("Sentence Search")
        ).andExpect(
            model().attribute("searchResults", hasSize(3))
        ).andExpect(
            model().attribute("searchResults[0].paths", not(empty()))
        );
        var resultCount = dto.getResults().size();
        response.andExpect(
            xpath("//div[@class='result-list-item']").nodeCount(resultCount)
        ).andExpect(
            xpath("//div[@class='object-path']").nodeCount(resultCount)
        ).andExpect(
            xpath("//div[@class='object-path']/div[@class='object-path-element']").nodeCount(16)
        ).andExpect(
            xpath("//div[@class='object-path'][1]/div[@class='object-path-element']/a/span/text()").string("〈Pyramidentexte〉")
        );

    }

}