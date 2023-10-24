package com.innovationandtrust.utils.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum QueryOperator {
    EQUALS,
    NOT_EQUALS,
    LIKE,
    IN,
    BETWEEN,
    BETWEEN_DATE,
    GREATER_THAN,
    LESS_THAN,
    IS_NOT_NULL,
    IS_NULL,
    IS_TRUE,
    IS_FALSE,
    OR_FIELDS, OR
}
