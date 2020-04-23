package tla.web.service;

import tla.web.model.ObjectDetails;
import tla.web.model.TLAObject;

public abstract class ObjectService<T extends TLAObject> {

    public abstract ObjectDetails<T> get(String id);

}