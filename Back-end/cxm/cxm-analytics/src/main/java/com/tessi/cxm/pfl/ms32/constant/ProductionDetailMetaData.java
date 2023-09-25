package com.tessi.cxm.pfl.ms32.constant;

import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionDetailMetaData {
  FILLERS("cxm_analytics.production_detail_table.filler", "filler", AnalyticsConstants.TEXT_TYPE),
  VOLUME_RECEIVED(
      "cxm_analytics.production_detail_table.volume_received",
      "volumeReceived",
      AnalyticsConstants.NUMBER_TYPE),
  PROCESSED("cxm_analytics.production_detail_table.processed", "processed", AnalyticsConstants.NUMBER_TYPE),
  IN_PROGRESS(
      "cxm_analytics.production_detail_table.in_progress", "inProgress", AnalyticsConstants.NUMBER_TYPE),
  PND("cxm_analytics.production_detail_table.pnd", "pndMnd", "number"),
  MND("cxm_analytics.production_detail_table.mnd", "pndMnd", "number"),
  PROCESSED_PERCENTAGE(
      "cxm_analytics.production_detail_table.processed_percentage",
      "processedPercentage",
      AnalyticsConstants.PERCENT_TYPE),
  PND_PERCENTAGE(
      "cxm_analytics.production_detail_table.pnd_percentage",
      "pndMndPercentage",
      AnalyticsConstants.PERCENT_TYPE),
  MND_PERCENTAGE(
      "cxm_analytics.production_detail_table.mnd_percentage",
      "pndMndPercentage",
      AnalyticsConstants.PERCENT_TYPE),
  TOTAL("cxm_analytics.production_detail_table.total", "total", "text");

  private final String key;
  private final String value;
  private final String dataType;

  private static final List<ProductionDetailMetaData> cachedPostalMetas;
  private static final List<ProductionDetailMetaData> cachedDigitalMetas;

  static {
    var metas = Arrays.asList(values());
    cachedPostalMetas = getMetaByChannel(metas, FlowDocumentChannelConstant.POSTAL);
    cachedDigitalMetas = getMetaByChannel(metas, FlowDocumentChannelConstant.DIGITAL);
  }

  public static List<ProductionDetailMetaData> getMetadata(String channel) {
    return FlowDocumentChannelConstant.POSTAL.equals(channel)
        ? cachedPostalMetas
        : cachedDigitalMetas;
  }

  private static List<ProductionDetailMetaData> getMetaByChannel(
      List<ProductionDetailMetaData> sourceMetas, String channel) {
    return sourceMetas.stream()
        .filter(
            metaData -> {
              if (FlowDocumentChannelConstant.POSTAL.equals(channel)) {
                return !metaData.getKey().equals(MND.getKey())
                    && !metaData.getKey().equals(MND_PERCENTAGE.getKey());
              }
              return !metaData.getKey().equals(PND.getKey())
                  && !metaData.getKey().equals(PND_PERCENTAGE.getKey());
            })
        .collect(Collectors.toList());
  }
}
