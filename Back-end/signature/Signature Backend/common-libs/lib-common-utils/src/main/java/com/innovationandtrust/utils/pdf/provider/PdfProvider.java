package com.innovationandtrust.utils.pdf.provider;

import static com.innovationandtrust.utils.commons.CommonValidations.ok;

import com.innovationandtrust.utils.file.exception.FileRequestException;
import com.innovationandtrust.utils.pdf.request.PdfRequest;
import com.innovationandtrust.utils.pdf.utils.PdfFeature;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfProvider {
  public static final float MAX_X = 595;
  public static final float MAX_Y = 842;
  private final PdfFeature pdfFeature;

  public PdfProvider() {
    this.pdfFeature = new PdfFeature();
  }

  public void editPdfByPageNo(PdfRequest pdfRequest, String resultPath) {
    addContentToPdf(pdfRequest, resultPath, false);
  }

  public void editPdf(PdfRequest pdfRequest, String resultPath) {
    addContentToPdf(pdfRequest, resultPath, true);
  }

  /**
   * to edit the existing pdf file
   *
   * @param pdfRequest refer to the pdf file request
   */
  private void addContentToPdf(PdfRequest pdfRequest, String resultPath, boolean isAllPage) {
    // Only image or text will be used
    if (ok(pdfRequest.getText()) == ok(pdfRequest.getImagePath())) {
      throw new FileRequestException("The only text or image can be used");
    }

    String editPath = this.pdfFeature.copyPdfFile(pdfRequest.getPdfPath(), resultPath);

    try (PdfDocument pdfDocument =
            new PdfDocument(new PdfReader(pdfRequest.getPdfPath()), new PdfWriter(editPath));
        Document document = new Document(pdfDocument)) {

      // Make responsive position for each pdf size
      Rectangle pdfPage = pdfDocument.getFirstPage().getPageSize();
      float marginOfMaxX = pdfPage.getWidth() - MAX_X;
      pdfRequest.setX(marginOfMaxX + pdfRequest.getX());

      int totalPage = pdfDocument.getNumberOfPages();

      if (ok(pdfRequest.getText())) {
        this.pdfFeature.addTextToPdf(pdfRequest, document, totalPage, isAllPage);
      }

      if (ok(pdfRequest.getImagePath())) {
        this.pdfFeature.addImageToPdf(pdfRequest, document, totalPage, isAllPage);
      }
    } catch (IOException exception) {
      log.error("Failed to edit pdf", exception);
      throw new FileRequestException("Unable to edit the file " + exception.getMessage());
    }
  }

}
