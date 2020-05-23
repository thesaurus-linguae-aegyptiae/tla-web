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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    private ResultActions makeDetailsRequest(String id, Language lang) throws Exception {
        return mockMvc.perform(
            get("/lemma/" + id).header(HttpHeaders.ACCEPT_LANGUAGE, lang)
        ).andDo(print()).andExpect(status().isOk());
    }

    private void testBasicStructure(ResultActions testResponse, Language lang) throws Exception {
        for (String id : EXPECT_TOP_LEVEL_ELEM_IDS) {
            testResponse.andExpect(
                xpath("//div[@id='details-content']/div[@id='" + id + "']").exists()
            );
        }
        testLocalization(testResponse, lang);
    }

    @ParameterizedTest
    @EnumSource(Language.class)
    void testLemmaDetails_containingExternalReferenceWithNoProviderConfigured(Language lang) throws Exception {
        final String id = "44130";
        respondToDetailsRequestWithLemma(id);
        // There is no 'vega' provider in the application properties but vega external reference should be displayed regardlesz
        ResultActions testResponse = makeDetailsRequest(id, lang);
        testBasicStructure(testResponse, lang);
        XpathResultMatchers vega = xpath("//div[@id='external-references-vega']/span[contains(@class,'external-reference-provider')]/text()");
        testResponse.andExpect(
            vega.exists()
        ).andExpect(
            vega.nodeCount(1)
        ).andExpect(
            vega.string("VégA")
        );
    }

    @ParameterizedTest
    @EnumSource(Language.class)
    void testLemmaDetails_hieratic(Language lang) throws Exception {
        final String id = "31610";
        respondToDetailsRequestWithLemma(id);
        ResultActions testResponse = makeDetailsRequest(id, lang);
        // check if POS fragments get compiled correctly
        testBasicStructure(testResponse, lang);
        testResponse.andExpect(
            xpath("//div[@id='lemma-property-part-of-speech']").exists()
        ).andExpect(
            xpath("//div[@id='lemma-property-part-of-speech']//span[@id='object-type-subtype']").exists()
        ).andExpect(
            xpath("//div[@id='details-content']/div[@id='lemma-property-attestations']").exists()
        ).andExpect(
            xpath("//div[contains(@class,'bibliography')]/p/span/span[contains(@class,'bibliographic-reference')]").nodeCount(3)
		).andExpect(
            xpath("//div[contains(@class,'bibliography')]/p/span/span[contains(@class,'bibliographic-reference')]/text()").string(
                "Wb 1, 130.1-5"
            )
        );
    }

    @ParameterizedTest
    @EnumSource(Language.class)
    void testLemmaDetails_demotic(Language lang) throws Exception {
        final String id = "d1315";
        respondToDetailsRequestWithLemma(id);
        ResultActions testResponse = makeDetailsRequest(id, lang);
        testBasicStructure(testResponse, lang);
        testResponse.andExpect(
            xpath("//div[@id='lemma-property-hieroglyphs']").doesNotExist()
        );
    }
}
