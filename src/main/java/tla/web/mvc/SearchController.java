package tla.web.mvc;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private LemmaSearchProperties lemmaSearchConf;

    @ModelAttribute("allScripts")
    public Script[] getAllScripts() {
        return LemmaController.SEARCHABLE_SCRIPTS;
    }

    @ModelAttribute("allTranslationLanguages")
    public Language[] getAllTranslationLanguages() {
        return LemmaController.SEARCHABLE_TRANSLATION_LANGUAGES;
    }

    @ModelAttribute("wordClasses")
    public Map<String, List<String>> getWordClasses() {
        return lemmaSearchConf.getWordClasses();
    }

    @ModelAttribute("lemmaAnnotationTypes")
    public Map<String, List<String>> getLemmaAnnotationTypes() {
        return lemmaSearchConf.getAnnotationTypes();
    }

    /**
     * Simple single-purpose POJO for initializing search form collapsible
     * expansion states (which search form currently has the focus)
     */
    @Getter
    @AllArgsConstructor
    public static class SearchFormExpansionState {
        private String key;
        private boolean expanded;
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
            "forms",
            List.of("dict", "sentence").stream().map(
                key -> new SearchFormExpansionState(
                    key, params.containsKey(key)
                )
            ).collect(Collectors.toList())
        );
        return "search";
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