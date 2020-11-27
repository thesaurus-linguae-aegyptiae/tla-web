package tla.web.mvc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ControllerTest {

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

}