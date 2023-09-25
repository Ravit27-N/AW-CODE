package com.tessi.cxm.pfl.ms32.constant;

import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum StatisticsExportingCSVHeader {
  ID_DOC("IDDoc", "IDDoc", 1),
  DIVISION("Division", "Division", 2),
  SERVICE("Service", "Service", 3),
  USER("User", "Utilisateur", 4),
  MODE_OF_DEPOSIT("Mode of deposit", "Mode de depot", 5),
  CHANNEL("Channel", "Canal", 6),
  CATEGORY("Category", "Categorie", 7),
  FILLER1("Filler1", "Filler1", 8),
  FILLER2("Filler2", "Filler2", 9),
  FILLER3("Filler3", "Filler3", 10),
  FILLER4("Filler4", "Filler4", 11),
  FILLER5("Filler5", "Filler5", 12),
  TOTAL_PAGE("Total Page", "Nb Pages", 13),
  RECIPIENT("Recipient", "Destinataire", 14),
  NUM_RECO("Recommended number", "N° Recommande", 15),
  DATE_RECEPTION("Date of reception provider sending", "Date reception prestataire envoi", 16),
  SENDING_DATE("Posting/Sending date", "Date depot poste/Envoi", 17),
  PND_DATE("Date of PND", "Date retour PND", 18),
  MND_DATE("Date of MND", "Date retour MND", 19),
  STATUS("Status", "Statut", 20);
  private final String en;
  private final String fr;
  private final int order;

  private static final Map<String, CSVHeaderModel> csvHeaders = new HashMap<>();

  static {
    for (var obj : values()) {
      csvHeaders.put(obj.en.toLowerCase(Locale.ROOT), new CSVHeaderModel(obj.fr, obj.order));
    }
  }

  public static List<String> getHeaders(List<SharedClientFillersDTO> fillers) {
    Map<String, CSVHeaderModel> localDefaultCSVHeaders = new HashMap<>(csvHeaders);
    fillers.forEach(
        filler -> {
          var csvHeaders = localDefaultCSVHeaders.get(filler.getKey().toLowerCase(Locale.ROOT));
          if (filler.isEnabled()) {
            csvHeaders.setValue(StringUtils.defaultIfBlank(filler.getValue(), filler.getKey()));
          }
        });

    List<String> fillerMaps =
        fillers.stream()
            .map(sharedClientFillersDTO -> sharedClientFillersDTO.getKey().toLowerCase(Locale.ROOT))
            .collect(Collectors.toList());

    getFillers().stream()
        .filter(key -> !fillerMaps.contains(key))
        .forEach(key -> localDefaultCSVHeaders.remove(key.toLowerCase(Locale.ROOT)));

    return localDefaultCSVHeaders.values().stream()
        .sorted(Comparator.comparingInt(CSVHeaderModel::getOrder))
        .map(CSVHeaderModel::getValue)
        .collect(Collectors.toList());
  }

  private static List<String> getFillers() {
    return List.of(
        FILLER1.en.toLowerCase(Locale.ROOT),
        FILLER2.en.toLowerCase(Locale.ROOT),
        FILLER3.en.toLowerCase(Locale.ROOT),
        FILLER4.en.toLowerCase(Locale.ROOT),
        FILLER5.en.toLowerCase(Locale.ROOT));
  }

  public static List<StatisticsExportingCSVHeader> defaultCSVFillers() {
    return List.of(
        StatisticsExportingCSVHeader.FILLER1,
        StatisticsExportingCSVHeader.FILLER2,
        StatisticsExportingCSVHeader.FILLER3,
        StatisticsExportingCSVHeader.FILLER4,
        StatisticsExportingCSVHeader.FILLER5);
  }
}
