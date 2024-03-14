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
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import tla.domain.dto.LemmaDto;
import tla.domain.model.Language;
import tla.domain.model.Script;
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.TLADTO;
import tla.web.model.meta.BTSObject;
import tla.web.model.meta.BackendPath;
import tla.web.model.parts.GlyphsLemma;
import tla.web.model.parts.Token;
import tla.web.model.parts.extra.AttestedTimespan;

@Slf4j
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@BackendPath("lemma")
@TLADTO(LemmaDto.class)
@BTSeClass("BTSLemmaEntry")
public class Lemma extends BTSObject {

    /**
     * The passport locator where bibliographical information should be stored.
     */
    public static final String PASSPORT_PROP_BIBL = "bibliography.bibliographical_text_field";

    @Setter(AccessLevel.NONE)
    private List<String> bibliography;

    @Singular
    private SortedMap<Language, List<String>> translations;
    
  
    private SortedMap<String, List<String>> transcription;

    @Singular
    private List<Token> words;
    
    private GlyphsLemma glyphs;

    @Singular
    private List<AttestedTimespan> attestations;

    /**
     * first and last year
     */
    private AttestedTimespan.Period timespan;

    /**
     * compute sum of sentence counts across all nested attestation timespans.
     */
    public Long getAttestationCount() {
      return this.attestations.stream().mapToLong(
          timespan -> timespan.getTotal().getSentences()
      ).sum();
    }

    /**
    * Determines the language phase this lemma belongs to
    */
    public Script getDictionaryName() {
        return Script.ofLemmaId(this.getId());
    }

    /**
     * Extract hieroglyphs from lemma words.
     * Return null if only empty hieroglyphs can be found.
     *
     * @return List of all lemma word hieroglyphs, or null if there are no hieroglyphs at all
     */
    public GlyphsLemma getHieroglyphs()
   
    {
    	 if (this.getDictionaryName().equals(Script.HIERATIC)) {
    		 //System.out.println ("Glyphs Lemma"+ this.getGlyphs().getMdcCompact());
    		 //System.out.println ("Unicode Lemma"+ this.getGlyphs().getUnicode());
    		 return this.getGlyphs();
    	 }
    	 else return null;
    	    		
    }
    /*public List<Glyphs> getHieroglyphs() {
        if (this.getDictionaryName().equals(Script.HIERATIC)) {
        	
            List<Glyphs> hieroglyphs = this.getWords().stream().map(
                Token::getGlyphs
            ).collect(
                Collectors.toList()
            );
            return (hieroglyphs.stream().allMatch(
                glyphs -> glyphs == null || glyphs.isEmpty()
            )) ? null : hieroglyphs;
        	}
        	
        
        return null;
    }*
  /*  public List<Glyphs> getHieroglyphs() {
        if (this.getDictionaryName().equals(Script.HIERATIC)) {
        	return getGlyphs();
        }
        return null;
        
    }*/
    

    /**
     * Returns a list of bibliographic references extracted from this lemma's
     * <code>bibliography.bibliographical_text_field</code> passport field.
     *
     * @see {@link #extractBibliography(Lemma)}
     */
    public List<String> getBibliography() {
        if (this.bibliography == null) {
            this.bibliography = extractBibliography(this);
        }
        return this.bibliography;
    }

    /**
     * Extract bibliographic information from lemma passport.
     *
     * Bibliography is being copied from the <code>bibliography.bibliographical_text_field</code>
     * passport field. The value(s) found under that locator are split at semicolons <code>";"</code>.
     *
     * @param lemma The Lemma instance from whose passport the bibliography is to be extracted.
     * @return List of textual bibliographic references or an empty list
     */
    private static List<String> extractBibliography(Lemma lemma) {
        List<String> bibliography = new ArrayList<>();
        try {
            lemma.getPassport().extractProperty(
                PASSPORT_PROP_BIBL
            ).forEach(
                node -> bibliography.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().split(";")
                    ).stream().map(
                        bibref -> bibref.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
            // log.debug("INFO: could not extract bibliography from lemma {}", lemma.getId());
        }
        return bibliography;
    }

    // Description
    
    public static final String PASSPORT_PROP_DESCR = "definition.main_group.definition";

    @Setter(AccessLevel.NONE)
    private List<String> description;

    public List<String> getDescription() {
        if (this.description == null) {
            this.description = extractDescription(this);
        }
        return this.description;
    }

    private static List<String> extractDescription(Lemma lemma) {
        List<String> description = new ArrayList<>();
        try {
            lemma.getPassport().extractProperty(
                PASSPORT_PROP_DESCR
            ).forEach(
                node -> description.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().replaceAll("\\s*\\r?\\n", "||").split("\\|\\|") 
                    ).stream().map(
                        descr -> descr.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract description from lemma "+lemma.getId());
        }
        return description;
    }
    
    // File comment
    
    public static final String PASSPORT_PROP_FILECOMMENT = "definition.main_group.comment";

    @Setter(AccessLevel.NONE)
    private List<String> fileComment;

    public List<String> getFileComment() {
        if (this.fileComment == null) {
            this.fileComment = extractFileComment(this);
        }
        return this.fileComment;
    }

    private static List<String> extractFileComment(Lemma lemma) {
        List<String> fileComment = new ArrayList<>();
        try {
            lemma.getPassport().extractProperty(
                PASSPORT_PROP_FILECOMMENT
            ).forEach(
                node -> fileComment.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().replaceAll("\\s*\\r?\\n", "||").split("\\|\\|") 
                    ).stream().map(
                        comment -> comment.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          // System.out.println("INFO: Could not extract file comment from lemma "+lemma.getId());
        }
        return fileComment;
    }

    /**
     * Checks whether there is an annotation or two among a lemma's relations.
     */
    public boolean isAnnotated() {
        return this.getRelations().values().stream().anyMatch(
            refs -> refs.stream().anyMatch(
                ref -> ref.getEclass().equals("BTSAnnotation")
            )
        );
    }

}
