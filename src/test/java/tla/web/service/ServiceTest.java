package tla.web.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tla.web.model.Annotation;
import tla.web.model.Lemma;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.web.model.ThsEntry;
import tla.web.repo.TlaClient;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.DocumentDto;

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
    void lemmaService() {
        SingleDocumentWrapper<DocumentDto> dto = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/lemma/details/31610.json",
            SingleDocumentWrapper.class
        );
        ObjectDetails<TLAObject> objectDetails = ObjectDetails.from(dto);
        assertTrue(objectDetails.getObject() instanceof Lemma);
        ObjectDetails<Lemma> lemmaDetails = new ObjectDetails<Lemma>(
            (Lemma) objectDetails.getObject(),
            objectDetails.getRelatedObjects()
        );
        assertNotNull(lemmaDetails);
        List<Annotation> annotations = lemmaService.extractAnnotations(lemmaDetails);
        assertAll("test lemma annotations extraction",
            () -> assertNotNull(annotations),
            () -> assertEquals(1, annotations.size()),
            () -> assertNotNull(annotations.get(0).getBody())
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void thsService() {
        when(backend.retrieveObject(any(), anyString())).thenReturn(
            tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/ths/details/KQY2F5SJVBBN7GRO5WUXKG5M6M.json",
            SingleDocumentWrapper.class
        )
        );
        assertTrue(
            backend.retrieveObject(ThsEntry.class, "KQY2F5SJVBBN7GRO5WUXKG5M6M") instanceof SingleDocumentWrapper,
            "check if mockup backend client works"
        );
        ObjectDetails<ThsEntry> details = thsService.get("KQY2F5SJVBBN7GRO5WUXKG5M6M");
        assertAll("test service method for thesaurus entry details retrieval",
            () -> assertNotNull(details.getObject())
        );
    }

}