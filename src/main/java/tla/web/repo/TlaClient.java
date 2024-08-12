package tla.web.repo;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
//import org.springframework.web.client.RestTemplate;
//import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import tla.domain.command.SearchCommand;
import tla.domain.dto.extern.SearchResultsWrapper;
import tla.domain.dto.extern.SingleDocumentWrapper;
import tla.domain.dto.meta.AbstractDto;
import tla.web.model.meta.BackendPath;
import tla.web.model.meta.TLAObject;
/*import tla.web.model.ThsEntry;
import tla.web.model.Text;
import tla.web.model.Sentence;
import java.util.Collections;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;*/

/**
 * Put all your model classes on top of this in the {@link ModelClasses} annotation so that
 * the backend path where instances of them can be retrieved can be extracted from their
 * respective {@link BackendPath} annotations.
 */
@Slf4j
public class TlaClient {

    public static final UriTemplate URI_AUTOCOMPLETE = new UriTemplate("{url}/complete?q={q}&type={type}");

    private RestTemplate client;

    private String backendUrl;

    private static Map<Class<? extends TLAObject>, String> backendPaths = new HashMap<>();

    public TlaClient(String backendUrl) {
        log.info("create client for backend at {}", backendUrl);
        this.client = new RestTemplate();
        this.backendUrl = backendUrl;
    }

    /**
     * Register the backend API endpoint path prefix necessary for retrieval of documents of the type
     * represented by a given domain model class.
     * This path prefix is being specified via the {@link BackendPath} annotation on domain model
     * class declarations. Registration is being triggered the moment spring initiates the service
     * beans corresponding to those domain model classes.
     */
    public static void registerModelclass(Class<? extends TLAObject> modelClass) {
        for (Annotation a: modelClass.getAnnotations()) {
            if (a instanceof BackendPath) {
            	//System.out.println("put model class "+modelClass.getName()+ " "+ ((BackendPath) a).value());
                backendPaths.put(
                    modelClass,
                    ((BackendPath) a).value()
                );
            }
        }
    }

    /**
     * Return backend API endpoint path prefix for given domain model class.
     */
    public static String getBackendPathPrefix(Class<? extends TLAObject> modelClass) {
        return backendPaths.get(modelClass);
    }

    /**
     * Return backend API endpoint URL prefix for given domain model class retrieval.
     */
    public String getEndpointURLPrefix(Class<? extends TLAObject> modelClass) {
        return String.format(
            "%s/%s",
            this.backendUrl,
            getBackendPathPrefix(modelClass)
        );
    }

    /**
     * Call backend autocomplete endpoint for given model.
     * TODO
     */
    @SuppressWarnings("rawtypes")
    public ResponseEntity<List> autoComplete(Class<? extends TLAObject> modelClass, String term, String type) {
        return client.getForEntity(
            URI_AUTOCOMPLETE.expand(
                Map.of(
                    "url", this.getEndpointURLPrefix(modelClass),
                    "q", term,
                    "type", type
                )
            ).toString(),
            List.class
        );
    }

    /**
     * Retrieves a single object from the backend application.
     *
     * In order for this to work, the requested object's type must be registered via {@link ModelClasses}
     * annotation on {@link TlaClient}, and must itself be annotated with a {@link BackendPath} annotation.
     */
    @SuppressWarnings("unchecked")
    public SingleDocumentWrapper<AbstractDto> retrieveObject(Class<? extends TLAObject> modelClass, String id) {
        return client.getForObject(
            String.format(
                "%s/get/%s",
                this.getEndpointURLPrefix(modelClass),
                id
            ),
            SingleDocumentWrapper.class
        );
    }
    
    /**
     * checks if single id exists at the backend application.//TODO returns/receives.
     */
    @SuppressWarnings("unchecked")
    public String getTypeOfIdFromAllIndices(String id) {
        return client.getForObject(
        		   String.format(
                           "%s/typeofid/%s",
                           this.backendUrl,
                           id
            ),
            String.class
        );
    }

    /**
     * Send search form/command to backend endpoint configured for specified frontend
     * model class. The endpoint path is taken from the frontend model class's
     * {@link BackendPath} annotation.
     * @see #retrieveObject(Class, String)
     */
    public <T extends TLAObject> SearchResultsWrapper<?> searchObjects(
        Class<? extends T> modelClass, SearchCommand<?> searchForm, int page
    ) {
        return client.postForObject(
            String.format("%s/search?page={page}", this.getEndpointURLPrefix(modelClass)),
            searchForm, SearchResultsWrapper.class,
            Map.of("page", Math.max(0, page - 1))
        );
    }

/*    private ThsEntry getThsEntry(String url, String id) {
        ResponseEntity<String> response = client.getForEntity(url, String.class, id);
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode docNode = rootNode.get("doc");
                ThsEntry thsEntry = objectMapper.treeToValue(docNode, ThsEntry.class);
                return thsEntry;
            } catch (Exception e)  {
                // Handle the exception or log an error message
               System.out.println("Error");
               e.printStackTrace();
            }
        }

        return null;
    }
    
    private Text getText(String url, String id) {
        ResponseEntity<String> response = client.getForEntity(url, String.class, id);
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode docNode = rootNode.get("doc");
                Text textEntry = objectMapper.treeToValue(docNode, Text.class);
                return textEntry;
            } catch (Exception e)  {
                // Handle the exception or log an error message
               System.out.println("Error");
               e.printStackTrace();
            }
        }

        return null;
    }
    
    public ThsEntry findById(String id) {
        String url = String.format("%s/get/%s", getEndpointURLPrefix(ThsEntry.class), id);
        System.out.println("URL"+url);
        return getThsEntry(url, id);
    }
    public Text findTextById(String id) {
        String url = String.format("%s/get/%s", getEndpointURLPrefix(Text.class), id);
        System.out.println("URL"+url);
        return getText(url, id);
    }
    
    public List<Sentence> getSentencesByTextId(String textId) {
        String endpointUrl = String.format("%s/sentences?textId=%s", backendUrl, textId);
        ResponseEntity<List<Sentence>> response = client.exchange(
            endpointUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Sentence>>() {}
        );
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Handle error response
            // You can throw an exception or return an empty list, depending on your use case
            return Collections.emptyList();
        }
    }*/

}