package tla.web.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectDetailsProperties {

    /**
     * List of passport property paths to be made available in details view.
     */
    private List<String> passportProperties;

}
