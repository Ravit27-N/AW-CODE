package com.innovationandtrust.utils.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUsages {
  public static String getFirstCharacter(String val) {
    return val.substring(0, 1);
  }

  public static Date now() {
    return new Date();
  }

  public static MultipartFile byteArrayToMultipartFile(
      byte[] bytes, String fileName, MediaType contentType) {
    return new ConvertMultiPartFile(fileName, fileName, contentType.toString(), bytes);
  }

  public static MultipartFile byteArrayToMultipartFile(byte[] bytes, String fileName) {
    return new ConvertMultiPartFile(
        fileName, fileName, ContentType.APPLICATION_OCTET_STREAM.getMimeType(), bytes);
  }

  public static MultipartFile objectToMultipartFile(Object object, String fileName) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String json = objectMapper.writeValueAsString(object);
      return new ConvertMultiPartFile(fileName, fileName, "application/json", json.getBytes());
    } catch (Exception e) {
      log.error("Failed to convert object to JSON.", e);
      throw new IllegalArgumentException(
          "Failed to convert object to JSON file: " + e.getMessage());
    }
  }

  public static <T> T castToRequiredType(Class<T> fieldType, String value) {
    try {
      if (!StringUtils.hasText(value)) {
        return null;
      } else if (fieldType.isAssignableFrom(Double.class)) {
        return fieldType.cast(Double.valueOf(value));
      } else if (fieldType.isAssignableFrom(Integer.class)) {
        return fieldType.cast(Integer.valueOf(value));
      } else if (fieldType.isAssignableFrom(Long.class)) {
        return fieldType.cast(Long.valueOf(value));
      } else if (fieldType.isAssignableFrom(Date.class)) {
        return fieldType.cast(Date.from(Instant.parse(value)));
      }
    } catch (Exception e) {
      log.error("Cannot parse {} to {} Exception:", value, fieldType, e);
      return null;
    }

    return fieldType.cast(value);
  }

  public static <T> List<T> castToRequiredType(Class<T> fieldType, List<String> values) {

    var valueList = new ArrayList<T>();
    for (String value : values) {
      var parsedValue = castToRequiredType(fieldType, value);
      if (Objects.nonNull(parsedValue)) {
        valueList.add(parsedValue);
      }
    }

    return valueList;
  }

  /**
   * get value of path from url.
   *
   * @param url string containing / .
   * @param index refers to index we want
   * @return value of index.
   */
  public static String getUrlPathAtIndex(String url, int index) {
    String[] parts = url.split("/", 3);
    return parts[index];
  }

  public static String getBaseUrlFromString(String url) {
    Pattern pattern = Pattern.compile("(https?://[^/]+)(.*)");
    Matcher matcher = pattern.matcher(url);
    if (matcher.matches()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }

  /**
   * To set user uuid for feign client exchange access token.
   *
   * @param userUuid refers to user uuid
   */
  public static void setUserUuid(String userUuid) {
    log.info("Setting user uuid to request context holder...");
    var request = RequestContextHolder.getRequestAttributes();
    if (Objects.nonNull(request)) {
      request.setAttribute(AuthenticationUtils.USER_UUID, userUuid, 0);
    }
  }

  public static String getFirstUpperCase(String text) {
    AtomicReference<String> name = new AtomicReference<>("");
    Arrays.stream(text.split(" "))
        .toList()
        .forEach(
            string -> {
              if (string.length() > 0) {
                name.set(name + string.substring(0, 1).toUpperCase());
              }
            });
    return name.get();
  }

  public static int getTextWidth(Font font, String text) {
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    FontMetrics fm = img.getGraphics().getFontMetrics(font);
    return fm.stringWidth(text);
  }
}
