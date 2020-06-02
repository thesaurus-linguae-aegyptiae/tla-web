package tla.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"dev"})
class DevTest {

    @Autowired
    private Environment env;

    @Test
    void properties() {
        assertEquals("false", env.getProperty("spring.thymeleaf.cache"), "thymeleaf doesn't use cache in dev profile");
    }

}
