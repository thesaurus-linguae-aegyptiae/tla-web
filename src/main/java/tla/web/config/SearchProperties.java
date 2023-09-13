package tla.web.config;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import tla.web.model.meta.ModelClass;
import tla.web.model.meta.TLAObject;

/**
 * annotate implementing subclasses with {@link Component}, {@link ConfigurationProperties},
 * and {@link ModelClass} in order to have them automatically registered and made available
 * to object service corresponding to model class.
 */
@Getter
@Setter
public abstract class SearchProperties {

    /**
     * List of properties for which show/hide toggle buttons are to be created on search results pages.
     * Each entry can have a number of child properties or an empty list.
     */
    private Map<String, List<String>> hideableProperties;
    /**
    * Properties/menu buttons for lemma search results 
    */
   private Map<String, List<String>> hideable1LemmaProperties;
   private Map<String, List<String>> showableProperties;
   private Map<String, List<String>> hideable2LemmaProperties;
   private Map<String, List<String>> hideablesTextsentencesProperties;
    /**
     * Sort orders to be made available on search results page
     */
    private List<String> sortOrders;
    
   

    private static Map<Class<? extends TLAObject>, SearchProperties> modelSearchProperties = new HashMap<>();

    public SearchProperties() {
        for (Annotation a : this.getClass().getAnnotationsByType(ModelClass.class)) {
            var modelClass = ((ModelClass) a).value();
            modelSearchProperties.put(
                modelClass, this
            );
        }
    }

    public static SearchProperties getPropertiesFor(Class<? extends TLAObject> modelClass) {
        return modelSearchProperties.getOrDefault(modelClass, null);
    }

}