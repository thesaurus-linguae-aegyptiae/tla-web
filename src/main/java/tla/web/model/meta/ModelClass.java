package tla.web.model.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put on top of object service in order to register the domain model class it is responsible for.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModelClass {

    public Class<? extends TLAObject> value();

}
