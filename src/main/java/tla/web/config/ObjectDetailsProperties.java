package tla.web.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectDetailsProperties {

    /**
     * List of passport property paths to be made available in details view.
     */
    private List<String> passportProperties = new LinkedList<>();

    /**
     * List of properties for which show/hide toggle buttons are to be created on search results pages.
     * Each entry can have a number of child properties or an empty list.
     */
    private Map<String, List<String>> hideableProperties;
    /**
     * Sort orders to be made available on search results page
     */
    //private Map<String, List<String>> showableProperties;
}
