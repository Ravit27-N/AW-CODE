package com.innovationandtrust.utils.pdf.request;

import com.itextpdf.layout.properties.UnitValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfRequest {
  private int pageNo;

  @NotNull(message = "pdfPath is required")
  private String pdfPath;
  private String text;
  private String imagePath;
  private float x;
  private float y;
  private UnitValue width;
  private float fontSize;
  private String fontFamily;
}
