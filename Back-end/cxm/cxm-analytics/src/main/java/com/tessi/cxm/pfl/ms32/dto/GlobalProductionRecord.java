package com.tessi.cxm.pfl.ms32.dto;

import com.tessi.cxm.pfl.ms32.constant.StatisticsExportingCSVHeader;
import com.tessi.cxm.pfl.shared.utils.TupleUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import javax.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalProductionRecord extends ProductionRecord implements Serializable {
  protected Long ownerId;
  private Map<Integer, String> globalCsvRow = new TreeMap<>();

  public GlobalProductionRecord(Tuple tuple, String timeZone) {
    this.ownerId = TupleUtils.getValue(tuple, 0, Long.class);
    // Use a stream to iterate over the range of indices to initialize the map
    IntStream.rangeClosed(1, 5)
        // Use a lambda expression to perform the logic for each index
        .forEach(
            i -> {
              // Use constants for the order values
              int order = getOrder(i);
              if (order > 0) {
                String value = TupleUtils.getValue(tuple, i, String.class);
                globalCsvRow.put(order, value);
              }
            });

    // Format the date values separately
    globalCsvRow.put(
        StatisticsExportingCSVHeader.DATE_RECEPTION.getOrder(),
        getStringDateFormat(TupleUtils.getValue(tuple, 6, Date.class), timeZone));
    globalCsvRow.put(
        StatisticsExportingCSVHeader.SENDING_DATE.getOrder(),
        getStringDateFormat(TupleUtils.getValue(tuple, 7, Date.class), timeZone));
    this.setFillers(tuple);
  }

  private void setFillers(Tuple tuple) {
    StatisticsExportingCSVHeader.defaultCSVFillers()
        .forEach(
            filler ->
                globalCsvRow.put(
                    filler.getOrder(),
                    TupleUtils.getValue(tuple, filler.name().toLowerCase(), String.class)));
  }

  public void addCsvRow(int order, String value) {
    globalCsvRow.put(order, value);
  }

  public void removeCsvRow(List<Integer> orderKeys) {
    orderKeys.forEach(globalCsvRow::remove);
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
      case 5:
        return StatisticsExportingCSVHeader.STATUS.getOrder();
      default:
        return -1; // Invalid index
    }
  }
}
