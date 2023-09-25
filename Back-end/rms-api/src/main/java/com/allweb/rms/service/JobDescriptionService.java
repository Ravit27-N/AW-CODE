package com.allweb.rms.service;

import com.allweb.rms.component.JobDescriptionAssembler;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.entity.dto.JobDescriptionDTO;
import com.allweb.rms.entity.jpa.JobDescription;
import com.allweb.rms.exception.JobDescriptionNotFoundException;
import com.allweb.rms.repository.jpa.JobDescriptionRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.rms.utils.StorageUtils;
import com.allweb.rms.utils.UUIDUtils;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@Log4j2
public class JobDescriptionService {

  /**
   * Class Service of JobDescription
   *
   * <p>> Do business logic ....
   */
  private static final String MSG_FORMAT = "JobDescription id %s not found";
  // field
  private static final String JOB_DESCRIPTION_FOLDER_NAME = "job-description";
  private final JobDescriptionRepository repository;
  private final JobDescriptionAssembler assembler;
  private final TemporaryStorageService storageService;
  private final ModelMapper mapper;
  private final StorageUtils storageUtils;
  private final Executor simpleThreadPoolTaskExecutor;
  private final StorageObject storageObject;

  @Autowired // Object Injection by constructor
  public JobDescriptionService(
      JobDescriptionRepository repository,
      JobDescriptionAssembler assembler,
      TemporaryStorageService storageService,
      ModelMapper mapper,
      StorageUtils storageUtils1,
      Executor simpleThreadPoolTaskExecutor,
      StorageUtils storageUtils) {
    this.repository = repository;
    this.assembler = assembler;
    this.storageService = storageService;
    this.mapper = mapper;
    this.storageUtils = storageUtils1;
    this.simpleThreadPoolTaskExecutor = simpleThreadPoolTaskExecutor;
    this.storageObject = storageUtils.getSubDirectory(JOB_DESCRIPTION_FOLDER_NAME);
  }

  /**
   * Method use to get JobDesc
   *
   * @param id of JobDesc object
   * @return {@link JobDescriptionDTO} result
   */
  @Transactional(readOnly = true)
  public JobDescriptionDTO getJobDescription(int id) {
    return convertToDTO(
        repository
            .findByIdAndActiveIsTrue(id)
            .orElseThrow(() -> new JobDescriptionNotFoundException(String.format(MSG_FORMAT, id))));
  }

  /**
   * Method use to generate page of list JobDesc
   *
   * @param filter is value of string that u want to get it from JobDesc
   * @param size is number of page
   * @param page is number of page index
   * @param sortByField is field of JobDesc entity
   * @param sortDirection is direction sort ex(ASC,DESC)
   * @param actives is boolean of active
   * @return {@link Pageable}
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<EntityModel<JobDescriptionDTO>> getJobDescriptions(
      int page,
      int size,
      String filter,
      String sortDirection,
      String sortByField,
      List<Boolean> actives) {
    Pageable pageable =
        PageRequest.of(
            page - 1, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    Page<EntityModel<JobDescriptionDTO>> map;
    if (!Strings.isNullOrEmpty(filter) && !CollectionUtils.isEmpty(actives)) {
      map =
          repository
              .getAllByFilterAndActive(filter, actives, pageable)
              .map(this::convertToEntityModel);
    } else if (!CollectionUtils.isEmpty(actives)) {
      map = repository.getAllByActiveIn(actives, pageable).map(this::convertToEntityModel);
    } else if (!Strings.isNullOrEmpty(filter)) {
      map = repository.getAllByFilter(filter, pageable).map(this::convertToEntityModel);
    } else map = repository.findAll(pageable).map(this::convertToEntityModel);
    return new EntityResponseHandler<>(map);
  }

  /**
   * Method use to performance save and updateJonDescription JobDesc
   *
   * @param dto is object of JobDesc
   * @return {@link JobDescriptionDTO} object result
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public JobDescriptionDTO save(JobDescriptionDTO dto) {
    JobDescription jobDesc =
        dto.getId() > 0
            ? repository
                .findById(dto.getId())
                .orElseThrow(
                    () ->
                        new JobDescriptionNotFoundException(String.format(MSG_FORMAT, dto.getId())))
            : convertToEntity(dto);
    if (dto.getId() > 0) {
      this.mapper.map(dto, jobDesc);
    }
    if (StringUtils.isNotBlank(jobDesc.getFilename())) {
      jobDesc.setFilename(UUIDUtils.removeUUIDFromStart("_", jobDesc.getFilename()));
    }
    jobDesc = repository.save(jobDesc);
    final int jobId = jobDesc.getId();
    // Move file from temp to real directory
    if (!Strings.isNullOrEmpty(dto.getFilename())) {
      simpleThreadPoolTaskExecutor.execute(
          () -> moveFile(jobId, Collections.singletonList(dto.getFilename())));
    }
    return convertToDTO(jobDesc);
  }

  /**
   * Method use to performance save and updateJonDescription JobDesc
   *
   * @param id of JobDesc object @not null
   * @param active status @not null
   * @return {@link JobDescriptionDTO} object result
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public JobDescriptionDTO updateActive(int id, boolean active) {
    JobDescriptionDTO dto =
        convertToDTO(
            repository
                .findById(id)
                .orElseThrow(
                    () -> new JobDescriptionNotFoundException(String.format(MSG_FORMAT, id))));
    dto.setActive(active);
    return save(dto);
  }

  /**
   * Method use to performance delete JobDesc
   *
   * @param id of JobDesc object
   * @return void
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(int id) {
    repository.delete(
        repository
            .findById(id)
            .orElseThrow(() -> new JobDescriptionNotFoundException(String.format(MSG_FORMAT, id))));
  }

  /**
   * Method convertor to (DTO and Entity)
   *
   * @param jobDescription Entity , dto
   * @return DTO and Entity
   */
  public JobDescriptionDTO convertToDTO(JobDescription jobDescription) {
    return mapper.map(jobDescription, JobDescriptionDTO.class);
  }

  public JobDescription convertToEntity(JobDescriptionDTO dto) {
    return mapper.map(dto, JobDescription.class);
  }

  public EntityModel<JobDescriptionDTO> convertToEntityModel(JobDescription jobDescription) {
    return assembler.toModel(mapper.map(jobDescription, JobDescriptionDTO.class));
  }
  // set empty string to filename of job description
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void removeFilenameFromEntity(int id) {
    Optional<JobDescription> byId = repository.findById(id);
    if (byId.isPresent()) {
      JobDescription jobDescription = byId.get();
      jobDescription.setFilename("");
      repository.save(jobDescription);
    } else throw new JobDescriptionNotFoundException(String.format(MSG_FORMAT, id));
  }

  // upload new file to temp
  public Map<String, String> uploadFile(MultipartFile file) {
    return Collections.singletonMap("fileName", storageService.uploadToTemporaryStorage(file));
  }
  // move file from temp to real directory
  @SneakyThrows
  public void moveFile(int jobId, List<String> fileList) {
    StorageObject directory =
        storageObject.getStorageObjectManager().createDirectory(String.valueOf(jobId));
    storageService.moveTo(fileList, directory, true);
  }
  // delete file from server
  @SneakyThrows
  public void removeFile(int id, String filename) {
    StorageObject child =
        storageObject
            .getStorageObjectManager()
            .getChild(String.valueOf(id))
            .getStorageObjectManager()
            .getChild(filename);
    if (child.getName().startsWith(filename)) {
      removeFilenameFromEntity(id);
      child.getStorageObjectManager().remove();
    }
  }
  // upload new file to existed directory
  @SneakyThrows
  public Map<String, String> uploadProfileOnUpdate(int id, MultipartFile filename) {
    String uuid = UUID.randomUUID().toString(); // uuid_fileName.extension
    String profileName = uuid + "_" + filename.getOriginalFilename();
    storageUtils.saveFile(
        filename.getInputStream(),
        profileName,
        storageObject
            .getStorageObjectManager()
            .getChild(String.valueOf(id))
            .getStorageObjectManager());
    return Collections.singletonMap("fileName", profileName);
  }

  // loading resource from server
  @SneakyThrows
  public Map<String, Object> loadFile(String id, String filename, HttpServletRequest request) {
    Map<String, Object> resourceMap = new HashMap<>();
    Resource resource =
        storageUtils.loadFile(filename, storageObject.getStorageObjectManager().getChild(id));

    String contentType =
        request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    // Fallback to the default content type if type could not be determined
    if (contentType == null) {
      contentType = "application/octet-stream"; // unknown binary file
    }
    resourceMap.put("contentType", contentType);
    resourceMap.put("resource", resource);
    return resourceMap;
  }
}
