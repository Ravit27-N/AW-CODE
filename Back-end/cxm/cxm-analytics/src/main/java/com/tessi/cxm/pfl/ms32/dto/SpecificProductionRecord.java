package com.tessi.cxm.pfl.ms32.dto;

import com.tessi.cxm.pfl.ms32.constant.StatisticsExportingCSVHeader;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.TupleUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import javax.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpecificProductionRecord extends ProductionRecord {
  private Long ownerId;
  private Map<Integer, String> specificCSVRow = new TreeMap<>();

  public SpecificProductionRecord(Tuple tuple, String timeZone) {
    this.ownerId = TupleUtils.getValue(tuple, 0, Long.class);
    // Use a stream to iterate over the range of indices to initialize the map
    IntStream.rangeClosed(1, 8)
        // Use a lambda expression to perform the logic for each index
        .forEach(
            i -> {
              // Use constants for the order values
              int order = getOrder(i);
              if (order > 0) {
                String value = TupleUtils.getValue(tuple, i, String.class);
                specificCSVRow.put(order, value);
              }
            });
    specificCSVRow.put(
        StatisticsExportingCSVHeader.TOTAL_PAGE.getOrder(),
        String.valueOf(TupleUtils.defaultIfNull(tuple, 5, Long.class, 0L)));
    // Format the date values separately
    specificCSVRow.put(
        StatisticsExportingCSVHeader.DATE_RECEPTION.getOrder(),
        this.getStringDateFormat(TupleUtils.getValue(tuple, 9, Date.class), timeZone));
    specificCSVRow.put(
        StatisticsExportingCSVHeader.SENDING_DATE.getOrder(),
        this.getStringDateFormat(TupleUtils.getValue(tuple, 10, Date.class), timeZone));
    this.setPNDOrMndDate(tuple, timeZone);
    this.setFillers(tuple);
  }

  private void setFillers(Tuple tuple) {
    StatisticsExportingCSVHeader.defaultCSVFillers()
        .forEach(
            filler ->
                specificCSVRow.put(
                    filler.getOrder(),
                    TupleUtils.getValue(tuple, filler.name().toLowerCase(), String.class)));
  }

  private void setPNDOrMndDate(Tuple tuple, String timeZone) {
    List<String> pndAndMndStatus = new ArrayList<>(FlowDocumentStatus.getPNDStatus());
    pndAndMndStatus.addAll(FlowDocumentStatus.getMNDStatus());
    boolean pNDOrMndStatus =
        pndAndMndStatus.contains(
            specificCSVRow.get(StatisticsExportingCSVHeader.STATUS.getOrder()));

    specificCSVRow.put(
        StatisticsExportingCSVHeader.PND_DATE.getOrder(),
        (pNDOrMndStatus)
            ? this.getStringDateFormat(TupleUtils.getValue(tuple, 11, Date.class), timeZone)
            : "");
  }

  public void addRow(int order, String value) {
    specificCSVRow.put(order, value);
  }

  public void removeRow(List<Integer> orderKeys) {
    orderKeys.forEach(key -> specificCSVRow.remove(key));
  }

  // A helper method to get the order value based on the index
  private int getOrder(int index) {
    switch (index) {
      case 1:
        return StatisticsExportingCSVHeader.ID_DOC.getOrder();
      case 2:
        return StatisticsExportingCSVHeader.MODE_OF_DEPOSIT.getOrder();
      case 3:
        return StatisticsExportingCSVHeader.CHANNEL.getOrder();
      case 4:
        return StatisticsExportingCSVHeader.CATEGORY.getOrder();
      case 6:
        return StatisticsExportingCSVHeader.RECIPIENT.getOrder();
      case 7:
        return StatisticsExportingCSVHeader.NUM_RECO.getOrder();
      case 8:
        return StatisticsExportingCSVHeader.STATUS.getOrder();
      default:
        return -1; // Invalid index
    }
  }
}
