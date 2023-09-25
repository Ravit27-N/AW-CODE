package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.ClientFillers;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientFillersRepository extends JpaRepository<ClientFillers, Long> {

  List<ClientFillers> findByClientIdAndEnabledTrue(long clientId);
}
