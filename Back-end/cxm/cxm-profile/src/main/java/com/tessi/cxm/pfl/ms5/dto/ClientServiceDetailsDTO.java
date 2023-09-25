package com.tessi.cxm.pfl.ms5.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientServiceDetailsDTO {

  private List<ClientResponseDTO> clients;

  /**
   * Get sorted list of {@link ClientResponseDTO} by {@link ClientResponseDTO#getName()} .
   *
   * @return Sorted list of {@link ClientResponseDTO}
   */
  public List<ClientResponseDTO> getClients() {
    final var sortingClients =
        Objects.requireNonNullElse(clients, new ArrayList<ClientResponseDTO>());
    return sortingClients.stream()
        .sorted(Comparator.comparing(ClientResponseDTO::getName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ClientResponseDTO {

    @Schema(type = "integer", example = "1")
    private long id;

    @Schema(type = "string", example = "Service 1")
    private String name;

    private List<DivisionResponseDTO> divisions;

    /**
     * Get sorted list of {@link DivisionResponseDTO} by {@link DivisionResponseDTO#getName()} .
     *
     * @return Sorted list of {@link DivisionResponseDTO}
     */
    public List<DivisionResponseDTO> getDivisions() {
      final var sortingDivisions =
          Objects.requireNonNullElse(divisions, new ArrayList<DivisionResponseDTO>());
      return sortingDivisions.stream()
          .sorted(Comparator.comparing(DivisionResponseDTO::getName, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DivisionResponseDTO {

    @Schema(type = "integer", example = "1")
    private long id;

    @Schema(type = "string", example = "Service 1")
    private String name;

    private List<DepartmentResponseDTO> departments;

    /**
     * Get sorted list of {@link DepartmentResponseDTO} by {@link DepartmentResponseDTO#getName()} .
     *
     * @return Sorted list of {@link DepartmentResponseDTO}
     */
    public List<DepartmentResponseDTO> getDepartments() {
      final var sortingDepartments =
          Objects.requireNonNullElse(departments, new ArrayList<DepartmentResponseDTO>());
      return sortingDepartments.stream()
          .sorted(
              Comparator.comparing(DepartmentResponseDTO::getName, String.CASE_INSENSITIVE_ORDER))
          .collect(Collectors.toList());
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DepartmentResponseDTO {

    @Schema(type = "integer", example = "1")
    private long id;

    @Schema(type = "string", example = "Service 1")
    private String name;
  }
}
