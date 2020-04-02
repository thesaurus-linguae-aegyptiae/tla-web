package tla.web.service;

import tla.domain.dto.LemmaDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.Lemma;
import tla.web.repo.TlaClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;


@Service
public class LemmaService {

    @Autowired
    private TlaClient api;

    @Autowired
    private ModelMapper mapper;

    public Lemma getLemma(String id) {
        SingleDocumentWrapper<LemmaDto> wrapper = api.getLemma(id);
        return mapper.map(wrapper.getDoc(), Lemma.class);
    }

}
