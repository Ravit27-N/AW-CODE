package com.innovationandtrust.utils.commons;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormatValidator {

  /**
   * Validates phone numbers is valid or not.
   *
   * @param phoneNumber number to validate.
   * @return true if phone numbers are valid.
   */
  public static boolean isValidPhoneNumber(String phoneNumber) {
    try {
      PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
      PhoneNumber number = phoneNumberUtil.parse(phoneNumber, "");
      return phoneNumberUtil.isValidNumber(number);
    } catch (NumberParseException e) {
      log.error("Invalid phone number", e);
      return false;
    }
  }

  /**
   * Validate email is valid or not.
   *
   * @param emailAddress address to validate.
   * @return true if email address is valid
   */
  public static boolean isValidEmailAddress(String emailAddress) {
    String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(emailAddress);
    return matcher.matches();
  }
}
