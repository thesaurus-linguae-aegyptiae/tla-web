package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.Text;
import tla.web.model.mappings.Util;
import tla.web.model.meta.ModelClass;

@Service
@ModelClass(Text.class)
public class TextService extends ObjectService<Text> {

    @Override
    public String getLabel(Text object) {
        return Util.escapeMarkup(
            object.getName()
        );
    }
}
