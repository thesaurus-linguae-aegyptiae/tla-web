package tla.web.model.mappings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import tla.web.model.Sentence;
import tla.web.repo.TlaClient;

@SpringBootTest
public class MappingConfTest {

    @Test
    void registryTest() {
        assertAll("see whether model mapping registry is set up",
            () -> assertNotNull(MappingConfig.getModelClass("BTSLemmaEntry"), "lemma eclass mapping"),
            () -> assertEquals(Sentence.class, MappingConfig.getModelClass("BTSSentence"), "sentence eclass mapping"),
            () -> assertEquals(
                "lemma",
                TlaClient.getBackendPathPrefix(MappingConfig.getModelClass("BTSLemmaEntry"))
            )
        );
    }

}
