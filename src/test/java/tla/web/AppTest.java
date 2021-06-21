package tla.web;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import tla.web.config.ApplicationProperties;
import tla.web.config.DetailsProperties;
import tla.web.config.LemmaSearchProperties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppTest {

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private LemmaSearchProperties lemmaSearchConf;

    @Autowired
    private DetailsProperties detailsProperties;

    @Autowired
    private Environment env;

    @Test
    void properties() {
        assertAll("applicationproperties should be available to context",
            () -> assertNotNull(properties, "application properties should not be null"),
            () -> assertNotNull(properties.getName(), "application title should be set"),
            () -> assertTrue(properties.getL10n().contains("en"), "supported languages set")
        );
        assertAll("search properties should be available to context",
            () -> assertNotNull(lemmaSearchConf, "lemma search configurations should not be null"),
            () -> assertNotNull(lemmaSearchConf.getWordClasses(), "searchable word classes"),
            () -> assertTrue(lemmaSearchConf.getWordClasses().get("adjective").size() > 1, "adjective word class has subtypes"),
            () -> assertTrue(lemmaSearchConf.getWordClasses().containsKey("interjection"), "word class interjection present"),
            () -> assertEquals(0, lemmaSearchConf.getWordClasses().get("interjection").size(), "interjection word classes has no subtypes")
        );
        assertAll("domain model object details views properties loaded and available",
            () -> assertNotNull(detailsProperties, "details properties"),
            () -> assertFalse(detailsProperties.isEmpty(), "details properties present"),
            () -> assertTrue(detailsProperties.containsKey("lemma"), "lemma details properties present"),
            () -> assertFalse(
                detailsProperties.get("lemma").getPassportProperties().isEmpty(),
                "passport field list in lemma details properties"
            )
        );
        assertNull(env.getProperty("spring.thymeleaf.cache"), "thymeleaf uses cache in default profile");
        assertEquals(0, env.getActiveProfiles().length, "this test should run in default profile");
    }

}
