package tla.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import tla.domain.dto.SentenceDto;
import tla.domain.dto.SentenceDto.SentenceContext;
import tla.domain.model.EditorInfo;
import tla.domain.model.Language;
import tla.domain.model.Passport;
import tla.domain.model.ObjectPath;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.Hierarchic;
import tla.domain.model.meta.TLADTO;
import tla.web.model.meta.BackendPath;
import tla.web.model.meta.TLAObject;
import tla.web.model.parts.Transcription;
import tla.web.model.parts.Token;
import tla.domain.model.ObjectReference;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("sentence")
@BTSeClass("BTSSentence")
@TLADTO(SentenceDto.class)
public class Sentence extends TLAObject implements Hierarchic {
	public static final String PASSPORT_PROP_DATE = "date.date.date";

    private SentenceContext context;

    private List<Token> tokens;

    private int wordCount;

    private Transcription transcription;

    private Text text;
    private List<DatePair> datierung;


    @Singular
    private SortedMap<Language, List<String>> translations;

    /**
     * Determine whether any of a sentence's tokens have any hieroglyph encodings.
     */
    public boolean hasGlyphs() {
        return this.tokens.stream().anyMatch( // for at least one token:
			token -> (token.getType().equals("word") && !token.getGlyphs().isEmpty()) // iff "word" token and there are hieroglyphs => true
        );
    }

    public boolean isArtificiallyAligned() {
		/*this.tokens.stream().forEach( // for at least one token:
			token -> System.out.println(token.getGlyphs().isMdcArtificiallyAligned())
        );*/
        return this.tokens.stream().anyMatch( // for at least one token:
			token -> token.getGlyphs().isMdcArtificiallyAligned() == true
        );
    }

    public String getName() {
        return this.getText() != null ? this.getText().getName() : null;
    }

    public String reviewState() {
        return this.getText() != null ? this.getText().getReviewState() : "published";
    }

    public EditorInfo getEdited() {
        return this.getText() != null ? this.getText().getEdited() : null;
    }

    private static List<DatePair> extractDatierung(Text text) {
        List<DatePair> datierung = new ArrayList<DatePair>();
        try {
        
          List<Passport> dates =text.getPassport().extractProperty(PASSPORT_PROP_DATE);
         
          for(int i=0;i<dates.size();i++) {
        	  for(int j=0;j<dates.get(i).extractObjectReferences().size();j++) {

        	/*	 
        	 System.out.println("Fields "+ i+ " "+ j+" "+ dates.get(i).extractObjectReferences().get(j).getName());
        	 String idths=dates.get(i).extractObjectReferences().get(j).getId();
        	 String nameths=dates.get(i).extractObjectReferences().get(j).getName();*/
        	  // ObjectReference.builder().eclass("BTSThsEntry").id(idths).name(nameths).type("date").build()
        	 //System.out.println("Fields "+ i+ " "+ j+" "+ dates.get(i).extractObjectReferences().get(j).getName());

        	 datierung.add(new DatePair(dates.get(i).extractObjectReferences().get(j).getId(),dates.get(i).extractObjectReferences().get(j).getName()));
        	  }
          }
            //System.out.println("Extracted "+text.getPassport().extractProperty("date.date.date").get(0).getContents());
        } catch (Exception e) {
           // log.debug("could not extract date from text {}", text.getId());
           System.out.println("could not extract bibliography from text "+  text.getId());
        }
        return datierung;
    }
    
  
    public List<DatePair> getDate() {
        if (this.datierung == null) {
            this.datierung = extractDatierung(this.getText());
        }
        return this.datierung;
    }
    @Override
    public List<ObjectPath> getPaths() {
		 // to add the corpus as the first paths element here ist too complicated
       return this.getText() != null ? this.getText().getPaths() : null;
    }
    
    public String getID() {
        return this.getId();
    }
    @Getter
    public static class DatePair {
    	String id;
    	String named;
    	
    	DatePair(String id, String name){
    		this.id=id;
    		this.named=name;
    	}
    }
}
