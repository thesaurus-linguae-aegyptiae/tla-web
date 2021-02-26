package tla.web.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.Sentence;
import tla.web.repo.TlaClient;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class SentenceDetailsTest extends ViewTest {

    @MockBean
    private TlaClient backendClient;

    @Test
    void sentenceNotFoundProduces404Error() throws Exception {
        mockMvc.perform(
            get("/sentence/XXX")
        ).andExpect(
            status().isOk()
        ).andExpect(
            xpath("//h3[@id='error-code']/text()").string(endsWith("404"))
        ).andDo(
            print()
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void sentenceDetailsTest() throws Exception {
        when(
            backendClient.retrieveObject(
                eq(Sentence.class), eq("IBcCBpKz4FWJo0yOhxfTNEhx5J0")
            )
        ).thenReturn(
            tla.domain.util.IO.loadFromFile(
                "src/test/resources/sample/data/sentence/details/IBcCBpKz4FWJo0yOhxfTNEhx5J0.json",
                SingleDocumentWrapper.class
            )
        );
        var response = mockMvc.perform(
            get("/sentence/IBcCBpKz4FWJo0yOhxfTNEhx5J0")
        ).andExpect(
            xpath("//div[@class=\"object-path\"]").nodeCount(1)
        ).andExpect(
            xpath("//div[@class=\"object-path\"]/div[@class=\"object-path-element\"]").nodeCount(5)
        );
        testLocalization(response, "en");
    }

}