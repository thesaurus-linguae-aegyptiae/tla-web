package tla.web.mvc;

import tla.domain.command.LemmaSearch;
import tla.domain.model.Language;
import tla.domain.model.Script;
import tla.web.model.Lemma;
import tla.web.model.ObjectDetails;
import tla.web.model.SearchResults;
import tla.web.model.ui.Pagination;
import tla.web.model.ui.TemplateModelName;
import tla.web.service.LemmaService;
import tla.web.service.ObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lemma")
@TemplateModelName("lemma")
public class LemmaController extends ObjectController<Lemma> {

    @Autowired
    private LemmaService lemmaService;

    public static final Script[] SEARCHABLE_SCRIPTS = {
        Script.HIERATIC,
        Script.DEMOTIC
    };

    public static final Language[] SEARCHABLE_TRANSLATION_LANGUAGES = {
        Language.DE,
        Language.EN,
        Language.FR
    };

    @Override
    public ObjectService<Lemma> getService() {
        return lemmaService;
    }

    @Override
    protected Model compileSingleObjectDetailsModel(Model model, ObjectDetails<Lemma> container) {
        model.addAttribute("bibliography", lemmaService.extractBibliography(container.getObject()));
        model.addAttribute("annotations", lemmaService.extractAnnotations(container));
        return model;
    }

    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String search(
        @ModelAttribute("lemmaSearchForm") LemmaSearch form,
        @RequestParam(defaultValue = "1") String page,
        Model model
    ) throws Exception {
        SearchResults results = lemmaService.search(form, Integer.parseInt(page));
        model.addAttribute("searchResults", results.getObjects());
        model.addAttribute("searchQuery", results.getQuery());
        model.addAttribute("page", results.getPage());
        model.addAttribute("pagination", new Pagination(results.getPage()));
        return String.format("%s/search_results", getTemplatePath());
    }

}
