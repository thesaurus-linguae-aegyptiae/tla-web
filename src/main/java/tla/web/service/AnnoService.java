package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.meta.ModelClass;

@Service
@ModelClass(tla.web.model.Annotation.class)
public class AnnoService extends ObjectService<tla.web.model.Annotation> {

    public String getLabel(tla.web.model.Annotation object) {
        return object.getId();
    }

}