package tla.web.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import tla.web.model.Lemma;
import tla.web.model.meta.ModelClass;

@Getter
@Setter
@Component
@ModelClass(Lemma.class)
@ConfigurationProperties(prefix = "search.lemma")
public class LemmaSearchProperties extends SearchProperties {

    /**
     * Word classes to choose from in search form
     */
    private Map<String, List<String>> wordClasses;

    /**
     * Annotation types to choose from in search form
     */
    private Map<String, List<String>> annotationTypes;

}