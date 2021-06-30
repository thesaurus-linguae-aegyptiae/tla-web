package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tla.domain.command.LemmaSearch;
import tla.web.model.ThsEntry;
import tla.web.model.meta.TemplateModelName;
import tla.web.service.ObjectService;
import tla.web.service.ThsService;

@Controller
@TemplateModelName("ths")
@RequestMapping("/thesaurus")
public class ThsController extends HierarchicObjectController<ThsEntry, LemmaSearch> {

    @Autowired
    private ThsService thsService;

    @Override
    public ObjectService<ThsEntry> getService() {
        return thsService;
    }

}