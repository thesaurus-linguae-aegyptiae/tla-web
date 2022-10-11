package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tla.domain.command.LemmaSearch;
import tla.domain.command.TextSearch;
import tla.domain.command.CorpusObjectSearch;
import tla.web.model.CorpusObject;
import tla.web.model.meta.TemplateModelName;
import tla.web.service.CorpusObjectService;
import tla.web.service.ObjectService;

@Controller
@RequestMapping("/object")
@TemplateModelName("object")
public class CorpusObjectController extends ObjectController<CorpusObject, CorpusObjectSearch> {

    @Autowired
    private CorpusObjectService service;

    @Override
    public ObjectService<CorpusObject> getService() {
        return service;
    }
    @Override
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String getSearchResultsPage(
        @ModelAttribute("objectSearchForm") CorpusObjectSearch form,
        @RequestParam(defaultValue = "1") String page,
        @RequestParam MultiValueMap<String, String> params,
        Model model
    ) {
        return super.getSearchResultsPage(form, page, params, model);
    } 
}