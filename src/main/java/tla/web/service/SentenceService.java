package tla.web.service;

import org.springframework.stereotype.Service;

import tla.web.model.Sentence;
import tla.web.model.meta.ModelClass;

@Service
@ModelClass(Sentence.class)
public class SentenceService extends ObjectService<Sentence> {}