package com.innovationandtrust.share.service;

import com.innovationandtrust.share.model.SearchCriteria;
import java.text.ParseException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @param <E> Entity
 * @param <T> id of the entity
 * **/
public interface CrudService <E,T>{
    E findById(T id);

    List<E> findAll();

    Page<E> findAll(Pageable pageable);

    Page<E> findAll(SearchCriteria searchCriteria);

    Page<E> findAll(Pageable pageable, String filter);

    E save(E entity) throws ParseException;

    E update(E entity);

    void delete(T id);

    void deleteAll(List<T> ids);
}