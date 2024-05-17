package tla.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import tla.domain.dto.ThsEntryDto;
import tla.domain.model.Language;
import tla.domain.model.ObjectPath;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.Hierarchic;
import tla.domain.model.meta.TLADTO;
import tla.web.model.CorpusObject.SynonymGroup;
import tla.web.model.meta.BTSObject;
import tla.web.model.meta.BackendPath;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("ths")
@BTSeClass("BTSThsEntry")
//@JsonIgnoreProperties(ignoreUnknown = true)
@TLADTO(ThsEntryDto.class)
public class ThsEntry extends BTSObject implements Hierarchic {

    @Singular
    private SortedMap<Language, List<String>> translations;

    private List<ObjectPath> paths;
   
    // 
    public static String extractValue(Passport passport, String searchString) {
    	String result=null;
    	try{
    		result = passport.extractProperty(searchString).get(0).getLeafNodeValue();
    	}catch(Exception e) {
    		// System.out.println("INFO: Could not extract " + searchString);
    	}
    	return result;
    }
    
    // Synonyms
    
    public static final String PASSPORT_PROP_SYNONYMGROUP = "synonyms.synonym_group";

    @Setter(AccessLevel.NONE)
    private List<SynonymGroup> synonymGroups;
    
    public static class SynonymGroup{
    	public SynonymGroup(Passport passport) {
    		this.language = extractValue(passport, "language");
    		this.synonym = extractValue(passport, "synonym");
    	}
    	private String language;
    	private String synonym;
    	
    	public String getSynonym() {
    		return this.synonym;
    	}
    	
    	public String getLanguage() {
    		return this.language;
    	}
    }

    public List<SynonymGroup> getSynonymGroups() {
        if (this.synonymGroups == null) {
            this.synonymGroups = extractSynonymGroups(this);
        }
        return this.synonymGroups;
    }

    private static List<SynonymGroup> extractSynonymGroups(ThsEntry thsentry) {
        List<SynonymGroup> synonymGroups = new ArrayList<>();
        try {
            thsentry.getPassport().extractProperty(
                PASSPORT_PROP_SYNONYMGROUP
            ).forEach(
                node -> synonymGroups.add(new SynonymGroup(node))
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract synonyms from object "+corpusobj.getId());
        }
        return synonymGroups;
    }

    // Description
    
    public static final String PASSPORT_PROP_DESCR = "definition.main_group.definition";

    @Setter(AccessLevel.NONE)
    private List<String> description;

    public List<String> getDescription() {
        if (this.description == null) {
            this.description = extractMultilineText(this.getPassport(), PASSPORT_PROP_DESCR);
        }
        return this.description;
    }

    // File comment
    
    public static final String PASSPORT_PROP_FILECOMMENT = "definition.main_group.comment";

    @Setter(AccessLevel.NONE)
    private List<String> fileComment;

    public List<String> getFileComment() {
        if (this.fileComment == null) {
            this.fileComment = extractMultilineText(this.getPassport(), PASSPORT_PROP_FILECOMMENT);
        }
        return this.fileComment;
    }
   
}