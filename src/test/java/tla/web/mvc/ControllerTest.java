package tla.web.mvc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.Lemma;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.ui.CorpusPathSegment;

@SpringBootTest
public class ControllerTest {

    @Autowired
    private LemmaController lemmaController;

    @Test
    void controllerRegistry() {
        var route = ObjectController.getRequestMapping("BTSLemmaEntry");
        assertAll("test view controller domain model mapping",
            () -> assertNotNull(route, "request mapping for eclass"),
            () -> assertTrue(ObjectController.eClassRequestMappings.containsKey("BTSLemmaEntry"), "lemma eclass route registered"),
            () -> assertEquals("/thesaurus/ID", ObjectController.getDetailsViewPath("BTSThsEntry", "ID"), "produces object details page URL path"),
            () -> assertTrue(ObjectController.eClassRequestMappings.size() > 1, "at least 2 routes registered") // TODO this is kinda messy because it might vary depending on in which order the other controller tests are run
        );
    }

    @Test
    @SuppressWarnings({"unchecked"})
    @DisplayName("check lemma details preparation")
    void lemmaDetails() throws Exception {
        SingleDocumentWrapper<?> dto = tla.domain.util.IO.loadFromFile(
            "src/test/resources/sample/data/lemma/details/151410.json",
            SingleDocumentWrapper.class
        );
        ObjectDetails<Lemma> container = (ObjectDetails<Lemma>) ObjectDetails.from(dto);
        Map<String, List<CorpusPathSegment>> passport = lemmaController.getPassportPropertyValues(container);
        assertAll("lemma details properties ready for UI",
            () -> assertNotNull(passport),
            () -> assertFalse(passport.isEmpty()),
            () -> assertEquals(2, passport.size()),
            () -> assertEquals(
                "III 5",
                passport.get("lemma_main_group_nominal_schenkel").get(0).getLabel()
            )
        );
    }

}