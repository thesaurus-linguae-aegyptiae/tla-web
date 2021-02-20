package tla.web.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "search.lemma")
public class LemmaSearchProperties {

    private Map<String, List<String>> wordClasses;

    private List<String> sortOrders;

    private Map<String, List<String>> annotationTypes;

}
