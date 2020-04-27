package tla.web.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tla.web.model.ObjectDetails;
import tla.web.model.ThsEntry;
import tla.web.model.ui.BreadCrumb;
import tla.web.service.ThsService;

@Controller
@RequestMapping("/thesaurus")
public class ThsController {

    @Autowired
    private ThsService thsService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getDetails(Model model, @PathVariable String id) {
        ObjectDetails<ThsEntry> container = thsService.get(id);
        ThsEntry ths = container.getObject();
        model.addAttribute(
            "breadcrumbs",
            List.of(
                BreadCrumb.of("/", "menu_global_home"),
                BreadCrumb.of("/search", "menu_global_search"),
                BreadCrumb.of("caption_details_ths")
            )
        );
        model.addAttribute("obj", ths);
        model.addAttribute("related", container.getRelatedObjects());
        return "ths/details";
    }


}