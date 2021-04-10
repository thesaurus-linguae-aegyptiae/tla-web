package tla.web.mvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.XpathResultMatchers;

import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.web.model.Lemma;

public class LemmaDetailsTest extends ViewTest {

    private static final String[] EXPECT_TOP_LEVEL_ELEM_IDS = {
        "lemma-property-dict",
        "lemma-property-type-subtype",
        "external-references",
        "translations",
        "bibliography"
    };

    @SuppressWarnings("unchecked")
    private SingleDocumentWrapper<AbstractDto> lemmaDetails(String id) throws Exception {
        return tla.domain.util.IO.loadFromFile(
            String.format(
                "src/test/resources/sample/data/lemma/details/%s.json",
                id
            ),
            SingleDocumentWrapper.class
        );
    }

    private void respondToDetailsRequestWithLemma(String id) throws Exception {
        when(backend.retrieveObject(Lemma.class, id)).thenReturn(lemmaDetails(id));
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
            xpath("//div[@id='lemma-property-type-subtype']").exists()
        ).andExpect(
            xpath("//div[@id='lemma-property-type-subtype']//span[@id='type-subtype']").exists()
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

    @Test
    void testModel() throws Exception {
        respondToDetailsRequestWithLemma("31610");
        ResultActions testResponse = makeDetailsRequest("31610", Language.en);
        testResponse.andExpect(
            model().attributeExists("annotations")
        ).andExpect(
            model().attributeExists("relations")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"100090"})
    void testResponseOk(String id) throws Exception {
        respondToDetailsRequestWithLemma(id);
        makeDetailsRequest(id, Language.en).andExpect(
            xpath("//div[@id='lemma-property-dict']").exists()
        );
    }

    @Test
    void testLookup() throws Exception {
        mockMvc.perform(
            get("/lemma/lookup?id=31610")
        ).andExpect(
            status().is3xxRedirection()
        ).andExpect(
            header().string(
                "Location", "/lemma/31610"
            )
        );
    }

}
