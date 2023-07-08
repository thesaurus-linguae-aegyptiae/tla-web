package tla.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tla.domain.dto.CorpusObjectDto;
import tla.domain.model.ObjectPath;
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

    public static final String PASSPORT_PROP_BIBL = "bibliography.bibliographical_text_field";
    public static final String PASSPORT_PROP_DATE = "date.date.date";
    public static final String PASSPORT_PROP_PRESLOC = "present_location.location.location";
    public static final String PASSPORT_PROP_ORIGIN = "find_spot.find_spot.place.place";
    public static final String PASSPORT_PROP_OBJTYPE = "object.description_of_object.type";
    
    @Setter(AccessLevel.NONE)
    private List<String> bibliography;
    @Setter(AccessLevel.NONE)
    private List<String> date;
    @Setter(AccessLevel.NONE)
    private List<String> locations;
    @Setter(AccessLevel.NONE)
    private List<String> origin;
    @Setter(AccessLevel.NONE)
    private List<String> objType;
    
    public List<String> getBibliography() {
        if (this.bibliography == null) {
            this.bibliography = extractBibliography(this);
        }
        return this.bibliography;
    }

    public List<String> getLocations() {
        if (this.locations == null) {
            this.locations = extractLocations(this);
        }
        return this.locations;
    }

    public List<String> getOrigin() {
        if (this.origin == null) {
            this.origin = extractOrigin(this);
        }
        return this.origin;
    }

    public List<String> getObjType() {
        if (this.objType == null) {
            this.objType = extractObjType(this);
        }
        return this.objType;
    }

    public List<String> getDate() {
        if (this.date == null) {
            this.date = extractDate(this);
        }
        return this.date;
    }

    /**
     * Extract bibliographic information from coprus object passport.
     *
     * Bibliography is being copied from the <code>bibliography.bibliographical_text_field</code>
     * passport field. The value(s) found under that locator are split at line breaks <code>\r\n</code>.
     *
     * @param corpusobj The corpus object instance from whose passport the bibliography is to be extracted.
     * @return List of textual bibliographic references or an empty list
     */
    private static List<String> extractBibliography(CorpusObject corpusobj) {
        List<String> bibliography = new ArrayList<>();
        try {
            corpusobj.getPassport().extractProperty(
                PASSPORT_PROP_BIBL
            ).forEach(
                node -> bibliography.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1").replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|")
                    ).stream().map(
                        bibref -> bibref.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          System.out.println("INFO: Could not extract bibliography from object "+corpusobj.getId());
        }
        return bibliography;
    }
    
    private static List<String> extractLocations(CorpusObject corpusobj) {
        List<String> locations = new ArrayList<String>();
        try {
        
          List<Passport> locationsPassports =corpusobj.getPassport().extractProperty(PASSPORT_PROP_PRESLOC);
         
          for(int i=0;i<locationsPassports.size();i++) {
        	  for(int j=0;j<locationsPassports.get(i).extractObjectReferences().size();j++) {

        	 locations.add(locationsPassports.get(i).extractObjectReferences().get(j).getName());
        	  }
          }
        } catch (Exception e) {
          System.out.println("INFO: Could not extract locations from object "+corpusobj.getId());
        }
        return locations;
    }
    
    private static List<String> extractOrigin(CorpusObject corpusobj) {
        List<String> origin = new ArrayList<String>();
        try {
        
          List<Passport> findSpotPassports =corpusobj.getPassport().extractProperty(PASSPORT_PROP_ORIGIN);
         
          for(int i=0;i<findSpotPassports.size();i++) {
        	  for(int j=0;j<findSpotPassports.get(i).extractObjectReferences().size();j++) {

        	 origin.add(findSpotPassports.get(i).extractObjectReferences().get(j).getName());
        	  }
          }
        } catch (Exception e) {
          System.out.println("INFO: Could not extract find spot (origin) from object "+corpusobj.getId());
        }
        return origin;
    }
    
    private static List<String> extractObjType(CorpusObject corpusobj) {
        List<String> objType = new ArrayList<String>();
        try {
        
          List<Passport> objTypePassports =corpusobj.getPassport().extractProperty(PASSPORT_PROP_OBJTYPE);
         
          for(int i=0;i<objTypePassports.size();i++) {
        	  for(int j=0;j<objTypePassports.get(i).extractObjectReferences().size();j++) {

        	 objType.add(objTypePassports.get(i).extractObjectReferences().get(j).getName());
        	  }
          }
        } catch (Exception e) {
          System.out.println("INFO: Could not extract object type from object "+corpusobj.getId());
        }
        return objType;
    }

    private static List<String> extractDate(CorpusObject corpusobj) {
        List<String> dates = new ArrayList<String>();
        try {
        
          List<Passport> datesPassport =corpusobj.getPassport().extractProperty(PASSPORT_PROP_DATE);
         
          for(int i=0;i<datesPassport.size();i++) {
        	  for(int j=0;j<datesPassport.get(i).extractObjectReferences().size();j++) {

        	 dates.add(datesPassport.get(i).extractObjectReferences().get(j).getName());
        	  }
          }
           
        } catch (Exception e) {
           System.out.println("INFO: Could not extract date from object "+corpusobj.getId());
        }
        return dates;
    }
    
  

}