package tla.web.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Getter
@Data
@Component
@ConfigurationProperties(prefix = "tla")
@PropertySource("classpath:application.yml")
public class ApplicationProperties {

    private String name;

    private String baseUrl;

    private String backendURL = "http://localhost:8080";

    private Map<String, LinkFormatter> linkFormatters = new HashMap<>();

    private Set<String> l10n;
           
}


