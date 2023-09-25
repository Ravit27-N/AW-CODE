package com.tessi.cxm.pfl.ms3.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlowDigitalType {
  public static final String EMAIL = "EMAIL";
  public static final String SMS = "SMS";
}
