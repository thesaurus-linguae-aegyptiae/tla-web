package tla.web.service;

import org.springframework.stereotype.Service;

import tla.domain.dto.DocumentDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.ThsEntry;

@Service
public class ThsService extends ObjectService<ThsEntry> {

    @Override
    protected SingleDocumentWrapper<DocumentDto> retrieveSingleDocument(String id) {
        return backend.retrieveObject(ThsEntry.class, id);
    }

}