package tla.web.repo;

import org.springframework.web.client.RestTemplate;

import tla.domain.dto.DocumentDto;
import tla.domain.dto.extern.SingleDocumentWrapper;


public class TlaClient {

    private RestTemplate client;

    private String backendUrl;


    public TlaClient(String backendUrl) {
        this.client = new RestTemplate();
        this.backendUrl = backendUrl;
    }

    @SuppressWarnings("unchecked")
    public SingleDocumentWrapper<DocumentDto> getLemma(String id) {
        return client.getForObject(
            String.format("%s/lemma/get/%s", this.backendUrl, id),
            SingleDocumentWrapper.class
        );
    }

}
