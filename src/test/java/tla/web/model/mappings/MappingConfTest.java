package tla.web.model.mappings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tla.web.model.Sentence;
import tla.web.repo.TlaClient;

@SpringBootTest
public class MappingConfTest {

    @Autowired
    private ModelMapper mapper;

    @Test
    void registryTest() {
        assertAll("see whether model mapping registry is set up",
            () -> assertNotNull(MappingConfig.getModelClass("BTSLemmaEntry"), "lemma eclass mapping"),
            () -> assertEquals(Sentence.class, MappingConfig.getModelClass("BTSSentence"), "sentence eclass mapping"),
            () -> assertEquals(
                "lemma",
                TlaClient.getBackendPathPrefix(MappingConfig.getModelClass("BTSLemmaEntry"))
            ),
            () -> assertFalse(MappingConfig.getRegisteredModelClasses().isEmpty(), "model class registry empty"),
            () -> assertNotNull(mapper, "model mapper initialized")
        );
        //ModelMapper mapper = context.getBean(ModelMapper.class);
    }

}
