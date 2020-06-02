package tla.web.model.ui;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BreadCrumb {

    private String href;
    private String label;

    public static BreadCrumb of(String href, String label) {
        return BreadCrumb.builder().href(href).label(label).build();
    }

    public static BreadCrumb of(String label) {
        return BreadCrumb.builder().label(label).build();
    }

}