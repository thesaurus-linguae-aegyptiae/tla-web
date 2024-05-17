package tla.web.model.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import tla.domain.model.EditorInfo;
import tla.domain.model.Passport;
import tla.web.model.parts.ExternalReference;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BTSObject extends TLAObject { // earlier: abstract 

    private Passport passport;

    private EditorInfo edited;

    private String reviewState;

    private String subtype;

    private String name;

    private TreeMap<String, List<ExternalReference>> externalReferences;
    
    public static final String PASSPORT_PROP_METADATA_EDITING = "definition.main_group.metadata_editing";
    
    @Setter(AccessLevel.NONE)
    private List<String> metadata_editing;

    public List<String> getMetadata_editing() {
        if (this.metadata_editing == null) {
            this.metadata_editing = extractMultilineText(this.getPassport(),PASSPORT_PROP_METADATA_EDITING);
        }
        return this.metadata_editing;
    }
    
    //Function to return a string from passport with default number 0. Takes value from first element
    //TODO think of move to passport.java 
    protected static String extractString(Passport passport, String searchField) {
    	return extractString( passport, searchField, 0); 
    }
    
    //Function to return a string from passport. Takes value from element number position
    //TODO think of move to passport.java 
    protected static String extractString(Passport passport, String searchField, Integer number) {
    	String value = new String();
    	try {
    		List<Passport> pass = passport.extractProperty(searchField);
    		value = pass.get(number).toString();
    	}catch (Exception e) {
            // System.out.println("could not extract " + searchField + " from text {} "+text.getId());
    	}
    	return value;
    }
    
    /**
     * Extract multiline text field information from BTSObject passport.
     *
     * The value(s) found under that locator are split at line breaks <code>\r\n</code>.
     */
    //Function to return a list of string from passport. Concats value from all elements
    //TODO think of move to passport.java 
    protected static List<String> extractMultilineText(Passport passport, String searchField) {
        List<String> stringList = new ArrayList<>();
        try {
            passport.extractProperty(searchField).forEach(
                node -> stringList.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().replaceAll("\\s*\\r?\\n", "||").replaceAll("\\-+ ", "– ").split("\\|\\|") 
                    ).stream().map(
                        para -> para.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract "+searchString+" from "+this.getId());
        }
        return stringList;
    }
}
