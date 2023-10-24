package com.innovationandtrust.utils.commons;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter {

  private String field;

  // Common use on OR Operator
  private List<String> fields;

  // Foreign field
  private List<String> referenceField;

  private String value;

  // Common use on IN operator and OR
  private List<String> values;

  // operator is the filter operator
  private QueryOperator operator;

  // logical operator is the chain between filter
  @Builder.Default private LogicalOperator logicalOperator = LogicalOperator.AND;

  // columns to be selected
  private List<String> selectColumns;

  @Override
  public String toString() {
    return String.format(
        "Filter: field:%s fields:%s referenceField:%s value:%s values:%s operator:%s logicalOperator:%s",
        field, fields, referenceField, value, values, operator, logicalOperator);
  }
}
