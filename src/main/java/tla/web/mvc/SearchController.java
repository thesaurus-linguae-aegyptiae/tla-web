package tla.web.mvc;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tla.domain.command.LemmaSearch;
import tla.domain.command.SentenceSearch;
import tla.domain.command.SentenceSearch.TokenSpec;
import tla.domain.model.Language;
import tla.domain.model.Script;
import tla.web.config.LemmaSearchProperties;
import tla.web.model.ui.BreadCrumb;
import tla.web.model.ui.SearchFormExpansionState;

@Slf4j
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private LemmaSearchProperties lemmaSearchConf;

    @Value("${search.config.default}")
    private String defaultForm;

    public static final List<String> SEARCH_FORMS = List.of("dict", "sentence");

    @ModelAttribute("allScripts")
    public Script[] getAllScripts() {
        return LemmaController.SEARCHABLE_SCRIPTS; // TODO
    }

    @ModelAttribute("allTranslationLanguages")
    public Language[] getAllTranslationLanguages() {
        return LemmaController.SEARCHABLE_TRANSLATION_LANGUAGES; // TODO
    }

    @ModelAttribute("wordClasses")
    public Map<String, List<String>> getWordClasses() {
        return lemmaSearchConf.getWordClasses();
    }

    @ModelAttribute("lemmaAnnotationTypes")
    public Map<String, List<String>> getLemmaAnnotationTypes() {
        return lemmaSearchConf.getAnnotationTypes();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String mainSearchPage(
        @ModelAttribute("lemmaSearchForm") LemmaSearch lemmaForm,
        @ModelAttribute("sentenceSearchForm") SentenceSearch sentenceForm,
        @RequestParam MultiValueMap<String, String> params,
        Model model
    ) {
        model.addAttribute(
            "breadcrumbs",
            List.of(
                BreadCrumb.of("/", "menu_global_home"),
                BreadCrumb.of("/search", "menu_global_search")
            )
        );
        if (sentenceForm.getTokens() == null || sentenceForm.getTokens().isEmpty()) {
            sentenceForm.setTokens(
                List.of(new TokenSpec(), new TokenSpec())
            );
        }
        model.addAttribute(
            "forms", initFormExpansionStates(params)
        );
        return "search";
    }

    /**
     * Determine how to determine whether a search form should initially be expanded.
     */
    private Function<String, SearchFormExpansionState> expandFormExpression(MultiValueMap<String, String> params) {
        if (SEARCH_FORMS.stream().anyMatch(
            key -> params.containsKey(key)
        )) {
            return key -> new SearchFormExpansionState(
                key, params.containsKey(key)
            );
        } else {
            return key -> new SearchFormExpansionState(
                key, key.equals(defaultForm)
            );
        }
    }

    /**
     * Determine the initial expansion states of collapsible search forms based on URL parameters
     * or the default search mode selected via the application setting <code>search.config.default</code>.
     */
    protected List<SearchFormExpansionState> initFormExpansionStates(MultiValueMap<String, String> params) {
        return SEARCH_FORMS.stream().map(
            this.expandFormExpression(params)
        ).collect(
            Collectors.toList()
        );
    }

    /**
     * This is for testing obviously.
     *
     * @throws Exception
     */
    @RequestMapping(value = "fail", method = RequestMethod.GET)
    public void failWithError(Model model) throws Exception {
        throw new Exception("something went wrong!");
    }

}