package com.tessi.cxm.pfl.ms5.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.WordUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

  /**
   * Apply title case to a string with more custom rules.
   *
   * <p>
   *
   * <ol>
   *   Rules:
   *   <li>Accept dash(-) and and space as word separator
   *   <li>Capitalize first word
   *   <li>For each word after the first word, lowercase all letter after the first letter
   * </ol>
   *
   * @param input test to apply title case
   * @return orignal or title case applied text
   */
  public static String titleCase(String input) {
    // check for null or empty input
    if (!org.springframework.util.StringUtils.hasText(input)) {
      return input;
    }
    input = input.trim();
    String transformedWord = input;
    // split the input by dash or space
    String[] words = input.split("[-\\s]+");
    // loop through the words array
    for (int i = 0; i < words.length; i++) {
      // get the current word
      String word = words[i];
      // capitalize the first word
      if (i == 0) {
        word = WordUtils.capitalizeFully(word);
      }
      // lowercase the rest of the letters after the first letter
      else {
        word = word.charAt(0) + word.substring(1).toLowerCase();
      }
      // Search for the word and replace it.
      transformedWord =
          org.apache.commons.lang3.StringUtils.replaceIgnoreCase(transformedWord, word, word);
    }
    // return the transformed string
    return transformedWord;
  }
}
