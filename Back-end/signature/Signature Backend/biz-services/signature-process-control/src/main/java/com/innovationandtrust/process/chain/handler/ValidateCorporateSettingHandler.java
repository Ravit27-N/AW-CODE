package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class handle on validating {@link com.innovationandtrust.share.enums.SignatureSettingLevel}
 * that a company has been subscribed. After subscribed, {@link
 * com.innovationandtrust.share.constant.RoleConstant} SUPER-ADMIN privilege will activate those
 * options. So CORPORATE-ADMIN of that company can activate for their employee. So employee can
 * create projects with those signature level. This class will validate the option employee chosen
 * to create this project with active options.
 */
@Slf4j
@Component
public class ValidateCorporateSettingHandler extends AbstractExecutionHandler {

  private final CorporateProfileFeignClient corporateProfileFeignClient;

  public ValidateCorporateSettingHandler(CorporateProfileFeignClient corporateProfileFeignClient) {
    this.corporateProfileFeignClient = corporateProfileFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var isSigningMulti = context.get(SignProcessConstant.IS_SIGNING_PROJECTS, Boolean.class);
    if (Objects.nonNull(isSigningMulti) && Boolean.TRUE.equals(isSigningMulti)) {
      return ExecutionState.NEXT;
    }

    log.info("[ValidateCorporateSettingHandler] To validate project setting");
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var companyUuid = project.getCorporateInfo().getCompanyUuid();
    if (Objects.isNull(companyUuid)) {
      log.info("Getting corporate info from corporate profile service...");
      var corporateInfo =
          this.corporateProfileFeignClient.findCorporateInfo(project.getCreatedBy());
      companyUuid = corporateInfo.getCompanyUuid();
    }

    log.info(
        "Getting company setting:{} from company:{}...", project.getSignatureLevel(), companyUuid);
    var companySetting =
        this.corporateProfileFeignClient.getCompanySettingByLevel(
            companyUuid, project.getSignatureLevel());

    // It will throw exception, If super-admin make any changed on settings
    // and project created have chosen options that unavailable in current settings
    log.info("Validating project signature level");
    CompanySettingUtils.validateSettingOption(companySetting, project.getSetting());

    return ExecutionState.NEXT;
  }
}
