package com.innovationandtrust.utils.corporateprofile.feignclient;

import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.processcontrol.EmailTheme;
import com.innovationandtrust.share.model.profile.Company;
import com.innovationandtrust.share.model.profile.CompanyIdListDTO;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.utils.feignclient.FeignClientMultipartConfiguration;
import feign.Headers;
import jakarta.validation.Valid;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/** CorporateProfileFeignClient use to communicate with corporate profile service. */
@FeignClient(
    name = "corporate-profile",
    url = "${signature.feign-client.clients.corporate-profile-url}",
    path = "${signature.feign-client.contexts.corporate-profile-context-path}",
    configuration = FeignClientMultipartConfiguration.class)
public interface CorporateProfileFeignClient {

  Logger log = Logger.getLogger(CorporateProfileFeignClient.class.getName());

  @GetMapping("/v1/employees/corporate/{id}")
  List<EmployeeDTO> getEmployeesOfCorporate(@PathVariable("id") Long id);

  @GetMapping("/v1/employees/user/{id}")
  List<EmployeeDTO> getEmployeesByUser(@PathVariable("id") Long id);

  @GetMapping("/v1/corporate/settings/themes/{userId}")
  Company findOwnTheme(@PathVariable(name = "userId") Long userId);

  @GetMapping("/corporate-settings/view")
  String viewFile(@RequestParam("fileName") String fileName);

  @GetMapping("/corporate-settings/view/content")
  Resource viewFileContent(@RequestParam(value = "fileName", defaultValue = "") String fileName);

  @PostMapping(value = "/corporate-settings/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Headers({"Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE})
  String uploadLogo(@RequestPart("file") MultipartFile file);

  /**
   * To retrieve the theme of the user.
   *
   * @param userId refers to the identity of a user
   * @return object of {@link EmailTheme}
   */
  default EmailTheme getLogo(Long userId) {
    try {
      var company = this.findOwnTheme(userId);
      var logo = viewFileContent(company.getTheme().get(0).getLogo());
      return new EmailTheme(company.getName(), company.getTheme().get(0).getMainColor(), logo);
    } catch (Exception e) {
      log.log(Level.ALL, "Failed for loading logo", e);
      return new EmailTheme(null, null, null);
    }
  }


  default EmailTheme getLogoByCompany(Company company) {
    try {
      var logo = viewFileContent(company.getTheme().get(0).getLogo());
      return new EmailTheme(company.getName(), company.getTheme().get(0).getMainColor(), logo);
    } catch (Exception e) {
      log.log(Level.ALL, "Failed for loading logo", e);
      return new EmailTheme(null, null, null);
    }
  }

  @PostMapping("/v1/corporate/settings/company")
  List<CorporateSettingDto> findByCompany(@RequestBody CompanyIdListDTO dto);

  @GetMapping("/v1/company/details/business-unit/{id}")
  Long getCompanyId(@PathVariable("id") Long id);

  @PutMapping("/v1/corporate/settings/update")
  CorporateSettingDto updateSetting(
      @RequestBody @Valid CorporateSettingDto dto, @RequestParam(value = "oldFile") String oldFile);

  @PostMapping("/v1/corporate/settings/levels")
  void saveCompanySetting(@RequestBody List<CompanySettingDto> settingDtoList);

  @GetMapping("v1/corporate/settings/levels")
  List<CompanySettingDto> findCompanySettings(@RequestParam(value = "uuid") String companyUuid);

  @GetMapping("v1/corporate/settings/level/{uuid}")
  CompanySettingDto getCompanySettingByLevel(
          @PathVariable("uuid") String companyUuid,
          @RequestParam("signatureLevel") String signatureLevel);

  @GetMapping("/v1/company/details/info/{userId}")
  CorporateInfo findCorporateInfo(@PathVariable("userId") Long userId);
}
