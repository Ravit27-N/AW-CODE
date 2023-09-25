package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The model is responsible for mapping data after query and returning to the user request.
 *
 * @author Sokhour LACH
 * @author Vichet CHANN
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoadFlowDocumentDetailsDto extends BaseFlowDocumentDto {

  @JsonProperty(access = Access.READ_ONLY)
  private String fileId;

  private String subChannel;

  private FlowDocumentDetailsDto details;
  private Set<FlowDocumentHistoryDto> histories;

  private Set<ElementAssociationDto> elementAssociations;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date unloadingDate;

  /**
   * To return the collection of flow document's history.
   *
   * @return collection of {@link FlowDocumentHistoryDto}
   */
  public Set<FlowDocumentHistoryDto> getHistories() {
    if (this.histories == null) {
      return Set.of();
    }
    // Default sort by status ordering
    var sortingComparator =
        Comparator.comparing(
            (FlowDocumentHistoryDto flowDocumentHistoryDto) ->
                Integer.parseInt(
                    flowDocumentHistoryDto
                        .getHistoryStatus()
                        .get(FlowTraceabilityConstant.STATUS_ORDER)));

    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(this.channel)) {
      sortingComparator = Comparator.comparing(FlowDocumentHistoryDto::getDateTime);
    }
    return this.histories.stream()
        .sorted(sortingComparator)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
