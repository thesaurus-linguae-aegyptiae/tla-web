package tla.web.repo;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import tla.domain.dto.DocumentDto;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.web.model.BackendPath;
import tla.web.model.Lemma;
import tla.web.model.TLAObject;

/**
 * Put all your model classes on top of this in the {@link ModelClasses} annotation so that
 * the backend path where instances of them can be retrieved can be extracted from their
 * respective {@link BackendPath} annotations.
 */
@ModelClasses({
    Lemma.class
})
public class TlaClient {

    private RestTemplate client;

    private String backendUrl;

    private static Map<Class<? extends TLAObject>, String> backendPaths = new HashMap<>();

    public TlaClient(String backendUrl) {
        this.client = new RestTemplate();
        this.backendUrl = backendUrl;
        registerModelClasses();
    }

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

    @SuppressWarnings("unchecked")
    public SingleDocumentWrapper<DocumentDto> retrieveObject(Class<? extends TLAObject> modelClass, String id) {
        String backendPath = backendPaths.get(modelClass);
        return client.getForObject(
            String.format("%s/%s/get/%s", this.backendUrl, backendPath, id),
            SingleDocumentWrapper.class
        );
    }

}
