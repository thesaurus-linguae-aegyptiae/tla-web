package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tla.domain.command.LemmaSearch;
import tla.web.model.ThsEntry;
import tla.web.model.meta.TemplateModelName;
import tla.web.service.ObjectService;
import tla.web.service.ThsService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.context.WebContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    
    @RequestMapping(value = "/{id}/name", method = RequestMethod.GET)
    public ResponseEntity<String> getObjectName(@PathVariable String id) {
        // Retrieve the object by ID
        ThsEntry thsEntry = thsService.findById(id);
        
        if (thsEntry != null) {
            // Retrieve the name from the object and return it
            String name ="Name"+ thsEntry.getId();
            return ResponseEntity.ok(name);
        } else {
            // Object not found, return an error response
            return ResponseEntity.notFound().build();
        }
    }

}
