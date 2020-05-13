package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.XpathResultMatchers;

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

    @Test
    @SuppressWarnings("unchecked")
    void testLemmaDetails_containingExternalReferenceWithNoProviderConfigured() throws Exception {
        when(backendClient.retrieveObject(Lemma.class, "44130")).thenReturn(
            tla.domain.util.IO.loadFromFile(
                "src/test/resources/sample/data/lemma/details/44130.json",
                SingleDocumentWrapper.class
            )
        );
        // There is no 'vega' provider in the application properties but vega external reference should be displayed regardlesz
        ResultActions testResponse = mockMvc.perform(
            get("/lemma/44130")
        ).andDo(print()).andExpect(status().isOk());
        XpathResultMatchers vega = xpath("//div[@id='external-references-vega']/div/span[@class='external-reference-provider']/text()");
        testResponse.andExpect(
            vega.exists()
        ).andExpect(
            vega.nodeCount(1)
        ).andExpect(
            vega.string("VégA")
        );
    }

}