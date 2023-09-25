package com.tessi.cxm.pfl.ms15.model;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.TemplateVariable;
import com.cxm.tessi.pfl.shared.flowtreatment.util.EmailValidatorUtils;
import com.cxm.tessi.pfl.shared.flowtreatment.util.PhoneNumberValidator;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProduction;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProduction.Data;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.model.GenericMailMessage;
import com.tessi.cxm.pfl.shared.utils.MapSortUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The model to map data after read from csv to {@link PortalFileFlowDocument}.
 *
 * @author Vichet CHANN
 * @version 1.5.0
 * @since 01 Jun 2022
 */
@Log4j2
public class CampaignDocumentFieldSet
    implements TemplateVariable, FieldSetMapper<PortalFileFlowDocument>, Serializable {

  private static final String DATA_KEY = "Data%s";
  private final String type;
  private final String channel;
  private final String subChannel;
  private final String[] variables;

  public CampaignDocumentFieldSet(String type, String channel, String subChannel,
      String[] variables) {
    this.type = type;
    this.channel = channel;
    this.subChannel = subChannel;
    this.variables = variables;
  }

  @Override
  public PortalFileFlowDocument mapFieldSet(FieldSet fieldSet) {
    Assert.notNull(fieldSet, "fieldSet cannot be null");

    int index = this.getRecipientColumnIndex();
    log.debug("Index of recipient column in CSV file: {}", index);

    var recipientId = fieldSet.getValues()[index];
    if (StringUtils.hasText(recipientId)) {
      if (FlowTreatmentConstants.PORTAL_CAMPAIGN_SMS.equalsIgnoreCase(type)) {
        recipientId = recipientId.replaceAll(
            PhoneNumberValidator.PHONE_NUMBER_PATTERN_REPLACEMENT, "");
      } else {
        recipientId = recipientId.replaceAll(GenericMailMessage.INVALID_EMAIl_PATTERN, "");
      }

      log.info("Recipient ID: {}", recipientId);
      return PortalFileFlowDocument.builder()
          .docUUID(UUID.randomUUID().toString())
          .offset("")
          .nbPages("1")
          .analysis(this.validateRecipient(recipientId))
          .channel(this.channel)
          .subChannel(this.subChannel)
          .recipientID(recipientId)
          .production(
              PortalFileDocumentProduction.builder()
                  .data(
                      buildProductionData(
                          java.util.Arrays.stream(fieldSet.getValues(), 0, fieldSet.getFieldCount())
                              .toArray(String[]::new)))
                  .build())
          .build();
    }
    return PortalFileFlowDocument.builder().docUUID("").build();
  }

  /**
   * Handle value of template variables.
   *
   * @return array of {@link String}
   */
  @Override
  public String[] getVariables() {
    return this.variables;
  }

  private Data buildProductionData(String[] values) {
    Map<String, String> dataMap = new HashMap<>();
    IntStream.range(0, values.length)
        .forEachOrdered(idx -> dataMap.put(String.format(DATA_KEY, idx + 1), values[idx]));
    return new Data(List.of(MapSortUtil.sortByKey(dataMap)));
  }

  /**
   * Get index of recipient column base on Campaign type.
   *
   * @return Index of recipient column in CSV file.
   */
  private int getRecipientColumnIndex() {
    if (FlowTreatmentConstants.PORTAL_CAMPAIGN_SMS.equalsIgnoreCase(type)) {
      return IntStream.range(0, this.variables.length)
          .filter(idx -> this.variables[idx].equals(TemplateVariable.SMS))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Invalid selected template variable"));
    }

    return EmailValidatorUtils.getIndexOfMailOrEmail(this.variables);
  }

  /**
   * Validate
   *
   * @param recipient SMS or Email recipient to validate
   * @return {@link FlowTreatmentConstants#FLOW_ANALYSIS_OK} if valid, otherwise
   * {@link FlowTreatmentConstants#FLOW_ANALYSIS_KO}.
   */
  private String validateRecipient(String recipient) {
    boolean isValidRecipient =
        FlowTreatmentConstants.PORTAL_CAMPAIGN_SMS.equalsIgnoreCase(this.type)
            ? PhoneNumberValidator.isValid(recipient)
            : EmailValidatorUtils.validateEmail(recipient);

    return isValidRecipient
        ? FlowTreatmentConstants.FLOW_ANALYSIS_OK
        : FlowTreatmentConstants.FLOW_ANALYSIS_KO;
  }

}
