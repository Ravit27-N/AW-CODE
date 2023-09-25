package com.allweb.rms.service;

import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.entity.dto.CompanyProfileDTO;
import com.allweb.rms.entity.jpa.CompanyProfile;
import com.allweb.rms.repository.jpa.CompanyProfileRepository;
import com.allweb.rms.utils.StorageUtils;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class CompanyProfileService {

  private static final String COMPANY_FOLDER_NAME = "company";
  private static final String LOGO_NAME = "allweb-logo.jpg";
  private final CompanyProfileRepository companyProfileRepository;
  private final ModelMapper modelMapper;
  private final StorageUtils storageUtils;
  private final StorageObject companyFolder;

  @Autowired
  public CompanyProfileService(
      CompanyProfileRepository companyProfileRepository,
      ModelMapper modelMapper,
      StorageUtils storageUtils) {
    this.companyProfileRepository = companyProfileRepository;
    this.modelMapper = modelMapper;
    this.storageUtils = storageUtils;
    this.companyFolder = storageUtils.getSubDirectory(COMPANY_FOLDER_NAME);
  }

  public CompanyProfile convertToEntity(CompanyProfileDTO companyProfileDTO) {
    return modelMapper.map(companyProfileDTO, CompanyProfile.class);
  }

  public CompanyProfileDTO convertToDTO(CompanyProfile companyProfile) {
    return modelMapper.map(companyProfile, CompanyProfileDTO.class);
  }

  // update company Profile
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CompanyProfileDTO updateCompanyProfile(CompanyProfileDTO companyProfile) {
    CompanyProfileDTO companyProfileDTO = getCompanyProfile();
    companyProfile.setId(companyProfileDTO.getId());
    return convertToDTO(companyProfileRepository.save(convertToEntity(companyProfile)));
  }

  // create company profile
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CompanyProfileDTO createCompanyProfile(CompanyProfileDTO companyProfile) {
    return convertToDTO(companyProfileRepository.save(convertToEntity(companyProfile)));
  }

  // get company profile
  @Transactional(readOnly = true)
  public CompanyProfileDTO getCompanyProfile() {
    return convertToDTO(
        companyProfileRepository.findAll().stream().findFirst().orElse(new CompanyProfile()));
  }

  /**
   * Upload logo
   *
   * @param companyLogoUrl
   * @return
   */
  @SneakyThrows
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void uploadLogo(MultipartFile companyLogoUrl) {
    StorageObjectManager folderManager = companyFolder.getStorageObjectManager();
    if (folderManager.exists(LOGO_NAME)) {
      folderManager.getChild(LOGO_NAME).getStorageObjectManager().remove();
    }
    storageUtils.saveFile(companyLogoUrl.getInputStream(), LOGO_NAME, folderManager);
  }

  /**
   * Load profile or logo of Company
   *
   * @return
   */
  public Resource loadCompanyLogo() {
    return storageUtils.loadFile(LOGO_NAME, companyFolder);
  }
}
