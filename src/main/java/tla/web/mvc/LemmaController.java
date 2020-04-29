package tla.web.mvc;

import tla.web.model.Lemma;
import tla.web.model.ObjectDetails;
import tla.web.model.ui.TemplateModelName;
import tla.web.service.LemmaService;
import tla.web.service.ObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lemma")
@TemplateModelName("lemma")
public class LemmaController extends ObjectController<Lemma> {

    @Autowired
    private LemmaService lemmaService;

    @Override
    public ObjectService<Lemma> getService() {
        return lemmaService;
    }

    @Override
    protected Model compileSingleObjectDetailsModel(Model model, ObjectDetails<Lemma> container) {
        model.addAttribute("hieroglyphs", lemmaService.extractHieroglyphs(container.getObject()));
        model.addAttribute("bibliography", lemmaService.extractBibliography(container.getObject()));
        model.addAttribute("annotations", lemmaService.extractAnnotations(container));
        return model;
    }

}
