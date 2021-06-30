package tla.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tla.domain.command.TextSearch;
import tla.web.model.Text;
import tla.web.model.meta.TemplateModelName;
import tla.web.service.ObjectService;
import tla.web.service.TextService;

@Controller
@RequestMapping("/text")
@TemplateModelName("text")
public class TextObjectController extends ObjectController<Text, TextSearch> {

    @Autowired
    private TextService service;

    @Override
    public ObjectService<Text> getService() {
        return service;
    }

}
