package tla.web.config;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "details")
public class DetailsProperties extends HashMap<String, ObjectDetailsProperties> {

    private static final long serialVersionUID = 8168797605526907568L;

}
