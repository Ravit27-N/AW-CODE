package com.innovationandtrust.process.model;

import com.innovationandtrust.share.model.project.Document;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInfo implements Serializable {
  private long id;
  private String docId;
  private String name;
  private String signedDocUrl = "";
  private int totalPages;

  public DocumentInfo(Document doc) {
    this.id = doc.getId();
    this.docId = doc.getUuid();
    this.name = doc.getOriginalFileName();
    this.totalPages = doc.getTotalPages();
    this.signedDocUrl = "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DocumentInfo that)) return false;
    return Objects.equals(getDocId(), that.getDocId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDocId());
  }
}
