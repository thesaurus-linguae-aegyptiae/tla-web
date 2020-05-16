package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
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
@AutoConfigureMockMvc

public class LemmaDetailsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TlaClient backendClient;

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

    private ResultActions makeDetailsRequest(String id) throws Exception {
        return mockMvc.perform(
            get("/lemma/" + id)
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testLemmaDetails_containingExternalReferenceWithNoProviderConfigured() throws Exception {
        final String id = "44130";
        respondToDetailsRequestWithLemma(id);
        // There is no 'vega' provider in the application properties but vega external reference should be displayed regardlesz
        ResultActions testResponse = makeDetailsRequest(id);
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
        ResultActions testResponse = makeDetailsRequest(id);
        // check if POS fragments get compiled correctly
        testResponse.andExpect(
            xpath("//div[@id='lemma-property-part-of-speech']").exists()
        ).andExpect(
            xpath("//span[@id='lemma-pos']").exists()
        );
    }

    @Test
    void testLemmaDetails_demotic() throws Exception {
        final String id = "d1315";
        respondToDetailsRequestWithLemma(id);
        ResultActions testResponse = makeDetailsRequest(id);
        testResponse.andExpect(
            xpath("//div[@id='lemma-property-hieroglyphs']").doesNotExist()
        );
    }
}