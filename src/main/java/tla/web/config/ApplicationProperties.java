package tla.web.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "tla")
public class ApplicationProperties {

    private String name;

    private String baseUrl;

    private String backendURL = "http://tladev.bbaw.de:8080";

    private Map<String, LinkFormatter> linkFormatters = new HashMap<>();

}
