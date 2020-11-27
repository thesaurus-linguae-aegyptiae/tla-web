package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import tla.web.model.Sentence;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.TemplateModelName;
import tla.web.service.ObjectService;
import tla.web.service.SentenceService;

@Controller
@RequestMapping("/sentence")
@TemplateModelName("sentence")
public class SentenceController extends ObjectController<Sentence> {

    @Autowired
    private SentenceService service;

    @Override
    public ObjectService<Sentence> getService() {
        return service;
    }

    @Override
    protected Model extendSingleObjectDetailsModel(Model model, ObjectDetails<Sentence> container) {
        return model;
    }

}