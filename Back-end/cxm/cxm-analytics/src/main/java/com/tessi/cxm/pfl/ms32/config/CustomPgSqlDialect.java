package com.tessi.cxm.pfl.ms32.config;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomPgSqlDialect extends PostgreSQL9Dialect {

  public CustomPgSqlDialect() {
    super();

    registerFunction(
        "CUS_DATE",
        new StandardSQLFunction(
            "DATE",
            StandardBasicTypes.DATE
        )
    );
  }
}
