package tla.web.repo;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import tla.domain.command.SearchCommand;
import tla.domain.dto.LemmaDto;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.domain.dto.meta.DocumentDto;
import tla.web.model.BackendPath;
import tla.web.model.Lemma;
import tla.web.model.TLAObject;
import tla.web.model.ThsEntry;

/**
 * Put all your model classes on top of this in the {@link ModelClasses} annotation so that
 * the backend path where instances of them can be retrieved can be extracted from their
 * respective {@link BackendPath} annotations.
 */
@Slf4j
@ModelClasses({
    Lemma.class,
    ThsEntry.class
})
public class TlaClient {

    private RestTemplate client;

    private String backendUrl;

    private static Map<Class<? extends TLAObject>, String> backendPaths = new HashMap<>();

    public TlaClient(String backendUrl) {
        log.info("create client for backend at {}", backendUrl);
        this.client = new RestTemplate();
        this.backendUrl = backendUrl;
        registerModelClasses();
    }

    /**
     * Takes all model classes listed in the {@link ModelClasses} annotation
     * of this class, retrieves the value of their respective {@link BackendPath}
     * annotations, and stores these value pairs for later lookup. 
     */
    private void registerModelClasses() {
        for (Annotation a : TlaClient.class.getAnnotations()) {
            if (a instanceof ModelClasses) {
                for (Class<? extends TLAObject> modelClass : ((ModelClasses) a).value()) {
                    registerModelclass(modelClass);
                }
            }
        }
    }

    private static void registerModelclass(Class<? extends TLAObject> modelClass) {
        for (Annotation a: modelClass.getAnnotations()) {
            if (a instanceof BackendPath) {
                backendPaths.put(
                    modelClass,
                    ((BackendPath) a).value()
                );
            }
        }
    }

    /**
     * Retrieves a single object from the backend application.
     *
     * In order for this to work, the requested object's type must be registered via {@link ModelClasses}
     * annotation on {@link TlaClient}, and must itself be annotated with a {@link BackendPath} annotation.
     */
    @SuppressWarnings("unchecked")
    public SingleDocumentWrapper<AbstractDto> retrieveObject(Class<? extends TLAObject> modelClass, String id) {
        String backendPath = backendPaths.get(modelClass);
        return client.getForObject(
            String.format("%s/%s/get/%s", this.backendUrl, backendPath, id),
            SingleDocumentWrapper.class
        );
    }

    @SuppressWarnings("unchecked")
    public SearchResultsWrapper<DocumentDto> lemmaSearch(SearchCommand<LemmaDto> command, int page) {
        return client.postForObject(
            String.format("%s/lemma/search?page={page}", this.backendUrl),
            command,
            SearchResultsWrapper.class,
            Map.of("page", Math.max(0, page - 1))
        );
    }

}
