package tla.web.mvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

import tla.domain.dto.extern.SearchResultsWrapper;
import tla.web.config.SentenceSearchProperties;
import tla.web.model.Sentence;
import tla.web.repo.TlaClient;

@SpringBootTest
public class SentenceSearchTest extends ViewTest {

    @MockBean
    private TlaClient backend;

    @Autowired
    private MessageSource messages;

    @Autowired
    private SentenceSearchProperties searchProperties;

    @Test
    void testSearchProperties() {
        assertAll("test loaded sentence search configuration",
            () -> assertTrue(searchProperties.getHideableProperties().containsKey("transcription")),
            () -> assertTrue(searchProperties.getHideableProperties().get("transcription").isEmpty())
        );
    }

    /**
     * Uses data file at <code>src/test/resources/sample/data/sentence/search/{datafile}</code>
     * to simulate a sentence search.
     * Return the data loaded from that file.
     */
    @SuppressWarnings("unchecked")
    SearchResultsWrapper<?> mockSearch(String datafile) throws Exception {
        var dto = tla.domain.util.IO.loadFromFile(
            String.format(
                "src/test/resources/sample/data/sentence/search/%s", datafile
            ),
            SearchResultsWrapper.class
        );
        when(
            backend.searchObjects(
                eq(Sentence.class), any(), anyInt()
            )
        ).thenReturn(
            dto
        );
        return dto;
    }


    @Test
    void sentenceSearchResultsViewModelTest() throws Exception {
        var dto = mockSearch("occurrences-145700.json");
        var response = mockMvc.perform(
            get("/sentence/search").param("tokens[0].lemma.id", new String[]{"145700"})
        ).andDo(print());

        testLocalization(response);

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

    @Test
    void sentenceSearchResultsLinksTest() throws Exception {
        var dto = mockSearch("occurrences-10070.json");
        var response = mockMvc.perform(
            get(
                URI.create("/sentence/search?tokens[0].lemma.id=10070&lang=de")
            )
        ).andDo(print());

        assertTrue(dto.getPage().isFirst(), "first results page");

        var pagination = "//nav/ul[contains(@class, 'pagination')]";
        var pagelink = pagination + "/li[@class='page-item']/a[@class='page-link']";
        response.andExpect(
            xpath(pagelink).exists()
        );
        response.andExpect(
            xpath(pagelink + "/@href").string(
                containsString("page=2")
            )
        ).andExpect(
            xpath(pagelink + "/@href").string(
                containsString("tokens[0].lemma.id")
            )
        );

        var resultsDesc = "//div[contains(@class, 'result-page-desc')]";
        response.andExpect(
            xpath(resultsDesc + "/b[2]/text()").string("32")
        ).andExpect(
            xpath(resultsDesc + "/span[3]/text()").string(
                messages.getMessage("result_page_description_right_sentence", null, Locale.GERMAN)
            )
        );
    }

    @Test
    void testSideBar() throws Exception {
        mockSearch("occurrences-145700.json");
        var response = mockMvc.perform(
            get(
                URI.create("/sentence/search?tokens[0].lemma.id=10070&lang=de")
            )
        ).andDo(print());
        response.andExpect(
            xpath(
                "//div[@class='hide-properties-buttons']/div[@class='indented-buttons']/button[@id='hide-property-button-translations-translation-de']"
            ).exists()
        );
    }

}