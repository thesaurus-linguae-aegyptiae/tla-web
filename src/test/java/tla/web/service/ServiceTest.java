package tla.web.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tla.web.model.Annotation;
import tla.web.model.Lemma;
import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.DocumentDto;

@SpringBootTest
public class ServiceTest {

    @Autowired
    private LemmaService lemmaService;

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

}