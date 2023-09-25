package com.tessi.cxm.pfl.ms15.model;

import com.tessi.cxm.pfl.ms15.model.DocumentInstructions.DocumentInstructionData;
import com.tessi.cxm.pfl.ms15.util.StringUtils;
import com.google.common.base.Strings;
import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProduction;
import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProduction.Data;
import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProduction.Pj;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

@NoArgsConstructor
public class DocumentFieldSet implements FieldSetMapper<FileFlowDocument>, Serializable {

  private String[] tokens;
  private final int firstIndex = 0;
  private final int lastIndex = 1;
  @Setter
  private DocumentInstructionData instructions;
  @Setter
  private String channel;
  @Setter
  private String subChannel;

  public DocumentFieldSet(DocumentInstructionData instructions, String channel, String subChannel) {
    this.instructions = instructions;
    this.channel = channel;
    this.subChannel = subChannel;
  }

  @Override
  public FileFlowDocument mapFieldSet(FieldSet fieldSet) {
    tokens = String.join(",", fieldSet.getValues()).split(";");
    var production =
        FileDocumentProduction.builder()
            .pjs(new Pj(buildPjs(instructions.getPjs())))
            .datas(new Data(buildData(instructions.getData())))
            .build();
    return FileFlowDocument.builder()
        .uuid(UUID.randomUUID().toString())
        .offset("")
        .nbPages("")
        .analyse("")
        .address(buildAddress(instructions.getAddress()))
        .channel(channel)
        .subChannel(subChannel)
        .recipientID(getColumnValue(instructions.getRecipientId()))
        .emailRecipient(getColumnValue(instructions.getEmailRecipient()))
        .emailObject(getColumnValue(instructions.getEmailObject()))
        .production(production)
        .build();
  }

  private String getColumnValue(String resource) {
    return !Strings.isNullOrEmpty(resource)
        ? tokens[StringUtils.extractNumberFromString(resource) - lastIndex]
        : "";
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private Map<String, String> buildAddress(String resource) {
    final int maxLineAddress = 7;
    Map<String, String> map = new HashMap<>();
    if (Strings.isNullOrEmpty(resource)) {
      return map;
    }
    var query = StringUtils.getDocumentQuery(resource);
    if (query.getAnd().isEmpty() && query.getTo().isEmpty()) {
      return map;
    }
    AtomicInteger lineStart = new AtomicInteger(lastIndex);
    query
        .getTo()
        .forEach(
            element ->
                IntStream.range(element.get(firstIndex) - lastIndex, element.get(lastIndex))
                    .anyMatch(
                        index -> {
                          if (lineStart.get() == maxLineAddress) {
                            return true;
                          }
                          map.put(getAddressLine(lineStart.get()), tokens[index]);
                          lineStart.getAndIncrement();
                          return false;
                        }));

    query.getAnd().stream()
        .anyMatch(
            index -> {
              if (map.size() == maxLineAddress) {
                return true;
              }
              map.put(getAddressLine(lineStart.get()), tokens[index - lastIndex]);
              lineStart.getAndIncrement();
              return false;
            });
    int lineEnd = maxLineAddress + 1;
    if (map.size() < lineEnd) {
      IntStream.range(map.size() + 1, lineEnd).forEach(index -> map.put(getAddressLine(index), ""));
    }
    return map.entrySet().stream()
        .sorted(Entry.comparingByKey())
        .collect(
            Collectors.toMap(
                Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  private String getAddressLine(int lineAddress) {
    final String linePattern = "Line%s";
    return String.format(linePattern, lineAddress);
  }

  private List<String> buildPjs(String resource) {
    return getStrings(resource);
  }

  private List<String> buildData(String resource) {
    return getStrings(resource);
  }

  private List<String> getStrings(String resource) {
    List<String> data = new ArrayList<>();
    if (Strings.isNullOrEmpty(resource)) {
      return data;
    }
    var query = StringUtils.getDocumentQuery(resource);
    if (query.getAnd().isEmpty() && query.getTo().isEmpty()) {
      return data;
    }
    query.getAnd().forEach(index -> data.add(tokens[index - lastIndex]));
    query
        .getTo()
        .forEach(
            element ->
                IntStream.range(element.get(firstIndex) - lastIndex, element.get(lastIndex))
                    .forEach(index -> data.add(tokens[index])));
    return data;
  }
}
