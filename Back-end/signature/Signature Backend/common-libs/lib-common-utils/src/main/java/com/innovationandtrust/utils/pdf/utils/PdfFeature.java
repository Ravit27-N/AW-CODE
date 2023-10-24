package com.innovationandtrust.utils.pdf.utils;

import com.innovationandtrust.utils.file.exception.UnableCopyFileException;
import com.innovationandtrust.utils.pdf.request.PdfRequest;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@NoArgsConstructor
public class PdfFeature {
    public String copyPdfFile(String source, String destination) {
        try {
            return Files.copy(Paths.get(source), Paths.get(destination)).toString();
        } catch (IOException exception) {
            log.error("Failed to copy pdf", exception);
            throw new UnableCopyFileException("Unable to copy file");
        }
    }

    public String generatedUpdatedPath(String path) {
        return path.substring(0, path.lastIndexOf(".")).concat("-updated").concat(".pdf");
    }

    public void addTextToPdf(PdfRequest pdfRequest, Document document, int totalPage, boolean isAllPage)
            throws IOException {
        var path = Paths.get("fonts").resolve(pdfRequest.getFontFamily().concat(".ttf"));
        ClassPathResource resource = new ClassPathResource(path.toString());
        // Add font to pdf document
        PdfFont font =
                PdfFontFactory.createFont(resource.getContentAsByteArray(), PdfEncodings.WINANSI);
        // Add text to pdf document
        for (int i = 1; i <= totalPage; i++) {
            Paragraph text = new Paragraph(pdfRequest.getText());
            text.setFont(font);
            text.setFontSize(pdfRequest.getFontSize() == 0.0 ? 12 : pdfRequest.getFontSize());
            text.setFixedPosition(
                    isAllPage ? i : pdfRequest.getPageNo(),
                    pdfRequest.getX(),
                    pdfRequest.getY(),
                    pdfRequest.getWidth());
            document.add(text).setFont(font);
            if (!isAllPage) {
                break;
            }
        }
    }

    public void addImageToPdf(
            PdfRequest pdfRequest, Document document, int totalPage, boolean isAllPage)
            throws MalformedURLException {
        // Add image to pdf document
        for (int i = 1; i <= totalPage; i++) {
            ImageData imageData = ImageDataFactory.create(pdfRequest.getImagePath());
            Image image = new Image(imageData);
            image.setFixedPosition(
                    isAllPage ? i : pdfRequest.getPageNo(),
                    pdfRequest.getX(),
                    pdfRequest.getY(),
                    pdfRequest.getWidth());
            document.add(image);
            if (!isAllPage) {
                break;
            }
        }
    }
}
