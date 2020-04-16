package tla.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tla.web.config.ApplicationProperties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppTest {

    @Autowired
    private ApplicationProperties properties;

    @Test
    void properties() {
        assertAll("applicationproperties should be available to context",
            () -> assertTrue(properties != null, "application properties should not be null"),
            () -> assertEquals("Thesaurus Linguae Aegyptiae (BETA)", properties.getName(), "application title should be set"),
            () -> assertTrue(properties.getAssets().getBootstrap() != null, "assets location configuration should be available")
        );
    }

}