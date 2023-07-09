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
import tla.domain.model.meta.BTSeClass;
import tla.domain.model.meta.Hierarchic;
import tla.domain.model.meta.TLADTO;
import tla.web.model.meta.BTSObject;
import tla.web.model.meta.BackendPath;

@Getter
@Setter
@NoArgsConstructor
@BackendPath("ths")
@BTSeClass("BTSThsEntry")
@TLADTO(ThsEntryDto.class)
public class ThsEntry extends BTSObject implements Hierarchic {

    @Singular
    private SortedMap<Language, List<String>> translations;

    private List<ObjectPath> paths;

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

    private static List<String> extractDescription(ThsEntry thsEntry) {
        List<String> description = new ArrayList<>();
        try {
            thsEntry.getPassport().extractProperty(
                PASSPORT_PROP_DESCR
            ).forEach(
                node -> description.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1").replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|")
                    ).stream().map(
                        descr -> descr.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          System.out.println("INFO: Could not extract description from thesaurus entry "+thsEntry.getId());
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

    private static List<String> extractFileComment(ThsEntry thsEntry) {
        List<String> fileComment = new ArrayList<>();
        try {
            thsEntry.getPassport().extractProperty(
                PASSPORT_PROP_FILECOMMENT
            ).forEach(
                node -> fileComment.addAll(
                    Arrays.asList(
                        node.getLeafNodeValue().replaceAll("(\\r?\\n|^)[\\s\\-]+", "$1").replaceAll("\\r?\\n[\\r?\\n\\s]*", "||").split("\\|\\|")
                    ).stream().map(
                        comment -> comment.strip()
                    ).collect(
                        Collectors.toList()
                    )
                )
            );
        } catch (Exception e) {
          System.out.println("INFO: Could not extract file comment from thesaurus entry "+thsEntry.getId());
        }
        return fileComment;
    }
   
}