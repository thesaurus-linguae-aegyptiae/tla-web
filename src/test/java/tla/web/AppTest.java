package tla.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import tla.web.config.ApplicationProperties;
import tla.web.config.SearchProperties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppTest {

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private SearchProperties searchConf;

    @Autowired
    private Environment env;

    @Test
    void properties() {
        assertAll("applicationproperties should be available to context",
            () -> assertNotNull(properties, "application properties should not be null"),
            () -> assertEquals("Thesaurus Linguae Aegyptiae (BETA)", properties.getName(), "application title should be set")
        );
        assertAll("search properties should be available to context",
            () -> assertNotNull(searchConf, "search configurations should not be null"),
            () -> assertNotNull(searchConf.getWordClasses(), "searchable word classes"),
            () -> assertTrue(searchConf.getWordClasses().get("adjective").size() > 1, "adjective word class has subtypes"),
            () -> assertTrue(searchConf.getWordClasses().containsKey("interjection"), "word class interjection present"),
            () -> assertEquals(0, searchConf.getWordClasses().get("interjection").size(), "interjection word classes has no subtypes")
        );
        assertNull(env.getProperty("spring.thymeleaf.cache"), "thymeleaf uses cache in default profile");
    }

}
