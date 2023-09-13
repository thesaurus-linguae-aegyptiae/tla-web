package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.meta.ModelClass;

@Service
@ModelClass(tla.web.model.Comment.class)
public class CommentService extends ObjectService<tla.web.model.Comment> {

    public String getLabel(tla.web.model.Comment object) {
        return object.getId();
    }

}