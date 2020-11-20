package tla.web.repo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tla.web.model.meta.TLAObject;

/**
 * TODO do we even need this? in the backend, each standalone class in the domain model has a dedicated service
 * which is being instantiated by the framework, and we have its respective model class be registered then.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModelClasses {

    public Class<? extends TLAObject>[] value();

}