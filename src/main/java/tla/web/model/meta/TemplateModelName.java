package tla.web.model.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For insertion in type-specific CSS class names and stuff. Also the name of the subdirectory
 * where a domain model class view controller (i.e. a {@link ObjectController} subclass) looks
 * up the HTML templates it needs in order to render views targeted at objects of the model class
 * it is dedicated to.
 *
 * <p>Put this on top of {@link ObjectController} subclasses.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TemplateModelName {

    public String value();

}
