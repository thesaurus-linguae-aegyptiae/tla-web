package tla.web.mvc;

import tla.domain.command.LemmaSearch;
import tla.domain.model.Language;
import tla.domain.model.Script;
import tla.web.config.LemmaSearchProperties;
import tla.web.model.Lemma;
import tla.web.model.meta.ObjectDetails;
import tla.web.model.meta.SearchResults;
import tla.web.model.meta.TemplateModelName;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.ui.Pagination;
import tla.web.service.LemmaService;
import tla.web.service.ObjectService;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/lemma")
@TemplateModelName("lemma")
public class LemmaController extends ObjectController<Lemma> {

    @Autowired
    private LemmaService lemmaService;

    @Autowired
    private LemmaSearchProperties searchConfig;

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
        model.addAttribute("annotations", lemmaService.extractAnnotations(container));
        return model;
    }

    @ModelAttribute("modifySearchUrl")
    public String modifySearchUrl() {
        return ServletUriComponentsBuilder.fromCurrentRequest().replacePath("search").toUriString();
    }

    @ModelAttribute("sortOrders")
    public List<String> getSortOrders() {
        return searchConfig.getSortOrders();
    }

    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String search(
        @ModelAttribute("lemmaSearchForm") LemmaSearch form,
        @RequestParam(defaultValue = "1") String page,
        Model model
    ) throws Exception {
        SearchResults results = lemmaService.search(form, Integer.parseInt(page));
        model.addAttribute("breadcrumbs",
            List.of(
                BreadCrumb.of("/", "menu_global_home"),
                BreadCrumb.of(
                    modifySearchUrl(),
                    "menu_global_search"
                ),
                BreadCrumb.of(
                    ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", "1").toUriString(),
                    "menu_global_search_lemma"
                )
            )
        );
        model.addAttribute("searchResults", results.getObjects());
        model.addAttribute("searchQuery", results.getQuery());
        model.addAttribute("facets", results.getFacets());
        model.addAttribute("page", results.getPage());
        model.addAttribute("pagination", new Pagination(results.getPage()));
        model.addAttribute("hideableProperties", List.of("hieroglyphs", "lemma-id", "wordClass", "bibliography", "attested-timespan"));
        model.addAttribute(
            "allTranslationLanguages",
            (form.getTranscription() != null) ? form.getTranslation().getLang() : Collections.EMPTY_LIST
        );
        model.addAttribute("allScripts", form.getScript());
        return String.format("%s/search", getTemplatePath());
    }

}
