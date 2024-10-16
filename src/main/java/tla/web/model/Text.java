package tla.web.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import tla.domain.dto.TextDto;
import tla.domain.model.Language;
import tla.domain.model.ObjectReference;
import tla.domain.model.Passport;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.TLADTO;
import tla.web.model.CorpusObject.SynonymGroup;
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
    public static final String PASSPORT_PROP_DATE = "date.date.date";
    public static final String PASSPORT_PROP_LANGUAGE ="text.textual_metadata.language";
    public static final String PASSPORT_PROP_SCRIPT = "text.textual_metadata.script";
    public static final String PASSPORT_PROP_EGYTEXTNAME ="text.textual_metadata.egytextname";
    public static final String PASSPORT_PROP_COMMENTLANGUAGE ="text.textual_metadata.comment_on_language";
    public static final String PASSPORT_PROP_COMMENTTEXTTYPE ="text.textual_metadata.comment_on_texttype";
    public static final String PASSPORT_PROP_COMMENTSCRIPT ="text.textual_metadata.comment_on_script";
    public static final String PASSPORT_PROP_TEXTTYPE ="text.textual_metadata.texttype";
    public static final String PASSPORT_PROP_SECINSCRIPTION ="text.textual_metadata.secondary_inscription";
   
    public static final String PASSPORT_PROP_TRANSLITERATION = "definition.main_group.transliteration";
    public static final String PASSPORT_PROP_LEMMATIZATION = "definition.main_group.lemmatization";
    public static final String PASSPORT_PROP_GRAMMATICAL_ENCODING = "definition.main_group.grammatical_encoding";
    public static final String PASSPORT_PROP_HIEROGLYPHIC_ENCODING = "definition.main_group.hieroglyphic_encoding";
    public static final String PASSPORT_PROP_HIEROGLYPHS_SEQUENTIAL = "definition.main_group.hieroglyphs_sequential"; 
    public static final String PASSPORT_PROP_TRANSLATIONS = "definition.main_group.translations";
    public static final String PASSPORT_PROP_LINE_COUNT = "definition.main_group.line_count";

    //TODO prüfen ob doppelt, da bereits in CorpusObject.java
    public static final String PASSPORT_PROP_ORIGPLACE ="find_spot.find_spot.place.place";
    public static final String PASSPORT_PROP_ISORIG ="find_spot.find_spot.place.is_origin";
    public static final String PASSPORT_PROP_PRESLOC ="present_location.location.location";
    
    
    @Setter(AccessLevel.NONE)
    private List<String> bibliography; 
    @Setter(AccessLevel.NONE)
    private String textualMetadata;
    @Setter(AccessLevel.NONE)
    private ObjectReference language;
    @Setter(AccessLevel.NONE)
    private ObjectReference skript; 
    @Setter(AccessLevel.NONE)
    private String egytextname;
    @Setter(AccessLevel.NONE)
    private String commentlanguage;
    @Setter(AccessLevel.NONE)
    private String commenttexttype;
    @Setter(AccessLevel.NONE)
    private String commentscript;
    @Setter(AccessLevel.NONE)
    private ObjectReference texttype; 
    @Setter(AccessLevel.NONE)
    private Boolean secinscription;
    @Setter(AccessLevel.NONE)
    private List<String>origplace;
    @Setter(AccessLevel.NONE)
    private String isorig;
    @Setter(AccessLevel.NONE)
    private List<String> presloc;
    
    @Setter(AccessLevel.NONE)
    private List<String> transliteration_by;
    @Setter(AccessLevel.NONE)
    private List<String> lemmatization;
    @Setter(AccessLevel.NONE)
    private List<String> grammatical_encoding;
    @Setter(AccessLevel.NONE)
    private List<String> hieroglyphic_encoding;
    @Setter(AccessLevel.NONE)
    private String hieroglyphs_sequential;
    @Setter(AccessLevel.NONE)
    private List<TranslationGroup> translations;
    @Setter(AccessLevel.NONE)
    private List<String> lineCountInfo;
    
	public static class TranslationGroup {
		private String language;
		private List<String> translation;

		public TranslationGroup(Passport passport) {
			this.language = extractString(passport, "language");
			this.translation = extractMultilineText(passport, "translation");
		}

		public String getLanguage() {
			return this.language;
		}

		public List<String> getTranslation() {
			return this.translation;
		}
	}
        
 /*  public String getOneSentence() {
	   return this.getSentence().get(0).getTranscription().getUnicode();
   }
    */
    public List<String> getBibliography() {
        if (this.bibliography == null) {
            this.bibliography = extractMultilineText(this.getPassport(), PASSPORT_PROP_BIBL);
        }
        return this.bibliography;
    }

    //TODO prüfen auf schematischeren Weg
    public boolean isEmptyTextualMetadata(){
		this.textualMetadata = extractString(this.getPassport(), "text.textual_metadata");
		if (this.textualMetadata == null) { return true;}
		else { return false;}
    }

    //TODO Check if generic function could replace extractLanguage
    public ObjectReference getLanguage() {
        if (this.language == null) {
            this.language = extractObjectReference(this,PASSPORT_PROP_LANGUAGE);
        }
        return this.language;
    }  
    
    //TODO Skript vs. Script vereinheitlichen mit details.html und Datenmodell
    public ObjectReference getSkript() {
        if (this.skript == null) {
            this.skript = extractObjectReference(this,PASSPORT_PROP_SCRIPT);
        }
        return this.skript;
    }
    
    public String getEgytextname() {
    	if (this.egytextname == null) {
    		this.egytextname = extractString(this.getPassport(), PASSPORT_PROP_EGYTEXTNAME);
    	}
    	return this.egytextname;
    }
    
    public String getCommentlanguage() {
    	if(this.commentlanguage == null) {
    		this.commentlanguage = extractString(this.getPassport(), PASSPORT_PROP_COMMENTLANGUAGE);
    	}
    	return this.commentlanguage;
    }

    public String getCommenttexttype() {
    	if (this.commenttexttype == null) {
    		this.commenttexttype = extractString(this.getPassport(),PASSPORT_PROP_COMMENTTEXTTYPE);
    	}
    	return this.commenttexttype;
    }
    
    public String getCommentscript() {
    	if(this.commentscript == null) {
    		this.commentscript = extractString(this.getPassport(),PASSPORT_PROP_COMMENTSCRIPT );
    	}
    	return this.commentscript;
    }
    
    public ObjectReference getTexttype(){
    	if(this.texttype == null) {
    		this.texttype = extractObjectReference(this, PASSPORT_PROP_TEXTTYPE);
    	}
    	return this.texttype;
    }
    
    public Boolean getSecinscription() {
    	if(this.secinscription == null) {
    	this.secinscription = Boolean.parseBoolean(extractString(this.getPassport(),PASSPORT_PROP_SECINSCRIPTION));
    	}
    	return this.secinscription;
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
    
    public List<String> getTransliteration_by() {
        if (this.transliteration_by == null) {
            this.transliteration_by = extractMultilineText(this.getPassport(), PASSPORT_PROP_TRANSLITERATION);
        }
        return this.transliteration_by;
    }

    public List<String> getLemmatization() {
        if (this.lemmatization == null) {
            this.lemmatization = extractMultilineText(this.getPassport(), PASSPORT_PROP_LEMMATIZATION);
        }
        return this.lemmatization;
    }
    
    public List<String> getGrammatical_encoding() {
        if (this.grammatical_encoding == null) {
            this.grammatical_encoding = extractMultilineText(this.getPassport(), PASSPORT_PROP_GRAMMATICAL_ENCODING);
        }
        return this.grammatical_encoding;
    }
    
    public List<String> getHieroglyphic_encoding() {
        if (this.hieroglyphic_encoding == null) {
            this.hieroglyphic_encoding = extractMultilineText(this.getPassport(),PASSPORT_PROP_HIEROGLYPHIC_ENCODING);
        }
        return this.hieroglyphic_encoding;
    }
    
    public Boolean isHieroglyphs_sequential(){
    	return this.getHieroglyphs_sequential().equalsIgnoreCase("true");
    }
    
    public String getHieroglyphs_sequential() { 
	   	 if (this.hieroglyphs_sequential == null) {
	         this.hieroglyphs_sequential = extractString(this.getPassport(),PASSPORT_PROP_HIEROGLYPHS_SEQUENTIAL);
	     }
		 return this.hieroglyphs_sequential;
	 }
    
    public List<TranslationGroup> getTranslations() {
    	if(this.translations == null) {
    		this.translations = extractGroups(this.getPassport(), PASSPORT_PROP_TRANSLATIONS);
    	}
		return this.translations;
	}
    
    public List<String> getLineCountInfo() {
    	if(this.lineCountInfo== null) {
    		this.lineCountInfo = extractMultilineText(this.getPassport(), PASSPORT_PROP_LINE_COUNT);
    	}
		return this.lineCountInfo;
	}
    
   
    
    //TODO generic
    private static List<TranslationGroup> extractGroups(Passport passport, String searchString) {
		List<TranslationGroup> groups = new ArrayList<TranslationGroup>();
		try {
			passport.extractProperty(searchString)
					.forEach(node -> groups.add(new TranslationGroup(node)));
		} catch (Exception e) {
		}
		return groups;
	}

    //Extracts a single ObjectReference (Ths-Entry) from the first found passport according to searchString
        private static ObjectReference extractObjectReference(Text text, String searchString) {
        ObjectReference objectReference = null;
        try {
           Passport pass= text.getPassport().extractProperty(searchString).get(0);
            objectReference = pass.extractObjectReferences().get(0);        
        } catch (Exception e) {
          // System.out.println("could not extract language from text {} "+text.getId());
        }
        return objectReference;
    }
             
        //Function to return a List of names form a passport Array
        //TODO think of move to passport.java ; also a copy in BTSObject.java (why is it not inherited properly?)
        private static List<String> extractNamesOfArray(Text text, String searchField) {
            List<String> values = new ArrayList<>();
            try {
               List<Passport> pass= text.getPassport().extractProperty(searchField);
                for(int i=0;i<pass.size();i++) {
                	values.add(pass.get(i).extractObjectReferences().get(0).getName());
                }           
            } catch (Exception e) {
              // System.out.println("could not extract" + searchField + "from text {} "+text.getId());
            }
            return values;
        }    
      
        //TODO prüfen auf unnötig
    private static String extractIsOrigPlace(Text text) {
        String isOrigPl = new String();
        try {
           List<Passport> pass= text.getPassport().extractProperty(PASSPORT_PROP_ISORIG);
           
            	isOrigPl=pass.get(0).toString();
            	//System.out.println("isOrig "+ isOrigPl);
            		if (isOrigPl.equals("true")) isOrigPl="original";
            		else isOrigPl=null;       
        } catch (Exception e) {
          // System.out.println("could not extract is orig place from text {} "+text.getId());isOrigPl=null;
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
          // System.out.println("could not extract original place from text {} "+text.getId());
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
          // System.out.println("could not extract present place from text {} "+text.getId());
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
           // System.out.println("could not extract date from text "+  text.getId());
        }
        return datierung;
    }
}