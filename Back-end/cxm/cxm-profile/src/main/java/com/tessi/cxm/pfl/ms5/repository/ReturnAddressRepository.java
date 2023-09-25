package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.entity.ReturnAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnAddressRepository extends JpaRepository<ReturnAddress, Long> {
  List<ReturnAddress> findAllByClientId(long clientId);
  Optional<ReturnAddress> findByRefIdAndType(Long refId, AddressType type);
  Optional<ReturnAddress> findByRefIdAndTypeAndClientId(Long refId, AddressType type, long clientId);
  void deleteByRefIdAndType(Long refId, AddressType type);

  void deleteByClientId(long clientId);

}
