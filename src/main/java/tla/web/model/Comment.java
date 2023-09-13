package tla.web.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.domain.dto.CommentDto;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.TLADTO;
import tla.web.model.mappings.Util;
import tla.web.model.meta.BTSObject;

@Getter
@Setter
@NoArgsConstructor
@BTSeClass("BTSComment")
@TLADTO(CommentDto.class)
public class Comment extends BTSObject {

    @Getter(AccessLevel.NONE)
    private String body;

    /**
     * Escape markup before returning value.
     */
    @Override
    public String getName() {
        return Util.escapeMarkup(super.getName());
    }
    
    public String getBody() {
       //return this.body;
       return Util.escapeMarkup(this.body);
    }

}