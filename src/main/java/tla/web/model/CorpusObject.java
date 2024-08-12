package tla.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.domain.dto.CorpusObjectDto;
import tla.domain.model.ObjectPath;
import tla.domain.model.ObjectReference;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.Hierarchic;
import tla.domain.model.meta.TLADTO;
import tla.web.model.meta.BTSObject;
import tla.web.model.meta.BackendPath;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("object")
@BTSeClass("BTSTCObject")
@TLADTO(CorpusObjectDto.class)
public class CorpusObject extends BTSObject implements Hierarchic {

    private List<ObjectPath> paths;
    
    // Text object date
    
    public static final String PASSPORT_PROP_DATE = "date.date.date";
    @Setter(AccessLevel.NONE)
    private List<ObjectReference> date;
    public List<ObjectReference> getDate(){
    	if (this.date == null) {
    		Passport passport;
    		this.date = this.extractObjectReferences(this,PASSPORT_PROP_DATE);
    	}
    	tla.domain.util.IO.json(this.date);
    	return this.date;
    }     


    // Text object date comment
    
    public static final String PASSPORT_PROP_DATECOMMENT = "date.date.comment";

    @Setter(AccessLevel.NONE)
    private List<String> dateComment;

    public List<String> getDateComment() {
        if (this.dateComment == null) {
            this.dateComment = extractMultilineText(this.getPassport(), PASSPORT_PROP_DATECOMMENT);
        }
        return this.dateComment;
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

    private static List<SynonymGroup> extractSynonymGroups(CorpusObject corpusobj) {
        List<SynonymGroup> synonymGroups = new ArrayList<>();
        try {
            corpusobj.getPassport().extractProperty(
                PASSPORT_PROP_SYNONYMGROUP
            ).forEach(
                node -> synonymGroups.add(new SynonymGroup(node))
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract synonyms from object "+corpusobj.getId());
        }
        return synonymGroups;
    }
    
    // Bibliography
    public static final String PASSPORT_PROP_BIBL = "bibliography.bibliographical_text_field";

     @Setter(AccessLevel.NONE)
    private List<String> bibliography;
    
    public List<String> getBibliography() {
        if (this.bibliography == null) {
            this.bibliography = extractMultilineText(this.getPassport(), PASSPORT_PROP_BIBL);
        }
        return this.bibliography;
    }
 
    // Object origin Find-spot
    
    public static final String PASSPORT_PROP_FINDSPOT = "find_spot.find_spot";
    public static final String PASSPORT_PROP_FINDSPOT_FORMERPLACE = "former_place";
    public static final String PASSPORT_PROP_FINDSPOT_PLACE = "place";
    
    @Setter(AccessLevel.NONE)
    private List<FindSpot> findspots;
    
    public static List<String> extractValueS(Passport passport, String searchString) {
    	List<Passport> passports;
    	List<String> result = new ArrayList<String>();
    	try{
    		passports = passport.extractProperty(searchString);
    		passports.forEach( node -> result.add(node.getLeafNodeValue()));
    	}catch(Exception e) {
    		// System.out.println("INFO: Could not extract " + searchString);
    	}
    	return result;
    }
    
    public static String extractValue(Passport passport, String searchString) {
    	String result=null;
    	try{
    		result = passport.extractProperty(searchString).get(0).getLeafNodeValue();
    	}catch(Exception e) {
    		// System.out.println("INFO: Could not extract " + searchString);
    	}
    	return result;
    }

    public static ObjectReference extractThsEntry(Passport passport, String searchString) {
    	ObjectReference result=null;
    	try{
    		result = passport.extractProperty(searchString).get(0).extractObjectReferences().get(0);
    	}catch(Exception e) {
    		// System.out.println("INFO: Could not extract " + searchString);
    	}
    	return result;
    }

    public static List<ObjectReference> extractThsEntries(Passport passport, String searchString) {
    	List<ObjectReference> result = new ArrayList<ObjectReference>();
        try {       
          result =passport.extractProperty(searchString).get(0).extractObjectReferences();
    	}catch(Exception e) {
    		// System.out.println("INFO: Could not extract " + searchString);
    	}
    	return result;
    }
  
    public static ObjectReference extractObjectReference(Passport passport) {
    	ObjectReference object=null;
    	try{
    		object = passport.extractObjectReferences().get(0);
    	}catch(Exception e) {
    		// System.out.println("INFO: Could not extract ObjectReference");
    	}
    	return object;
    }
    
    private static List<ObjectReference> extractObjectReferences(CorpusObject object, String searchString) {
        List<ObjectReference> objectReferences = new ArrayList();
        try {
          List<Passport> pass= object.getPassport().extractProperty(searchString);
           pass.forEach(node -> objectReferences.add(node.extractObjectReferences().get(0)));
        } catch (Exception e) {
          // System.out.println("INFO: could not extract language from text {} " + object.getId());
        }
        return objectReferences;
    }
    
    @Getter
    public static class Place{
    	public Place(Passport passport) {
    		this.certainty = extractValue(passport, "certainty");
    		this.comment = extractValue(passport, "comment");
    		this.isorigin= extractValue(passport, "is_origin");
    		this.place = extractObjectReference(passport);
    		}
    	private String certainty;
    	private String comment;
    	
    	private String isorigin;
    	private ObjectReference place;

    	public String getIsorigin() { //TODO Lombok
    		return isorigin;
    	}
    	
    
    }
    
    
    public static class FindSpot{
    	public FindSpot(Passport passport) {
    		this.places = extractPlace(passport,PASSPORT_PROP_FINDSPOT_PLACE);
    		this.formerPlaces = extractPlace(passport,PASSPORT_PROP_FINDSPOT_FORMERPLACE);
    	}
    	private List<Place> places;
    	private List<Place> formerPlaces;
    	
    	public List<Place> getPlaces() {
    		return this.places;
    	}
    	
    	public List<Place> getFormerPlaces() {
    		return this.formerPlaces;
    	}
     }
    
    public List<FindSpot> getFindSpots() {
        if (this.findspots == null) {
            this.findspots = extractFindSpot(this);
        }
        return this.findspots;
    }

    private static List<FindSpot> extractFindSpot(CorpusObject corpusobj) {
        List<FindSpot> findspots = new ArrayList<FindSpot>();
        try {       
          List<Passport> findSpotPassports =corpusobj.getPassport().extractProperty(PASSPORT_PROP_FINDSPOT);
         findSpotPassports.forEach(
        		 entry -> findspots.add(new FindSpot(entry)));        
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract find spot (origin) from object "+corpusobj.getId());
        }
        return findspots;
    }
    
    private static List<Place> extractPlace(Passport passport, String searchString){	
    	List<Passport> placesPassport = new ArrayList<Passport>();
    	List<Place> places = new ArrayList<Place>();
        try {
          placesPassport = passport.extractProperty(searchString);
         placesPassport.forEach(
        		 entry -> places.add(new Place(entry)));
         
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract a place from findspot from object ");
        }
        return places;	
    }
    
     // Object location
     
    public static final String PASSPORT_PROP_PRESLOC = "present_location.location";
    
    @Setter(AccessLevel.NONE)
    private List<Location> locations;
    
    @Getter
    public static class Location{
    	public Location(Passport passport) {
    		this.comment = extractValue(passport, "comment");
    		this.inventory_number = extractValueS(passport, "inventory_number");
    	    this.is_present_location = extractValue(passport, "is_present_location");
    		this.in_situ = extractValue(passport, "in_situ");
    		this.location = extractObjectReference(passport);
    		
    	}
    	private String comment;
    	private List<String> inventory_number;
    	private String is_present_location;
    	private String in_situ;
    	private ObjectReference location;
    		
    }

   public List<Location> getLocations() {
        if (this.locations == null) {
            this.locations = extractLocations(this);
        }
        return this.locations;
    }
 
    private static List<Location> extractLocations(CorpusObject corpusobj) {
        List<Location> locations = new ArrayList<Location>();
        try {
          List<Passport> locationsPassports = corpusobj.getPassport().extractProperty(PASSPORT_PROP_PRESLOC);
          locationsPassports.forEach(
        		  passport -> locations.add(new Location(passport))
        		  );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract locations from object "+corpusobj.getId());
        }
        return locations;
    }

    // Object Inv.nos.
     
    public static final String PASSPORT_PROP_PRESLOC_ID = "present_location.location.inventory_number";
    
    @Setter(AccessLevel.NONE)
    private List<String> invNos;

   public List<String> getInvNos() {
        if (this.invNos == null) {
            this.invNos = extractInvNos(this);
        }
        return this.invNos;
    }
 
    private static List<String> extractInvNos(CorpusObject corpusobj) {
        List<String> invNos = new ArrayList<String>();
        try {
            corpusobj.getPassport().extractProperty(
                PASSPORT_PROP_PRESLOC_ID
            ).forEach(
                node -> invNos.add(node.getLeafNodeValue())
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract location inventory nos. from object "+corpusobj.getId());
        }
        
        return invNos;
    }
    
    // Protocol
    
    public static final String PASSPORT_PROP_PROTOCOL = "definition.main_group.protocol";

    @Setter(AccessLevel.NONE)
    private List<String> protocol;

    public List<String> getProtocol() {
        if (this.protocol == null) {
            this.protocol = extractMultilineText(this.getPassport(), PASSPORT_PROP_PROTOCOL);
        }
        return this.protocol;
    }
      
    // Description
    
    public static final String PASSPORT_PROP_DESCR = "definition.main_group";

    
    @Getter
    public static class Description{
    	Description(Passport passport){
    		this.reconstructed = extractValue(passport, "reconstructed");
    		this.protocol = extractValue(passport, "protocol");
    		this.comment = extractValue(passport, "comment");
    		this.definition = extractValue(passport, "definition");
    	}
    	private String reconstructed;
	    private String protocol;
	    private String comment;
	    private String definition;
    	
    }
    
    Description description = null;

    public Description getDescription() {
    	
        if (description == null) {
            description = new Description(extractDescription(this));
        }
        return description;
    }

    private static Passport extractDescription(CorpusObject corpusobj) { //only one description in an object
        Passport descriptionPassport = new Passport();
        try {
           descriptionPassport = corpusobj.getPassport().extractProperty(
                PASSPORT_PROP_DESCR
            ).get(0);
                        //node.getLeafNodeValue().replaceAll("\\s*\\r?\\n", "||").split("\\|\\|") 
                   
           
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract description from object "+corpusobj.getId());
        }
        return descriptionPassport;
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
    
    // Object description
    ///////////////////////////
    public static final String PASSPORT_PROP_OBJ_MODEL= "object.description_of_object.model";
    private String isModel;
    public String getIsModel() {
        if (this.isModel == null) {
            this.isModel = extractValue(this.getPassport(), PASSPORT_PROP_OBJ_MODEL);
        }
        return this.isModel;
    }
    
    public static final String PASSPORT_PROP_OBJ_IMITATION= "object.description_of_object.imitation";
    private String isImitation;
    public String getIsImitation() {
        if (this.isImitation == null) {
            this.isImitation = extractValue(this.getPassport(), PASSPORT_PROP_OBJ_IMITATION);
        }
        return this.isImitation;
    }
    
    public static final String PASSPORT_PROP_OBJ_MINIATURE= "object.description_of_object.miniature";
    private String isMiniature;
    public String getIsMiniature() {
        if (this.isMiniature == null) {
            this.isMiniature = extractValue(this.getPassport(), PASSPORT_PROP_OBJ_MINIATURE);
        }
        return this.isMiniature;
    }
    
    public static final String PASSPORT_PROP_OBJ_SKEUO= "object.description_of_object.skeuomorph";
    private String isSkeuomorph;
    public String getIsSkeuomorph() {
        if (this.isSkeuomorph == null) {
            this.isSkeuomorph = extractValue(this.getPassport(), PASSPORT_PROP_OBJ_SKEUO);
        }
        return this.isSkeuomorph;
    }
    
    public static final String PASSPORT_PROP_OBJ_OWNER = "object.description_of_object.owner";
    private ObjectReference owner;
    public ObjectReference getOwner() {
        if (this.owner == null) {
            this.owner = extractThsEntry(this.getPassport(), PASSPORT_PROP_OBJ_OWNER);
        }
        return this.owner;
    }
    
    public static final String PASSPORT_PROP_OBJ_DESCR = "object.description_of_object.description";
    public static final String PASSPORT_PROP_OBJ_COMMENT = "object.description_of_object.comment";
    
    public static final String PASSPORT_PROP_OBJ_CONDITION = "object.technical_details.condition";
    private ObjectReference condition;
    public ObjectReference getCondition() {
        if (this.condition == null) {
            this.condition = extractThsEntry(this.getPassport(), PASSPORT_PROP_OBJ_CONDITION);
        }
        return this.condition;
    }

    public static final String PASSPORT_PROP_OBJ_TECH = "object.technical_details.technique";
    private List<ObjectReference> technique;
    public List<ObjectReference> getTechnique() {
        if (this.technique == null) {
            this.technique = extractThsEntries(this.getPassport(), PASSPORT_PROP_OBJ_TECH);
        }
        return this.technique;
    }    
    
    public static final String PASSPORT_PROP_OBJ_TECH_COMMENT = "object.technical_details.comment";
    
    @Setter(AccessLevel.NONE)
    private List<String> materialityComment;
    
    public List<String> getMaterialityComment() {
        if (this.materialityComment == null) {
            this.materialityComment = extractMultilineText(this.getPassport(), PASSPORT_PROP_OBJ_TECH_COMMENT);
        }
        return this.materialityComment;
    }
    
    public static final String PASSPORT_PROP_OBJ_GROUPING = "object.archaeological_cultural_context_of_object.grouping";
    
    @Setter(AccessLevel.NONE)
    private ObjectReference grouping;
    
    public ObjectReference getGrouping() {
        if (this.grouping == null) {
            this.grouping = extractThsEntry(this.getPassport(), PASSPORT_PROP_OBJ_GROUPING);
        }
        return this.grouping;
    }
    
    public static final String PASSPORT_PROP_OBJ_CONTEXT = "object.archaeological_cultural_context_of_object.cultural_context";
    
    @Setter(AccessLevel.NONE)
    private List<ObjectReference> culturalContext;
    
    public List<ObjectReference> getCulturalContext() {
        if (this.culturalContext == null) {
            this.culturalContext = extractThsEntries(this.getPassport(), PASSPORT_PROP_OBJ_CONTEXT);
        }
        return this.culturalContext;
    }
    
    public static final String PASSPORT_PROP_OBJ_ARCH_COMMENT = "object.archaeological_cultural_context_of_object.comment";
    
    @Setter(AccessLevel.NONE)
    private List<String> contextComment;
    
    public List<String> getContextComment() {
        if (this.contextComment == null) {
            this.contextComment = extractMultilineText(this.getPassport(), PASSPORT_PROP_OBJ_ARCH_COMMENT);
        }
        return this.contextComment;
    }

    // Object Description_Of_Object type
    
    public static final String PASSPORT_PROP_OBJ_TYPE = "object.description_of_object.type";
    @Setter(AccessLevel.NONE)
    private ObjectReference objType;
    public ObjectReference getObjType() {
        if (this.objType == null) {
            this.objType = extractThsEntry(this.getPassport(), PASSPORT_PROP_OBJ_TYPE);
        }
        return this.objType;
    }
    
    // Object Description_Of_Object description

    public static final String PASSPORT_PROP_OBJ_DESCRIPTION = "object.description_of_object.description";
    //Maybe new Datatype description_of_object
    @Setter(AccessLevel.NONE)
    private List<String> objectDescription;
    public List<String> getObjectDescription(){
    	if(this.objectDescription == null) {
    		this.objectDescription = extractMultilineText(this.getPassport(), PASSPORT_PROP_OBJ_DESCRIPTION);
    	}
    	return this.objectDescription;
    }
    
    // Object component
    
    public static final String PASSPORT_PROP_OBJ_COMPONENT = "object.description_of_object.component";
    
    @Setter(AccessLevel.NONE)
    private List<ObjectReference> components;
    
    public List<ObjectReference> getComponents() {
        if (this.components == null) {
            this.components = extractThsEntries(this.getPassport(), PASSPORT_PROP_OBJ_COMPONENT);
        }
        return this.components;
    }

    // Material
    
    public static final String PASSPORT_PROP_OBJ_MATERIAL = "object.technical_details.material";

    @Setter(AccessLevel.NONE)
    private List<ObjectReference> materials;

    public List<ObjectReference> getMaterials() {
        if (this.materials == null) {
            this.materials = extractThsEntries(this.getPassport(), PASSPORT_PROP_OBJ_MATERIAL);
        }
        return this.materials;
    }
    
    // Dimensions

    public static final String PASSPORT_PROP_OBJ_HEIGHT = "object.technical_details.dimensions.h";
    public static final String PASSPORT_PROP_OBJ_LENGTH = "object.technical_details.dimensions.l";
    public static final String PASSPORT_PROP_OBJ_WIDTH = "object.technical_details.dimensions.w";

    @Setter(AccessLevel.NONE)
    private List<String> dimensions;

    public List<String> getDimensions() {
        if (this.dimensions == null) {
            this.dimensions = extractDimensions(this);
        }
        return this.dimensions;
    }

    private static List<String> extractDimensions(CorpusObject corpusobj) {
        List<String> dimensions = new ArrayList<>();
        try {
            corpusobj.getPassport().extractProperty(
                PASSPORT_PROP_OBJ_HEIGHT
            ).forEach(
                node -> dimensions.add(node.getLeafNodeValue())
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract length from object "+corpusobj.getId());
        }
        try {
            corpusobj.getPassport().extractProperty(
                PASSPORT_PROP_OBJ_LENGTH
            ).forEach(
                node -> dimensions.add(node.getLeafNodeValue())
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract width from object "+corpusobj.getId());
        }
        try {
            corpusobj.getPassport().extractProperty(
                PASSPORT_PROP_OBJ_WIDTH
            ).forEach(
                node -> dimensions.add(node.getLeafNodeValue())
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract thickness from object "+corpusobj.getId());
        }
        return dimensions;
    }
       
  
}