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
            () -> assertEquals(1, ObjectController.eClassRequestMappings.size(), "1 route registered"),
            () -> assertEquals("/thesaurus/ID", ObjectController.getDetailsViewPath("BTSThsEntry", "ID"), "produces object details page URL path"),
            () -> assertEquals(2, ObjectController.eClassRequestMappings.size(), "2 routes registered")
        );
    }

}