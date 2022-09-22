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
    @Setter(AccessLevel.NONE)
    private List<String> bibliography;
    @Setter(AccessLevel.NONE)
    private List<String> date;
    public List<String> getBibliography() {
        if (this.bibliography == null) {
            this.bibliography = extractBibliography(this);
        }
        return this.bibliography;
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
                        node.getLeafNodeValue().replaceAll("(\\r\\n|^)[\\s\\-]+", "$1").replaceAll("\\r\\n[\\r\\n\\s]*", "<br/>||").split("\\|\\|")
                    ).stream().map(
                        bibref -> bibref.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          System.out.println("could not extract bibliography from object {} "+corpusobj.getId());
        }
        return bibliography;
    }
    
    private static List<String> extractDate(CorpusObject corpus) {
        List<String> datierung = new ArrayList<String>();
        try {
        
          List<Passport> dates =corpus.getPassport().extractProperty(PASSPORT_PROP_DATE);
         
          for(int i=0;i<dates.size();i++) {
        	  for(int j=0;j<dates.get(i).extractObjectReferences().size();j++) {

        	 datierung.add(dates.get(i).extractObjectReferences().get(j).getName());
        	  }
          }
           
        } catch (Exception e) {
           // log.debug("could not extract date from text {}", text.getId());
           System.out.println("could not extract date from object ");
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