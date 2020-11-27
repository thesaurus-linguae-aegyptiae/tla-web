package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.CorpusObject;
import tla.web.model.mappings.Util;
import tla.web.model.meta.ModelClass;

@Service
@ModelClass(CorpusObject.class)
public class CorpusObjectService extends ObjectService<CorpusObject> {

    @Override
    public String getLabel(CorpusObject object) {
        return Util.escapeMarkup(
            object.getName()
        );
    }

}