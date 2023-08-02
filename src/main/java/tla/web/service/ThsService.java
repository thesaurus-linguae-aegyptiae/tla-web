package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.ThsEntry;
import tla.web.model.meta.ModelClass;
import tla.web.repo.TlaClient;

@Service
@ModelClass(ThsEntry.class)
public class ThsService extends ObjectService<ThsEntry> {
	
	 private TlaClient tlaClient;

	    public ThsService(TlaClient tlaClient) {
	        this.tlaClient = tlaClient;
	    }

    @Override
    public String getLabel(ThsEntry object) {
        return object.getName();
    }
    
    public ThsEntry findById(String id) {
        return tlaClient.findById(id);
    }
}