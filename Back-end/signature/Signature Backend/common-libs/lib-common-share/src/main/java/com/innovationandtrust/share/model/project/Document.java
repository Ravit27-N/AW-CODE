package com.innovationandtrust.share.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.innovationandtrust.share.model.tdc.TdcDocument;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document implements Serializable {

  private Long id;
  private String uuid;
  private String name;

  private String fullPath;
  private String fileName;
  private String originalFileName;
  private String contentType;
  private Long size;
  private String extension;
  private int totalPages;

  private String docUrl;
  private List<DocumentDetail> details = new ArrayList<>();

  private TdcDocument tdcDocument;

  public List<DocumentDetail> getParticipantDetails(Long participantId, String type) {
    return details.stream()
        .filter(
            detail ->
                Objects.equals(detail.getSignatoryId(), participantId)
                    && detail.getType().contains(type))
        .toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Document document)) return false;
    return Objects.equals(getId(), document.getId())
        && Objects.equals(getUuid(), document.getUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUuid());
  }
}
