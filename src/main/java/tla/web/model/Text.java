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
import tla.domain.dto.TextDto;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.TLADTO;
import tla.web.model.Sentence.DatePair;
import tla.web.model.meta.BackendPath;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("text")
@BTSeClass("BTSText")
@TLADTO(TextDto.class)
public class Text extends CorpusObject {

    private TextDto.WordCount wordCount;
    public static final String PASSPORT_PROP_BIBL = "bibliography.bibliographical_text_field";
    public static final String PASSPORT_PROP_SCRIPT = "text.textual_metadata.script";
    public static final String PASSPORT_PROP_DATE = "date.date.date";
    public static final String PASSPORT_PROP_LANGUAGE ="text.textual_metadata.language";
    public static final String PASSPORT_PROP_ORIGPLACE ="find_spot.find_spot.place.place";
    public static final String PASSPORT_PROP_ISORIG ="find_spot.find_spot.place.is_origin";
    public static final String PASSPORT_PROP_PRESLOC ="present_location.location.location";
    @Setter(AccessLevel.NONE)
    private List<String> bibliography;
    @Setter(AccessLevel.NONE)
    private List<String>skript;
    @Setter(AccessLevel.NONE)
    private List<String> date;
    @Setter(AccessLevel.NONE)
    private List<String>language;
    @Setter(AccessLevel.NONE)
    private List<String>origplace;
    @Setter(AccessLevel.NONE)
    private String isorig;
    @Setter(AccessLevel.NONE)
    private List<String> presloc;
    
    public List<String> getBibliography() {
        if (this.bibliography == null) {
            this.bibliography = extractBibliography(this);
        }
        return this.bibliography;
    }
    public List<String> getSkript() {
        if (this.skript == null) {
            this.skript = extractScript(this);
        }
        System.out.println ("Skript "+this.skript.toString());
        return this.skript;
    }
    
    public List<String> getLanguage() {
        if (this.language == null) {
            this.language = extractLanguage(this);
        }
     
        return this.language;
    }
    public List<String> getOrigplace() {
        if (this.origplace == null) {
            this.origplace = extractOrigplace(this);
        }
     
        return this.origplace;
    }
    
    public List<String> getPresloc() {
        if (this.presloc == null) {
            this.presloc = extractPresloc(this);
        }
     
        return this.presloc;
    }
    public String getIsorig() {
        if (this.isorig == null) {
            this.isorig = extractIsOrigPlace(this);
        }
     
        return this.isorig;
    }

    /**
     * Extract bibliographic information from text passport.
     *
     * Bibliography is being copied from the <code>bibliography.bibliographical_text_field</code>
     * passport field. The value(s) found under that locator are split at line breaks <code>\r\n</code>.
     *
     * @param text The text instance from whose passport the bibliography is to be extracted.
     * @return List of textual bibliographic references or an empty list
     */
    private static List<String> extractBibliography(Text text) {
        List<String> bibliography = new ArrayList<>();
        try {
            text.getPassport().extractProperty(
                PASSPORT_PROP_BIBL
            ).forEach(
                node -> bibliography.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue()
                    ).stream().map(
                        bibref -> bibref.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          System.out.println("could not extract bibliography from text {} "+text.getId());
        }
        return bibliography;
    }
/* Extract Script von Passport */
    
    private static List<String> extractScript(Text text) {
        List<String> skript = new ArrayList<>();
        try {
           List<Passport> pass= text.getPassport().extractProperty(PASSPORT_PROP_SCRIPT);
            for(int i=0;i<pass.size();i++) {
            	skript.add(pass.get(i).extractObjectReferences().get(0).getName());
            }
            
           
        } catch (Exception e) {
          System.out.println("could not extract script from text {} "+text.getId());
        }
        return skript;
    }
    
    
    private static List<String> extractLanguage(Text text) {
        List<String> language = new ArrayList<>();
        try {
           List<Passport> pass= text.getPassport().extractProperty(PASSPORT_PROP_LANGUAGE);
            for(int i=0;i<pass.size();i++) {
            	language.add(pass.get(i).extractObjectReferences().get(0).getName());
            }
            
           
        } catch (Exception e) {
          System.out.println("could not extract language from text {} "+text.getId());
        }
        return language;
    }
    
    private static String extractIsOrigPlace(Text text) {
        String isOrigPl = new String();
        try {
           List<Passport> pass= text.getPassport().extractProperty(PASSPORT_PROP_ISORIG);
           
            	isOrigPl=pass.get(0).toString();
            	System.out.println("isOrig "+ isOrigPl);
            		if (isOrigPl.equals("true")) isOrigPl="original";
            		else isOrigPl=null;
       
           
        } catch (Exception e) {
          System.out.println("could not extract is orig place from text {} "+text.getId());isOrigPl=null;
        }
        return isOrigPl;
    }
    
    private static List<String> extractOrigplace(Text text) {
        List<String> origplace = new ArrayList<>();
        try {
           List<Passport> pass= text.getPassport().extractProperty(PASSPORT_PROP_ORIGPLACE);
            for(int i=0;i<pass.size();i++) {
            	origplace.add(pass.get(i).extractObjectReferences().get(0).getName());
            }
            
           
        } catch (Exception e) {
          System.out.println("could not extract original place from text {} "+text.getId());
        }
        return origplace;
    }
    
    private static List<String> extractPresloc(Text text) {
        List<String> presloc = new ArrayList<>();
        try {
           List<Passport> pass= text.getPassport().extractProperty(PASSPORT_PROP_PRESLOC);
            for(int i=0;i<pass.size();i++) {
            	presloc.add(pass.get(i).extractObjectReferences().get(0).getName());
            }
            
           
        } catch (Exception e) {
          System.out.println("could not extract present place from text {} "+text.getId());
        }
        return presloc;
    }
    
    private static List<String> extractDate(Text text) {
        List<String> datierung = new ArrayList<String>();
        try {
        
          List<Passport> dates =text.getPassport().extractProperty(PASSPORT_PROP_DATE);
         
          for(int i=0;i<dates.size();i++) {
        	  for(int j=0;j<dates.get(i).extractObjectReferences().size();j++) {

        	 datierung.add(dates.get(i).extractObjectReferences().get(j).getName());
        	  }
          }
           
        } catch (Exception e) {
           // log.debug("could not extract date from text {}", text.getId());
           System.out.println("could not extract date from text "+  text.getId());
        }
        return datierung;
    }
    
  
    public List<String> getDate() {
        if (this.date == null) {
            this.date = extractDate(this);
        }
        return this.date;
    }

}
