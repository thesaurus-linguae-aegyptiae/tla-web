package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tla.domain.command.LemmaSearch;
import tla.web.model.CorpusObject;
import tla.web.model.meta.TemplateModelName;
import tla.web.service.CorpusObjectService;
import tla.web.service.ObjectService;

@Controller
@RequestMapping("/object")
@TemplateModelName("object")
public class CorpusObjectController extends ObjectController<CorpusObject, LemmaSearch> {

    @Autowired
    private CorpusObjectService service;

    @Override
    public ObjectService<CorpusObject> getService() {
        return service;
    }

}