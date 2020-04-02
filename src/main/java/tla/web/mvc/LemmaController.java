package tla.web.mvc;

import tla.web.model.Lemma;
import tla.web.mvc.MvcConfig.TlaPageHeader;
import tla.web.service.LemmaService;

import java.util.Collections;

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

    @Autowired
    private TlaPageHeader pageHeader;

    @RequestMapping(value = "/lemma/{id}", method = RequestMethod.GET)
    public String getLemmaDetails(Model model, @PathVariable String id) {
        Lemma lemma = lemmaService.getLemma(id);
        model.addAttribute("obj", lemma);
        model.addAttribute("env", pageHeader);
        try {
            model.addAttribute(
                "bibliography",
                lemma.getPassport().extractProperty("bibliography.bibliographical_text_field")
            );
        } catch (Exception e) {
            log.warn("could not extract bibliography from lemma {}", id);
            model.addAttribute("bibliography", Collections.emptyList());
        }
        return "lemma/details";
    }

}
