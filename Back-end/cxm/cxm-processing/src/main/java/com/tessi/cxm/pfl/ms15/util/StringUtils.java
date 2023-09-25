package com.tessi.cxm.pfl.ms15.util;

import com.google.common.base.CharMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
  public static int extractNumberFromString(String source) {

    return Integer.parseInt(CharMatcher.inRange('0', '9').retainFrom(source));
  }

  private static List<String> extractNumberFromToConditionString(String source) {
    return Arrays.stream(source.split("to")).map(String::trim).collect(Collectors.toList());
  }

  private static List<String> extractNumberFromAndConditionString(String source) {
    return Arrays.stream(source.split("and")).map(String::trim).collect(Collectors.toList());
  }

  public static DocumentQuery getDocumentQuery(String source) {
    var list = extractNumberFromAndConditionString(source);
    List<Integer> and = new ArrayList<>();
    List<List<Integer>> to = new ArrayList<>();
    for (String s : list) {
      if (org.apache.commons.lang.StringUtils.isNumeric(s)) {
        and.add(extractNumberFromString(s));
      } else {
        var tos =
            extractNumberFromToConditionString(s).stream()
                .map(StringUtils::extractNumberFromString)
                .collect(Collectors.toList());
        List<Integer> integers = List.of(tos.get(0), tos.get(1));
        to.add(integers);
      }
    }
    return new DocumentQuery(and, to);
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DocumentQuery {
    private List<Integer> and;
    private List<List<Integer>> to;
  }
}
