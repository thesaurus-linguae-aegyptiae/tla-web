package tla.web.model.parts;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExternalReference {

	
    @JsonAlias("id")
    private String value;
    private String type;
    private String href;
    
}
