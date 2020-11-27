package tla.web.model.ui;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorpusPathSegment extends BreadCrumb {

    private String type;
    private String eclass;

    public CorpusPathSegment(String href, String label) {
        super(href, label);
    }

    public CorpusPathSegment(String href, String label, String eclass, String type) {
        super(href, label);
        this.eclass = eclass;
        this.type = type;
    }

    public static CorpusPathSegment of(String href, String label, String eclass, String type) {
        return new CorpusPathSegment(href, label, eclass, type);
    }

}