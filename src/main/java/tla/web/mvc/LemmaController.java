package tla.web.mvc;

import tla.web.model.Lemma;
import tla.web.model.ObjectDetails;
import tla.web.model.ui.BreadCrumb;
import tla.web.service.LemmaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LemmaController {

    @Autowired
    private LemmaService lemmaService;

    @RequestMapping(value = "/lemma/{id}", method = RequestMethod.GET)
    public String getLemmaDetails(Model model, @PathVariable String id) {
        log.debug("Compile lemma detail view data for lemma {}", id);
        ObjectDetails<Lemma> container = lemmaService.getLemma(id);
        Lemma lemma = container.getObject();
        model.addAttribute(
            "breadcrumbs",
            List.of(
                BreadCrumb.of("/", "menu_global_home"),
                BreadCrumb.of("/search", "menu_global_search"),
                BreadCrumb.of("caption_details_lemma")
            )
        );
        model.addAttribute("obj", lemma);
        model.addAttribute("hieroglyphs", lemmaService.extractHieroglyphs(lemma));
        model.addAttribute("bibliography", lemmaService.extractBibliography(lemma));
        model.addAttribute("related", container.getRelatedObjects());
        model.addAttribute("annotations", lemmaService.extractAnnotations(container));
        return "lemma/details";
    }

}
