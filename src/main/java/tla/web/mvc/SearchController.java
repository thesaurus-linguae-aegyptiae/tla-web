package tla.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tla.domain.command.LemmaSearch;
import tla.domain.model.Language;
import tla.domain.model.Script;

@Controller
@RequestMapping("/search")
public class SearchController {

    @ModelAttribute("allScripts")
    public Script[] getAllScripts() {
        return LemmaController.SEARCHABLE_SCRIPTS;
    }

    @ModelAttribute("allTranslationLanguages")
    public Language[] getAllTranslationLanguages() {
        return LemmaController.SEARCHABLE_TRANSLATION_LANGUAGES;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String mainSearchPage(Model model) {
        model.addAttribute("lemmaSearchForm", new LemmaSearch());
        return "search";
    }

}