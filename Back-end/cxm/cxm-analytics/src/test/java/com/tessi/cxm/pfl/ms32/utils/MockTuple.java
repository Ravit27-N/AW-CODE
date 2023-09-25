package com.tessi.cxm.pfl.ms32.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;

public class MockTuple implements Tuple {
  private Map<Object, Object> tuple = new HashMap<>();

  public MockTuple(){}

  @Override
  public <X> X get(TupleElement<X> tupleElement) {
    return null;
  }

  @Override
  public <X> X get(String s, Class<X> aClass) {
    return (X) tuple.get(s);
  }

  @Override
  public Object get(String s) {
    return null;
  }

  @Override
  public <X> X get(int i, Class<X> aClass) {
    return (X) tuple.get(i);
  }

  @Override
  public Object get(int i) {
    return tuple.get(i);
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public List<TupleElement<?>> getElements() {
    return null;
  }

  public void setTuple(Object index, Object value){
    tuple.put(index, value);
  }
}
