package com.tessi.cxm.pfl.ms8.dto;

import com.tessi.cxm.pfl.ms8.validators.ValidWatermarkColor;
import com.tessi.cxm.pfl.ms8.validators.ValidWatermarkPosition;
import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatermarkDto implements Serializable {
  private Long id;

  @NotBlank(message = "The watermark text is required.")
  @Size(max = 30, message = "The watermark text must be less than or equal to 30 characters.")
  private String text;

  @ValidWatermarkPosition private String position;

  @NotNull(message = "Text size is required.")
  @Min(value = 1, message = "The text size must be a positive integer.")
  private Integer size;

  @NotNull(message = "Text rotation is mandatory.")
  @Min(value = -360, message = "Text must be rotated between -360 and 360.")
  @Max(value = 360, message = "Text must be rotated between -360 and 360.")
  private Integer rotation;

  @ValidWatermarkColor private String color;

  @NotBlank(message = "The flowId field is required.")
  private String flowId;
}
