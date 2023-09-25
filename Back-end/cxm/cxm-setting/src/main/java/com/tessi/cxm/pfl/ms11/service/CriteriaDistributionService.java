package com.tessi.cxm.pfl.ms11.service;

import com.tessi.cxm.pfl.ms11.constant.ClientSettingFunctionalities;
import com.tessi.cxm.pfl.ms11.entity.CriteriaDistribution;
import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.ms11.repository.CriteriaDistributionRepository;
import com.tessi.cxm.pfl.ms11.util.SettingPrivilegeUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerRequest;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionRequest;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.model.setting.criteria.Preference;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.CriteriaDistributionChannel;
import com.tessi.cxm.pfl.shared.utils.HubDigitalFlowHelper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CriteriaDistributionService implements SharedService {

  private final CriteriaDistributionRepository criteriaDistributionRepository;
  private final HubDigitalFlow hubDigitalFlow;
  private final HubDigitalFlowHelper hubDigitalFlowHelper;

  private final ProfileFeignClient profileFeignClient;

  @Autowired
  public void setSettingPrivilegeUtil(ProfileFeignClient profileFeignClient) {
    SettingPrivilegeUtil.setProfileFeignClient(profileFeignClient);
  }

  @Autowired
  public void setHubDigitalFlowHelper(AESHelper aesHelper) {
    hubDigitalFlowHelper.setAesHelper(aesHelper);
  }

  /**
   * Get all criteria distributions of a customer.
   *
   * @param customer Customer name
   * @return {@link CriteriaDistributionsResponse}
   */
  @Transactional(readOnly = true)
  public CriteriaDistributionsResponse getCriteriaDistribution(Optional<String> customer) {
    // None admin user
    var refCustomer =
        customer.orElseGet(
            () -> this.profileFeignClient.getUserDetail(this.getAuthorizedToken()).getClientName());
    // Admin user
    if (customer.isPresent()) {
      SettingPrivilegeUtil.validateAdminRequest(refCustomer);
    }
    CriteriaDistributionsResponse criteriaDistributionsResponse =
        new CriteriaDistributionsResponse();
    criteriaDistributionsResponse.setCustomer(refCustomer);

    List<CriteriaDistribution> persistedCriteriaList =
        this.criteriaDistributionRepository.findAllCriteria(
            refCustomer, criteriaDistributionsResponse.getCriteriaNames());

    persistedCriteriaList.forEach(
        criteriaDistribution -> {
          Preference updatingCriteria =
              criteriaDistributionsResponse.getCriteria(criteriaDistribution.getName());
          updatingCriteria.setActive(criteriaDistribution.isActive());
        });

    return criteriaDistributionsResponse;
  }

  /**
   * check criteria distributions of a customer is activated.
   *
   * @param customer Customer name
   * @return {@link CriteriaDistributionsResponse}
   */
  @Transactional(readOnly = true)
  public boolean isActive(String customer, String channel) {
    List<String> subDigitalChannels =
        List.of(
            ClientSettingFunctionalities.EMAILING_CAMPAIGN.getSubValue().toLowerCase(),
            ClientSettingFunctionalities.SMS_CAMPAIGN.getSubValue().toLowerCase());
    if (subDigitalChannels.contains(channel.toLowerCase())) {
      List<String> digitalChanels =
          List.of(
              CriteriaDistributionChannel.DIGITAL.getValue().toLowerCase(), channel.toLowerCase());
      List<CriteriaDistribution> criteriaDistributions =
          this.criteriaDistributionRepository.findByCustomerIgnoreCaseAndNameInIgnoreCase(
              customer, digitalChanels);
      if (criteriaDistributions.stream()
          .map(CriteriaDistribution::getName)
          .map(String::toLowerCase)
          .noneMatch(s -> CriteriaDistributionChannel.DIGITAL.getValue().equalsIgnoreCase(s))) {
        return false;
      }
      return criteriaDistributions.stream().allMatch(CriteriaDistribution::isActive);
    }

    return this.criteriaDistributionRepository
        .existsByCustomerIgnoreCaseAndNameIgnoreCaseAndIsActiveTrue(customer, channel);
  }

  @Transactional(rollbackFor = Exception.class)
  public CriteriaDistributionRequest updateCriteriaDistributions(
      CriteriaDistributionRequest request) {
    final var customer = request.getCustomer();

    SettingPrivilegeUtil.validateAdminRequest(customer);

    var criteriaDistribution =
        this.criteriaDistributionRepository
            .findCriteria(customer, request.getPreference().getName())
            .orElseGet(
                () ->
                    CriteriaDistribution.builder()
                        .name(request.getPreference().getName())
                        .customer(customer)
                        .isActive(request.getPreference().isActive())
                        .build());

    criteriaDistribution.setActive(request.getPreference().isActive());

    if (CriteriaDistributionChannel.DIGITAL.getValue().equals(request.getPreference().getName())
        && request.getPreference().isActive()) {
      String hubAuthToken =
          hubDigitalFlowHelper.getUserHubTokenByKeycloakAdmin(
              customer,
              BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
      CustomerRequest customerRequest =
          new CustomerRequest(request.getCustomer(), request.getCustomer());
      this.hubDigitalFlow.createCustomer(customerRequest, hubAuthToken);
    }
    this.criteriaDistributionRepository.save(criteriaDistribution);
    return request;
  }

  /**
   * Validate the exist of customer and allow only Super-Admin and Platform-Admin
   *
   * @param customer Customer name
   * @throws UserAccessDeniedExceptionHandler If the invoking user not an admin
   * @throws CustomerNotFoundException If customer not exist in the platform
   */
}
