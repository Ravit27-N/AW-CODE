package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.Functionalities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionalitiesRepository extends JpaRepository<Functionalities, Long> {
  List<Functionalities> findAllByKeyIn(List<String> key);
  List<Functionalities> findAllByClientFunctionalitiesDetailsClientId(long clientId);
}
