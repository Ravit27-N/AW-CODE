package com.innovationandtrust.process.utils;

import com.innovationandtrust.process.constant.RegexPatternConstant;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PhoneNumberUtils {

  public static String removePhoneNumber(String phoneNumber, int missingLength) {
    var regex = String.format(RegexPatternConstant.LAST_DIGIT, missingLength);
    return phoneNumber.replaceAll(regex, "");
  }

  public static String findPhoneNumberLastDigit(String phoneNumber, int length) {
    Pattern regex = Pattern.compile(String.format(RegexPatternConstant.LAST_DIGIT, length));
    var matcher = regex.matcher(phoneNumber);
    if (matcher.find()) {
      return matcher.group(0);
    }
    return "";
  }

  public static boolean verifyPhoneNumber(String original, String toCompare, int missingLength) {
    return (original.length() == toCompare.length() && Objects.equals(original, toCompare))
        || (toCompare.length() == missingLength
            && Objects.equals(
                original, removePhoneNumber(original, missingLength).concat(toCompare)));
  }
}
