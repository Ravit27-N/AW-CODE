package com.innovationandtrust.utils.companySetting;

import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.enums.SignatureFileType;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanySettingUtils {

  private static final String INVALID_LEVEL = "Invalid signature level:";

  private static final String INVALID_CHANEL = " Invalid notification type:";

  private static final String INVALID_FILE_TYPE = " Invalid file type:";

  private static final String INVALID_OPTION =
      ". This option may not available for this company...";

  private static final String MESSAGE = "In signature level:";

  public static String getCompanyUuid(List<CompanySettingDto> settingList) {
    return settingList.stream()
        .map(CompanySettingDto::getCompanyUuid)
        .collect(Collectors.toSet())
        .iterator()
        .next();
  }

  /** To validate incoming request is the valid request */
  public static void validateCompanySettings(List<CompanySettingDto> settingList) {
    log.info("Validating company uuid...");
    var uuids =
        settingList.stream().map(CompanySettingDto::getCompanyUuid).collect(Collectors.toSet());
    if (uuids.size() != 1) {
      throw new InvalidRequestException("Invalid company uuid...");
    }

    log.info("Check is there any duplicate requests...");
    var groupedByLevel =
        settingList.stream().collect(Collectors.groupingBy(CompanySettingDto::getSignatureLevel));
    boolean hasDuplicateLevels = groupedByLevel.values().stream().anyMatch(list -> list.size() > 1);
    if (hasDuplicateLevels) {
      throw new InvalidRequestException("Duplicate requests ...");
    }
  }

  /** [SUPER-ADMIN] To validate setting options before save to database. */
  public static void validateOptionBySuperAdmin(List<CompanySettingDto> settingList) {
    log.info("Validating signature levels and notification type...");
    if (settingList.isEmpty()) {
      throw new InvalidRequestException("Request can not be empty...");
    }
    var signatureLevels = SignatureSettingLevel.getLevels();
    var notificationsTypes = NotificationConstant.getTypes();
    settingList.forEach(
        setting -> {
          // Set to null, prevent wrong mapping from model mapper
          setting.setCompanyChannel(null);
          setting.setCompanyFileType(null);

          if (!signatureLevels.contains(setting.getSignatureLevel())) {
            throw new InvalidRequestException(INVALID_LEVEL + setting.getSignatureLevel());
          } else if (!notificationsTypes.contains(setting.getChannelReminder())) {
            throw new InvalidRequestException(INVALID_CHANEL + setting.getChannelReminder());
          }

          validateFileType(setting.getFileType());
        });
  }

  /**
   * [CORPORATE-ADMIN] To validate setting options before save to database.
   *
   * @param settingList refers to settings to update
   */
  public static void validateOptionByCorporateAdmin(List<CompanySettingDto> settingList) {
    log.info("Validating signature levels and notification type...");
    if (settingList.isEmpty()) {
      throw new InvalidRequestException("Request can not be empty...");
    }

    var signatureLevels = SignatureSettingLevel.getLevels();
    var notificationsTypes = NotificationConstant.getTypes();
    settingList.forEach(
        setting -> {
          // Set to null, prevent wrong mapping from model mapper
          setting.setChannelReminder(null);
          setting.setFileType(null);

          if (!signatureLevels.contains(setting.getSignatureLevel())) {
            throw new InvalidRequestException(INVALID_LEVEL + setting.getSignatureLevel());
          } else if (!notificationsTypes.contains(setting.getCompanyChannel())) {
            throw new InvalidRequestException(INVALID_CHANEL + setting.getCompanyChannel());
          }

          validateFileType(setting.getCompanyFileType());
        });
  }

  private static void validateFileType(Set<String> fileTypes) {
    var availableOptions = SignatureFileType.getFileTypes();
    fileTypes.forEach(
        fileType -> {
          if (!availableOptions.contains(fileType)) {
            throw new InvalidRequestException(INVALID_FILE_TYPE + fileType + "...");
          }
        });
  }

  /**
   * @param existFileTypes company chosen options
   * @param newFileTypes super-admin update file types
   */
  public static Set<String> getCompanyFileTypes(
      Set<String> existFileTypes, Set<String> newFileTypes) {
    existFileTypes.forEach(
        fileType -> {
          if (!newFileTypes.contains(fileType)) {
            existFileTypes.remove(fileType);
          }
        });

    return existFileTypes;
  }

  /**
   * @param settingList refers to super-admin setting for corporate
   * @param newSettingList refers to corporate setting
   */
  public static void validateSettings(
      List<CompanySettingDto> settingList, List<CompanySettingDto> newSettingList) {

    log.info("Validating company settings with company setting from mandatory database...");
    newSettingList.forEach(
        newSetting -> {
          log.info("Validating signature level...");
          var setting =
              settingList.stream()
                  .filter(
                      currentSetting ->
                          Objects.equals(
                              currentSetting.getSignatureLevel(), newSetting.getSignatureLevel()))
                  .findFirst()
                  .orElse(null);

          if (Objects.nonNull(setting)) {
            var message = MESSAGE + setting.getSignatureLevel();

            log.info("Validating file type...");
            var companyFileType = newSetting.getCompanyFileType();
            if (!companyFileType.isEmpty()) {
              companyFileType.forEach(
                  fileType -> {
                    if (!setting.getFileType().contains(fileType)) {
                      throw new InvalidRequestException(
                          message + INVALID_FILE_TYPE + fileType + INVALID_OPTION);
                    }
                  });
            }

            log.info("Validating notification type...");
            var companyChannel = newSetting.getCompanyChannel();
            if (Objects.nonNull(companyChannel)
                && (!Objects.equals(setting.getChannelReminder(), NotificationConstant.SMS_EMAIL))
                && (!Objects.equals(setting.getChannelReminder(), companyChannel))) {
              throw new InvalidRequestException(
                  message + INVALID_CHANEL + newSetting.getCompanyChannel() + INVALID_OPTION);
            }

          } else {
            throw new InvalidRequestException(
                INVALID_LEVEL + newSetting.getSignatureLevel() + INVALID_OPTION);
          }
        });
  }

  /**
   * @param setting refers to super-admin setting for corporate
   * @param option refers to END-USER option
   */
  public static void validateSettingOption(CompanySettingDto setting, CompanySettingDto option) {

    var message = MESSAGE + setting.getSignatureLevel();

    log.info("Validating available file type...");
    var fileTypes = option.getFileType();
    if (!fileTypes.isEmpty()) {
      for (var fileType : fileTypes) {
        if (!setting.getFileType().contains(fileType)
            || !setting.getCompanyFileType().contains(fileType)) {
          throw new InvalidRequestException(
              message + INVALID_FILE_TYPE + fileType + INVALID_OPTION);
        }
      }
    }

    log.info("Validating available notification type...");
    var channelReminder = option.getChannelReminder();
    if (Objects.nonNull(channelReminder)) {
      boolean notExistInSetting =
          (!Objects.equals(setting.getChannelReminder(), NotificationConstant.SMS_EMAIL)
              && !Objects.equals(setting.getChannelReminder(), channelReminder));
      boolean notExistInCompany =
          (!Objects.equals(setting.getCompanyChannel(), NotificationConstant.SMS_EMAIL)
              && !Objects.equals(setting.getCompanyChannel(), channelReminder));

      if (notExistInSetting || notExistInCompany) {
        throw new InvalidRequestException(
            message + INVALID_CHANEL + channelReminder + INVALID_OPTION);
      }
    }
  }

  /**
   * To validate corporate setting options still available from super-admin.
   *
   * @param setting refers to super-admin setting for corporate
   * @param companySetting refers to corporate setting options
   */
  public static void validateSetting(CompanySettingDto setting, CompanySettingDto companySetting) {
    var message = MESSAGE + setting.getSignatureLevel();

    log.info("Validating file types available...");
    var fileTypes = companySetting.getCompanyFileType();
    fileTypes.forEach(
        fileType -> {
          if (!setting.getFileType().contains(fileType)) {
            throw new InvalidRequestException(
                message + INVALID_FILE_TYPE + fileType + INVALID_OPTION);
          }
        });

    log.info("Validating notification type...");
    var companyChannel = companySetting.getCompanyChannel();
    if (Objects.nonNull(companyChannel)
        && (!Objects.equals(setting.getChannelReminder(), NotificationConstant.SMS_EMAIL)
            && !Objects.equals(setting.getChannelReminder(), companyChannel))) {
      throw new InvalidRequestException(message + INVALID_CHANEL + companyChannel + INVALID_OPTION);
    }
  }

  /**
   * Model mapper mapping this object wrong. It maps channelReminder to companyChannel. It maps
   * fileType to companyFileType
   */
  public static void setToNull(CompanySettingDto setting) {
    setting.setCompanyFileType(null);
    setting.setCompanyChannel(null);
  }

  public static void checkEmpty(String companyUuid, String signatureLevel) {
    if (!StringUtils.hasText(companyUuid)) {
      throw new InvalidRequestException("Company uuid must be present...");
    } else if (!StringUtils.hasText(signatureLevel)) {
      throw new InvalidRequestException("Signature level must be present...");
    }
  }
}
