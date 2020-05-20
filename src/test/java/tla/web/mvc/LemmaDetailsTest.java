package tla.web.mvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.XpathResultMatchers;

import tla.domain.dto.DocumentDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.Lemma;
import tla.web.repo.TlaClient;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

@SpringBootTest

public class LemmaDetailsTest extends ViewTest {

    @MockBean
    private TlaClient backendClient;

    private static final String[] EXPECT_TOP_LEVEL_ELEM_IDS = {
        "lemma-property-dict",
        "lemma-property-part-of-speech",
        "external-references",
        "translations",
        "bibliography"
    };

    @SuppressWarnings("unchecked")
    private SingleDocumentWrapper<DocumentDto> lemmaDetails(String id) throws Exception {
        return tla.domain.util.IO.loadFromFile(
            String.format(
                "src/test/resources/sample/data/lemma/details/%s.json",
                id
            ),
            SingleDocumentWrapper.class
        );
    }

    private void respondToDetailsRequestWithLemma(String id) throws Exception {
        when(backendClient.retrieveObject(Lemma.class, id)).thenReturn(lemmaDetails(id));
    }

    private ResultActions makeDetailsRequest(String id, String lang) throws Exception {
        return mockMvc.perform(
            get("/lemma/" + id).header(HttpHeaders.ACCEPT_LANGUAGE, lang)
        ).andDo(print()).andExpect(status().isOk());
    }

    private void testBasicStructure(ResultActions testResponse, String lang) throws Exception {
        for (String id : EXPECT_TOP_LEVEL_ELEM_IDS) {
            testResponse.andExpect(
                xpath("//div[@id='details-content']/div[@id='" + id + "']").exists()
            );
        }
        testLocalization(testResponse, lang);
    }

    @Test
    void testLemmaDetails_containingExternalReferenceWithNoProviderConfigured() throws Exception {
        final String id = "44130";
        respondToDetailsRequestWithLemma(id);
        // There is no 'vega' provider in the application properties but vega external reference should be displayed regardlesz
        ResultActions testResponse = makeDetailsRequest(id, "en");
        testBasicStructure(testResponse, "en");
        XpathResultMatchers vega = xpath("//div[@id='external-references-vega']/span[contains(@class,'external-reference-provider')]/text()");
        testResponse.andExpect(
            vega.exists()
        ).andExpect(
            vega.nodeCount(1)
        ).andExpect(
            vega.string("VégA")
        );
    }

    @Test
    void testLemmaDetails_hieratic() throws Exception {
        final String id = "31610";
        respondToDetailsRequestWithLemma(id);
        ResultActions testResponse = makeDetailsRequest(id, "en");
        // check if POS fragments get compiled correctly
        testBasicStructure(testResponse, "en");
        testResponse.andExpect(
            xpath("//div[@id='lemma-property-part-of-speech']").exists()
        ).andExpect(
            xpath("//span[@id='lemma-pos']").exists()
        ).andExpect(
            xpath("//div[@id='details-content']/div[@id='lemma-property-attestations']").exists()
        );
    }

    @Test
    void testLemmaDetails_demotic() throws Exception {
        final String id = "d1315";
        respondToDetailsRequestWithLemma(id);
        ResultActions testResponse = makeDetailsRequest(id, "en");
        testBasicStructure(testResponse, "en");
        testResponse.andExpect(
            xpath("//div[@id='lemma-property-hieroglyphs']").doesNotExist()
        );
    }
}
