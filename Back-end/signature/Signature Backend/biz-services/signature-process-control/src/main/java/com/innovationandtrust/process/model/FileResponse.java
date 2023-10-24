package com.innovationandtrust.process.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {

  private byte[] resource;
  private long size;

  private String contentType;

  private String filename;

  public HttpHeaders getResourceHeader() {
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, this.contentType);
    headers.setContentLength(this.size);
    headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
    return headers;
  }
}
