package org.olat.core.commons.persistence.manager;

import java.io.Serializable;
import java.util.List;

import org.olat.institute.feature.model.InstituteFeature;

public interface GenericDao <T, PK extends Serializable> {

    T create(T entity);
    List<T> create(List<T> entities);
    T load(PK id);
    List<T> load(List<PK> ids);
    void merge(T transientObject);
    void remove(T persistentObject);
    List<T> loadAll();
}