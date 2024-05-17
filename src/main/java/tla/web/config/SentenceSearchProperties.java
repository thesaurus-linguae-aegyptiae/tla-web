package tla.web.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import tla.web.model.Sentence;
import tla.web.model.meta.ModelClass;

@Getter
@Setter
@Component
@ModelClass(Sentence.class)
@ConfigurationProperties(prefix = "search.sentence")
public class SentenceSearchProperties extends SearchProperties {
	
	
	private Map<String, List<String>> contextInformation;
}