package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.ThsEntry;
import tla.web.model.meta.ModelClass;

@Service
@ModelClass(ThsEntry.class)
public class ThsService extends ObjectService<ThsEntry> {

    @Override
    public String getLabel(ThsEntry object) {
        return object.getName();
    }

}