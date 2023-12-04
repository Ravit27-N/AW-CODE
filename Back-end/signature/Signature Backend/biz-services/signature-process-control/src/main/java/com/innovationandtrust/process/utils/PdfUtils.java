package com.innovationandtrust.process.utils;

import static com.innovationandtrust.process.constant.PathConstant.SIGNATURE_PATH;
import static com.innovationandtrust.utils.commons.CommonUsages.getTextWidth;

import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.model.project.DocumentDetail;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.date.DateUtil;
import com.innovationandtrust.utils.exception.exceptions.InternalErrorException;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.itextpdf.html2pdf.HtmlConverter;
import gui.ava.html.image.generator.HtmlImageGenerator;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationRubberStamp;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PdfUtils {

  public static final String PARAPH = "Paraph";
  public static final String SIGNATORY = "Signatory";
  private static final String PDF = ".pdf";
  private static final String PNG = ".png";
  private static final String FONT = "Arial";
  private static final String P_TAG =
      "<p style=\"font-family: Arial, Helvetica, sans-serif; text-align: center;";

  public static String buildTempPath(Path path, Long id) {
    var addition = "_" + id + PDF;
    return FileUtils.toPath(path) + addition;
  }

  public static File createParaphrase(String paraphrase, Participant participant, Path dir) {
    String sign = String.format("%s%s%s", P_TAG + "font-size: 50px;\">", paraphrase, "</p>");

    log.info("Generating paraphrase image from HTML...");
    var fileName = participant.getUuid() + "_paraphrase.png";
    return convertHtmlToImage(dir, fileName, sign, false, false);
  }

  /**
   * To generate signature block image.
   *
   * @param signature refers to text of the top of visible signature
   * @param participant participant refers to participant who actioning
   * @return the text act like signature
   */
  public static File createSign(String signature, Participant participant, Path dir) {
    String sign =
        String.format(
            "%s%s %sSigné électroniquement le %s%sPar %s %s%sAvec le code à usage unique %s%s",
            P_TAG + "font-size: 100px;\">",
            signature,
            "<br><br>",
            DateUtil.toSignedDate(participant.getSignedDate()),
            "<br>",
            participant.getFullName(),
            participant.getPhone(),
            "<br>",
            participant.getOtpCode(),
            "</p>");

    log.info("Generating signature image from HTML...");
    var fileName = participant.getUuid() + ".png";
    return convertHtmlToImage(dir, fileName, sign, true, true);
  }

  /**
   * To generate signature block image.
   *
   * @param signature refers to text of the top of visible signature
   * @param participant participant refers to participant who actioning
   * @param dir the directory storing file
   * @param signatureImage signature image uploaded by signer
   * @return file signature
   */
  public static File createImageSign(
      String signature, Participant participant, Path dir, Path signatureImage) {
    String sign =
        String.format(
            "%s %s%s%s %s %s%s Signé électroniquement le %s %sPar %s %s%sAvec le code à usage unique %s%s",
            "<div >",
            "<img style=\"display: block; margin-left: auto; margin-right: auto; height: 300px; border-radius: 15px;\" src=\"",
            getImageUrl(signatureImage),
            "\">",
            P_TAG + "font-size: 60px;\">",
            signature,
            "<br><br>",
            DateUtil.toSignedDate(participant.getSignedDate()),
            "<br>",
            participant.getFullName(),
            participant.getPhone(),
            "<br>",
            participant.getOtpCode(),
            "</p></div>");

    FileUtils.createDirIfNotExist(dir.resolve(SIGNATURE_PATH));
    log.info("Generating signature image from HTML...");
    var fileName = participant.getUuid();

    // We chose write html to pdf and then to image. Because it supports modern css styles
    var signImage = parseHtmlToImage(sign, dir.resolve(SIGNATURE_PATH).resolve(fileName));
    if (FileUtils.isTransparentImage(signImage)) {
      FileUtils.removeWhiteBackground(signImage);
    }
    return signImage;
  }

  private static String getImageUrl(Path path) {
    try {
      // This URL provide full path for display image on web page
      return path.toUri().toURL().toString();
    } catch (Exception e) {
      log.error("Error getting image url", e);
      return null;
    }
  }

  /**
   * @param half mean you want to crop that half. It because of HtmlImageGenerator will produce
   *     white space equals to half of expected
   */
  private static File convertHtmlToImage(
      Path dir, String fileName, String html, boolean removeBg, boolean half) {

    log.info("Generating image from HTML...");
    HtmlImageGenerator hig = new HtmlImageGenerator();
    hig.loadHtml(html);
    FileUtils.createDirIfNotExist(dir.resolve(SIGNATURE_PATH));
    File file = dir.resolve(SIGNATURE_PATH).resolve(fileName).toFile();
    log.info("Saving file PATH: " + file.getPath());
    hig.saveAsImage(file);

    try {
      BufferedImage img = ImageIO.read(file);
      BufferedImage subImage =
          half ? img.getSubimage(0, 0, img.getWidth(), img.getHeight() / 2) : img;
      ImageIO.write(subImage, "PNG", file);

      if (removeBg) {
        FileUtils.removeWhiteBackground(file);
      }
      return file;
    } catch (Exception e) {
      log.error("Error while creating signature annotation...", e);
      return null;
    }
  }

  /**
   * @param signature refers to text of the top of visible signature
   * @param participant participant refers to participant who actioning
   * @return the text act like signature
   */
  public static String buildSignature(String signature, Participant participant) {
    return String.format(
        "%s %sSigné électroniquement le %s%sPar %s %s%sAvec le code à usage unique %s",
        signature,
        "\n\n",
        DateUtil.toSignedDate(participant.getSignedDate()),
        "\n",
        participant.getFullName(),
        participant.getPhone(),
        "\n",
        participant.getOtpCode());
  }

  /**
   * To generate annotation to document file in one position.
   *
   * @param document the project document file.
   * @param participant refers to participant who actioning
   * @param detail refers to paraphrase of signers
   * @param basePath refers to base path that stored documents.
   * @throws IOException if any issue while generating annotations
   */
  public static void setParaphraseAnnotations(
      PDDocument document,
      Project project,
      Participant participant,
      DocumentDetail detail,
      String basePath)
      throws IOException {

    var input =
        createParaphrase(project.getParaph(), participant, Path.of(basePath, project.getFlowId()));
    if (Objects.isNull(input)) {
      throw new InternalErrorException("Error while creating paraphrase for documents");
    }

    // This participant name  include slash if one signed
    int width =
        getTextWidth(new Font(FONT, Font.PLAIN, detail.getFontSize()), participant.getShortName());
    // full width is about 100% width of signers name who signed and signer to sign
    int fullWidth =
        getTextWidth(new Font(FONT, Font.PLAIN, detail.getFontSize()), project.getParaph());

    try (var img = new FileInputStream(input)) {
      // Reading generated image for PDImage
      PDImageXObject xImage =
          PDImageXObject.createFromByteArray(document, img.readAllBytes(), input.getName());

      PDPageTree pages = document.getDocumentCatalog().getPages();
      for (PDPage page : pages) {
        float x = (float) detail.getX();
        // To move paraphrase x axis
        detail.setX(x - (project.isOneSigned() ? fullWidth - width : 0));
        detail.setWidth(fullWidth);
        // plus 2 for accurate the height of image annotation, if not plus it looks short
        detail.setHeight((double) ObjectUtils.defaultIfNull(detail.getFontSize(), 0) + 2);

        // Reading generated sign image for PDImage
        PDAnnotationRubberStamp stamp =
            placeImage(document, page, detail, xImage, SIGNATORY + participant.getFullName());
        detail.setX(x); // reset x to default, prevent update object

        addAnnotation(page.getAnnotations(), stamp, page);
      }

      org.apache.commons.io.FileUtils.deleteQuietly(input);
      log.info("Paraphrase file which was generated, was deleted...");
    }
  }

  /**
   * To generate signature block on a page.
   *
   * @param document document the project document file.
   * @param detail refers to position of signature block to generate
   * @param input refers to the signature image which was generated from HTML
   * @param participant refers to whom actioning
   * @throws IOException if any issue while generating signature block
   */
  public static void setImageAnnotation(
      PDDocument document, Participant participant, DocumentDetail detail, File input)
      throws IOException {

    PDPage page = document.getPage(detail.getPageNum() - 1);
    float width = (float) detail.getWidth();
    float height = (float) detail.getHeight();
    float padding = width - 20;

    if (!StringUtils.equals(participant.getSignatureMode(), SignatureMode.WRITE.name())) {
      // multiply by 2, because default value is for WRITE mode
      detail.setHeight(height * 2);
      detail.setY(detail.getY() - height);
      padding = width - 10; // 10 pixels is the padding
    }

    detail.setWidth(padding);
    try (var img = new FileInputStream(input)) {
      // Reading generated image for PDImage
      PDImageXObject xImage =
          PDImageXObject.createFromByteArray(document, img.readAllBytes(), input.getName());

      PDAnnotationRubberStamp stamp =
          placeImage(document, page, detail, xImage, SIGNATORY + participant.getFullName());

      addAnnotation(page.getAnnotations(), stamp, page);
    }
  }

  private static PDAnnotationRubberStamp placeImage(
      PDDocument document,
      PDPage page,
      DocumentDetail detail,
      PDImageXObject xImage,
      String stampName)
      throws IOException {

    PDRectangle rectangle = createRectangle(page, detail);
    PDAnnotationRubberStamp stamp = createStamp(rectangle, stampName);
    PDResources resources = new PDResources();
    resources.add(xImage);
    PDFormXObject form = createForm(document, rectangle, resources);
    PDPageContentStream stream = new PDPageContentStream(document, createAppearance(stamp, form));

    // Drawing image to pdf file
    stream.drawImage(
        xImage,
        rectangle.getLowerLeftX(),
        rectangle.getLowerLeftY(),
        rectangle.getWidth(),
        rectangle.getHeight());
    stream.close();

    return stamp;
  }

  private static void addAnnotation(
      List<PDAnnotation> annotations, PDAnnotationRubberStamp stamp, PDPage page) {
    // Adding annotation to PDF annotation list
    annotations.add(stamp);
    COSArrayList<PDAnnotation> list = (COSArrayList<PDAnnotation>) annotations;
    COSArrayList.converterToCOSArray(list).setNeedToBeUpdated(true);
    page.getCOSObject().setNeedToBeUpdated(true);
    page.setAnnotations(annotations);
  }

  private static PDRectangle createRectangle(PDPage page, DocumentDetail detail) {
    // Creating space for placing annotation
    float x = (float) detail.getX();
    float width = (float) detail.getWidth();
    float height = (float) detail.getHeight();

    PDRectangle mediaBox = page.getMediaBox();
    float pdfHeight = mediaBox.getHeight();
    // pdfHeight minus the provided Y
    // because client calculate Y from top and this pdfBox library calculate Y from bottom
    float y = (float) (pdfHeight - detail.getY() - height);
    PDRectangle rectangle = new PDRectangle(x, y, width, height);
    rectangle.getCOSArray().setNeedToBeUpdated(true);
    return rectangle;
  }

  private static PDAnnotationRubberStamp createStamp(PDRectangle rectangle, String stampName) {
    // "Creating stamp
    PDAnnotationRubberStamp stamp = new PDAnnotationRubberStamp();
    stamp.getCOSObject().setNeedToBeUpdated(true);
    stamp.setName(stampName);
    stamp.setLocked(true);
    stamp.setReadOnly(true);
    stamp.setPrinted(true);
    stamp.setRectangle(rectangle);
    return stamp;
  }

  private static PDFormXObject createForm(
      PDDocument document, PDRectangle rectangle, PDResources resources) {
    // Creating form for adding annotation
    PDFormXObject form = new PDFormXObject(document);
    form.setBBox(rectangle);
    form.setResources(resources);
    form.setFormType(1);
    form.getCOSObject().setNeedToBeUpdated(true);
    form.getResources().getCOSObject().setNeedToBeUpdated(true);
    return form;
  }

  private static PDAppearanceStream createAppearance(
      PDAnnotationRubberStamp stamp, PDFormXObject form) {
    // Creating appearance
    PDAppearanceStream appearanceStream = new PDAppearanceStream(form.getCOSObject());
    PDAppearanceDictionary appearance = new PDAppearanceDictionary(new COSDictionary());
    appearanceStream.getCOSObject().setNeedToBeUpdated(true);
    appearance.getCOSObject().setNeedToBeUpdated(true);
    appearance.setNormalAppearance(appearanceStream);
    stamp.setAppearance(appearance);
    return appearanceStream;
  }

  public static File parseHtmlToImage(String html, Path dir) {
    Document doc;
    boolean hasHead = html.contains("<!doctype html>");
    if (!hasHead) {
      String template = "<!doctype html><html><head></head><body></body></html>";
      doc = Jsoup.parse(template);
      doc.body().prepend(html);
    } else {
      doc = Jsoup.parse(html);
    }
    // display code to render screen for HTML image file.
    // this pdf size only for this signature. Because it related to font size of texts to add
    doc.head().append("<style>@page{size: 15in 9in; }</style>");
    // create a new temporary file.
    var tempPdf = convertHtmlToPdf(doc, Path.of(dir + PDF));
    var image = convertPdfToPNGImage(tempPdf, Path.of(dir + PNG));
    // delete temp file.
    org.apache.commons.io.FileUtils.deleteQuietly(tempPdf);
    return image;
  }

  public static File convertHtmlToPdf(Document doc, Path filePath) {
    try {
      File file = filePath.toFile();
      HtmlConverter.convertToPdf(doc.html(), new FileOutputStream(file));
      return file;
    } catch (IOException ioException) {
      log.error("Failed to convert HTML to PDF", ioException);
      throw new InternalErrorException("Error while generating sign image.");
    }
  }

  public static File convertPdfToPNGImage(File pdfFile, Path filePath) {
    try {
      File pngImageFile = filePath.toFile();
      PDDocument document = Loader.loadPDF(pdfFile);
      BufferedImage image = (new PDFRenderer(document)).renderImage(0, 1.0F);
      ImageIO.write(image, "PNG", pngImageFile);
      document.close();
      return pngImageFile;
    } catch (IOException ioException) {
      log.error("Failed to convert PDF to PNG image", ioException);
      throw new InternalErrorException("Error while generating sign image.");
    }
  }
}
