package tla.web.model.parts;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalReference {

    @JsonAlias("id")
    private String value;
    private String type;
    private String href;
    
}
