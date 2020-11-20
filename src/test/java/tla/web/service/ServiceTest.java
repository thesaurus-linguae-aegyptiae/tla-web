package tla.web.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tla.domain.command.LemmaSearch;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.DocumentDto;
import tla.web.model.Annotation;
import tla.web.model.Lemma;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.SearchResults;
import tla.web.model.meta.TLAObject;
import tla.web.model.ThsEntry;
import tla.web.repo.TlaClient;

@SpringBootTest
public class ServiceTest {

    @MockBean
    private TlaClient backend;

    @Autowired
    private LemmaService lemmaService;

    @Autowired
    private ThsService thsService;

    @Test
    @SuppressWarnings("unchecked")
    void lemmaService() throws Exception {
        SingleDocumentWrapper<DocumentDto> dto = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/lemma/details/31610.json",
            SingleDocumentWrapper.class
        );
        ObjectDetails<TLAObject> objectDetails = ObjectDetails.from(dto);
        assertTrue(objectDetails.getObject() instanceof Lemma);
        ObjectDetails<Lemma> lemmaDetails = new ObjectDetails<Lemma>(
            (Lemma) objectDetails.getObject(),
            objectDetails.getRelated()
        );
        assertNotNull(lemmaDetails);
        Map<String, List<TLAObject>> relatedObjects = lemmaDetails.extractRelatedObjects();
        assertAll("test related objects extraction",
            () -> assertEquals(dto.getDoc().getRelations().size(), relatedObjects.size(), "predicate count"),
            () -> assertEquals(2, relatedObjects.get("contains").size(), "relations of predicate 'contains'"),
            () -> assertTrue(relatedObjects.get("contains").get(0) instanceof Annotation, "reference reified to Annotation instance")
        );
        List<Annotation> annotations = lemmaService.extractAnnotations(lemmaDetails);
        assertAll("test lemma annotations extraction",
            () -> assertNotNull(annotations),
            () -> assertEquals(1, annotations.size()),
            () -> assertNotNull(annotations.get(0).getBody())
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void thsService() throws Exception {
        when(
            backend.retrieveObject(ThsEntry.class, "KQY2F5SJVBBN7GRO5WUXKG5M6M")
        ).thenReturn(
            tla.domain.util.IO.loadFromFile(
                "src/test/resources/sample/data/ths/details/KQY2F5SJVBBN7GRO5WUXKG5M6M.json",
                SingleDocumentWrapper.class
            )
        );
        assertTrue(
            backend.retrieveObject(ThsEntry.class, "KQY2F5SJVBBN7GRO5WUXKG5M6M") instanceof SingleDocumentWrapper,
            "check if mockup backend client works"
        );
        ObjectDetails<ThsEntry> details = thsService.getDetails("KQY2F5SJVBBN7GRO5WUXKG5M6M").get();
        assertAll("test service method for thesaurus entry details retrieval",
            () -> assertNotNull(details.getObject())
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void lemmaSearchResultsMapping() throws Exception {
        SearchResultsWrapper<DocumentDto> wrap = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/lemma/search/demotic_translation_de.json",
            SearchResultsWrapper.class
        );
        when(
            backend.lemmaSearch(any(), anyInt())
        ).thenReturn(
            wrap
        );
        assertNotNull(wrap);
        SearchResultsWrapper<?> dto = backend.lemmaSearch(new LemmaSearch(), 1);
        assertAll("assert that deserialization from file works",
            () -> assertNotNull(dto),
            () -> assertNotNull(dto.getResults())
        );
        SearchResults result = lemmaService.search(new LemmaSearch(), 1);
        assertAll("test mapping from DTO to search result page frontend model",
            () -> assertNotNull(result.getObjects(), "search hits not null"),
            () -> assertNotNull(result.getQuery(), "query not null"),
            () -> assertNotNull(result.getPage(),"page not null")
        );
        result.getObjects().stream().forEach(
            o -> assertTrue(o instanceof Lemma, o.getName() + " should be of class lemma")
        );
    }

}
