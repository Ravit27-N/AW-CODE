package com.innovationandtrust.project.service;

import com.innovationandtrust.project.model.dto.DocumentDetailDTO;
import com.innovationandtrust.project.model.dto.DocumentDetailRequest;
import com.innovationandtrust.project.model.entity.DocumentDetail;
import com.innovationandtrust.project.repository.DocumentDetailRepository;
import com.innovationandtrust.share.service.AbstractCrudService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** DocumentDetailService has method such as save. */
@Service
@Slf4j
public class DocumentDetailService
    extends AbstractCrudService<DocumentDetailDTO, DocumentDetail, Long> {
  private final DocumentDetailRepository documentDetailRepository;

  protected DocumentDetailService(
      ModelMapper modelMapper, DocumentDetailRepository documentDetailRepository) {
    super(modelMapper);
    this.documentDetailRepository = documentDetailRepository;
  }

  /**
   * Insert a DocumentDetail record.
   *
   * @param documentDetailRequests projectId
   */
  @Transactional(rollbackFor = Exception.class)
  public void save(List<DocumentDetailRequest> documentDetailRequests) {
    documentDetailRequests.forEach(
        dt -> this.documentDetailRepository.save(this.modelMapper.map(dt, DocumentDetail.class)));
  }
}
