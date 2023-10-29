package com.techno.ms2.quartzscheduling.share.service;

import com.techno.ms2.quartzscheduling.share.model.SearchCriteria;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Handling implementation of method defined in the interface.
 *
 * @param <D> is the DTO class.
 * @param <E> is the Entity class.
 * @param <T> is the identity of Entity class {@link E}
 */
public abstract class AbstractCrudService<D, E, T> implements CrudService<D, T> {

  protected final ModelMapper modelMapper;

  protected AbstractCrudService(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Handle process mapping dto to entity.
   *
   * @param dto    refer to dto.
   * @param entity refer to entity.
   */
  protected E mapEntity(D dto, E entity) {
    modelMapper.map(dto, entity);
    return entity;
  }

  /**
   * Handle process mapping dto to entity.
   *
   * @param dto S refer to dto
   */
  protected E mapEntity(D dto) {
    return modelMapper.map(dto, this.getClassType(1));
  }

  /**
   * Handle process mapping entity to dto.
   *
   * @param dto    S refer to dto
   * @param entity E refer to entity
   */
  protected D mapData(E entity, D dto) {
    modelMapper.map(entity, dto);
    return dto;
  }

  /**
   * Handle process mapping entity to dto.
   *
   * @param entity E refer to entity
   */
  protected D mapData(E entity) {
    return modelMapper.map(entity, this.getClassType(0));
  }

  /**
   * Handling a process mapping collection of source entity to destination.
   *
   * @param entities the collection of source entities
   * @param outClass the destination entity
   * @param <L>      refer to destination entity to map
   * @param <S>      refer to source entity
   * @return the collect of destination entity
   */
  protected <L, S> List<L> mapAll(final Collection<S> entities, Class<L> outClass) {
    return entities.stream().map(entity -> modelMapper.map(entity, outClass)).toList();
  }

  /**
   * Handling a process mapping collection of source entity to destination.
   *
   * @param entities the collection of source entities
   * @param outClass the destination entity
   * @param <L>      refer to destination entity to map
   * @param <S>      refer to source entity
   * @return the collect of destination entity
   */
  protected <L, S> Page<L> mapAll(final Page<S> entities, Class<L> outClass) {
    return entities.map(entity -> modelMapper.map(entity, outClass));
  }

  @Override
  public D findById(T id) {
    return null;
  }

  @Override
  public List<D> findAll() {
    return new ArrayList<>();
  }

  @Override
  public Page<D> findAll(Pageable pageable) {
    return null;
  }

  @Override
  public Page<D> findAll(SearchCriteria searchCriteria) {
    return null;
  }

  @Override
  public Page<D> findAll(Pageable pageable, String filter) {
    return null;
  }

  @Override
  public D save(D dto) {
    return null;
  }

  @Override
  public D update(D dto) {
    return null;
  }

  @Override
  public void deleteAll(List<T> ids) {
  }

  @Override
  public void delete(T id) {
  }

  protected Type getClassType(int parameterTypeIndex) {
    ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
    return superClass.getActualTypeArguments()[parameterTypeIndex];
  }
}