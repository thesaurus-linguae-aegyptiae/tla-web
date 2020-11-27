package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.CorpusObject;
import tla.web.model.meta.ModelClass;

@Service
@ModelClass(CorpusObject.class)
public class CorpusObjectService extends ObjectService<CorpusObject> {}