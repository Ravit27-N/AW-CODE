package com.tessi.cxm.pfl.ms5.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.dto.ClientDto;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.ReturnAddress;
import com.tessi.cxm.pfl.ms5.repository.ReturnAddressRepository;
import com.tessi.cxm.pfl.shared.config.ResttemplateFactory;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import com.tessi.cxm.pfl.shared.utils.AddressValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ReturnAddressServiceTest {

  @Mock
  ReturnAddressRepository mockReturnAddressRepository;
  @Mock
  ResttemplateFactory mockRestTemplateFactory;
  @Mock
  RestTemplate mockRestTemplate;
  ModelMapper mockModelMapper;
  private ReturnAddressService returnAddressService;

  @BeforeEach
  void setUp() {
    this.mockModelMapper = new ModelMapper();
    this.returnAddressService = new ReturnAddressService(mockReturnAddressRepository,
        new AddressValidator(mockRestTemplateFactory),
        mockModelMapper);
  }

  @ParameterizedTest
  @MethodSource({"createAddressSuccessParams"})
  @Order(1)
  void testCreateAddresses_ThenReturnSuccess(ClientDto clientDto, Client clientEntity,
      long totalAddressExpected) {
    var codePostalResponse = new HashMap<>();
    codePostalResponse.put("codePostal", "87000");
    codePostalResponse.put("codeCommune", "87000");
    codePostalResponse.put("nomCommune", "Limoges");
    codePostalResponse.put("libelleAcheminement", "Limoges");

    List<ReturnAddress> addressListResponse = new ArrayList<>();
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.CLIENT)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.DIVISION)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.SERVICE)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());

    when(this.mockRestTemplate.getForObject(anyString(), any()))
        .thenReturn(List.of(codePostalResponse));
    when(this.mockRestTemplateFactory.getRestTemplate(anyBoolean())).thenReturn(mockRestTemplate);
    when(this.mockReturnAddressRepository.saveAll(anyList())).thenReturn(addressListResponse);

    var response = this.returnAddressService.saveAll(clientDto, clientEntity);
    Assertions.assertNotNull(response, "The response must not be null");
    Assertions.assertEquals(response.size(), totalAddressExpected);
    response.forEach(address -> log.info("Return address: {}", address));
  }

  @ParameterizedTest
  @MethodSource({"createAddressFailParams"})
  @Order(2)
  void testCreateAddresses_ThenReturnFail(ClientDto clientDto, Client clientEntity,
      Class expectedException) {
    var codePostalResponse = new HashMap<>();
    codePostalResponse.put("codePostal", "87000");
    codePostalResponse.put("codeCommune", "87000");
    codePostalResponse.put("nomCommune", "Limoges");
    codePostalResponse.put("libelleAcheminement", "Limoges");

    List<ReturnAddress> addressListResponse = new ArrayList<>();
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.CLIENT)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.DIVISION)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.SERVICE)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());

    lenient().when(this.mockRestTemplate.getForObject(anyString(), any()))
        .thenReturn(List.of(codePostalResponse));
    lenient().when(this.mockRestTemplateFactory.getRestTemplate(anyBoolean()))
        .thenReturn(mockRestTemplate);
    lenient().when(this.mockReturnAddressRepository.saveAll(anyList()))
        .thenReturn(addressListResponse);
    Assertions.assertThrows(expectedException,
        () -> this.returnAddressService.saveAll(clientDto, clientEntity));
    log.info("Expected exception: {}", expectedException.getName());
  }

  @ParameterizedTest
  @MethodSource({"updateAddressSuccessParams"})
  @Order(3)
  void testUpdateAddresses_ThenReturnSuccess(ClientDto clientDto, Client clientEntity,
      Map<Long, AddressType> addressRemoved,
      long totalAddressExpected) {
    var codePostalResponse = new HashMap<>();
    codePostalResponse.put("codePostal", "87000");
    codePostalResponse.put("codeCommune", "87000");
    codePostalResponse.put("nomCommune", "Limoges");
    codePostalResponse.put("libelleAcheminement", "Limoges");

    List<ReturnAddress> addressListResponse = new ArrayList<>();
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.CLIENT)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.DIVISION)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.SERVICE)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());

    when(this.mockRestTemplate.getForObject(anyString(), any()))
        .thenReturn(List.of(codePostalResponse));
    when(this.mockRestTemplateFactory.getRestTemplate(anyBoolean())).thenReturn(mockRestTemplate);
    when(this.mockReturnAddressRepository.saveAll(anyList())).thenReturn(addressListResponse);

    var response = this.returnAddressService.updateAll(clientDto, clientEntity, addressRemoved);
    Assertions.assertNotNull(response, "The response must not be null");
    Assertions.assertEquals(response.size(), totalAddressExpected);
    response.forEach(address -> log.info("Return address: {}", address));
  }

  @ParameterizedTest
  @MethodSource({"updateAddressFailParams"})
  @Order(3)
  void testUpdateAddresses_ThenReturnFail(ClientDto clientDto, Client clientEntity,
      Map<Long, AddressType> addressRemoved, Class expectedException) {
    var codePostalResponse = new HashMap<>();
    codePostalResponse.put("codePostal", "87000");
    codePostalResponse.put("codeCommune", "87000");
    codePostalResponse.put("nomCommune", "Limoges");
    codePostalResponse.put("libelleAcheminement", "Limoges");

    List<ReturnAddress> addressListResponse = new ArrayList<>();
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.CLIENT)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.DIVISION)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());
    addressListResponse.add(ReturnAddress.builder()
        .id(1L)
        .type(AddressType.SERVICE)
        .refId(1L)
        .client(clientEntity)
        .line1("DEST_ADR1")
        .line2("DEST_ADR2")
        .line3("DEST_ADR3")
        .line4("MARIA DUFROIX")
        .line5("6 RUE GEORGE SAND")
        .line6("87000 Limoges")
        .build());

    when(this.mockRestTemplate.getForObject(anyString(), any()))
        .thenReturn(List.of(codePostalResponse));
    when(this.mockRestTemplateFactory.getRestTemplate(anyBoolean())).thenReturn(mockRestTemplate);
    when(this.mockReturnAddressRepository.saveAll(anyList())).thenReturn(addressListResponse);

    lenient().when(this.mockRestTemplate.getForObject(anyString(), any()))
        .thenReturn(List.of(codePostalResponse));
    lenient().when(this.mockRestTemplateFactory.getRestTemplate(anyBoolean()))
        .thenReturn(mockRestTemplate);
    lenient().when(this.mockReturnAddressRepository.saveAll(anyList()))
        .thenReturn(addressListResponse);
    Assertions.assertThrows(expectedException,
        () -> this.returnAddressService.updateAll(clientDto, clientEntity, addressRemoved));
    log.info("Expected exception: {}", expectedException.getName());
  }

  public static Stream<Arguments> createAddressSuccessParams() {
    var serviceEntity = Department.builder()
        .id(1L)
        .name("ser TEST")
        .build();
    var divisionEntity = Division.builder()
        .id(1L)
        .name("div TEST")
        .departments(List.of(serviceEntity))
        .build();
    var clientEntity = Client.builder()
        .id(1L)
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionEntity))
        .build();

    var serviceDto = DepartmentDto.builder()
        .name("ser TEST")
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .build();
    var divisionDto = DivisionDto.builder()
        .name("div TEST")
        .services(List.of())
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .services(List.of(serviceDto))
        .build();
    var clientDto = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .build();

    long totalAddressExpected = 3L;
    return Stream.of(Arguments.arguments(clientDto, clientEntity, totalAddressExpected));
  }

  public static Stream<Arguments> createAddressFailParams() {
    var serviceEntity = Department.builder()
        .id(1L)
        .name("ser TEST")
        .build();
    var divisionEntity = Division.builder()
        .id(1L)
        .name("div TEST")
        .departments(List.of(serviceEntity))
        .build();
    var clientEntity = Client.builder()
        .id(1L)
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionEntity))
        .build();

    var serviceDto = DepartmentDto.builder()
        .name("ser TEST")
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .build();
    var divisionDto = DivisionDto.builder()
        .name("div TEST")
        .services(List.of())
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .services(List.of(serviceDto))
        .build();
    var clientDto1 = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .build())
        .build();
    var clientDto2 = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .build())
        .build();
    var clientDto3 = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges TEST")
            .build())
        .build();

    var argument1 = Arguments.arguments(clientDto1, clientEntity, BadRequestException.class);
    var argument2 = Arguments.arguments(clientDto2, clientEntity, BadRequestException.class);
    var argument3 = Arguments.arguments(clientDto3, clientEntity, BadRequestException.class);
    return Stream.of(argument1, argument2, argument3);
  }

  public static Stream<Arguments> updateAddressSuccessParams() {
    var serviceEntity = Department.builder()
        .id(1L)
        .name("ser TEST")
        .build();
    var divisionEntity = Division.builder()
        .id(1L)
        .name("div TEST")
        .departments(List.of(serviceEntity))
        .build();
    var clientEntity = Client.builder()
        .id(1L)
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionEntity))
        .build();

    var serviceDto = DepartmentDto.builder()
        .name("ser TEST")
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .build();
    var divisionDto = DivisionDto.builder()
        .name("div TEST")
        .services(List.of())
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .services(List.of(serviceDto))
        .build();
    var clientDto = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .build();

    long totalAddressExpected = 3L;
    return Stream.of(
        Arguments.arguments(clientDto, clientEntity, new HashMap<>(), totalAddressExpected));
  }

  public static Stream<Arguments> updateAddressFailParams() {
    var serviceEntity = Department.builder()
        .id(1L)
        .name("ser TEST")
        .build();
    var divisionEntity = Division.builder()
        .id(1L)
        .name("div TEST")
        .departments(List.of(serviceEntity))
        .build();
    var clientEntity = Client.builder()
        .id(1L)
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionEntity))
        .build();

    var serviceDto = DepartmentDto.builder()
        .name("ser TEST")
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .build();
    var divisionDto = DivisionDto.builder()
        .name("div TEST")
        .services(List.of())
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges")
            .build())
        .services(List.of(serviceDto))
        .build();
    var clientDto1 = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .build())
        .build();
    var clientDto2 = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .build())
        .build();
    var clientDto3 = ClientDto.builder()
        .name("ClientXXXX")
        .email("client.xxxx@gmail.com")
        .divisions(List.of(divisionDto))
        .address(AddressDto.builder()
            .line1("DEST_ADR1")
            .line2("DEST_ADR2")
            .line3("DEST_ADR3")
            .line4("MARIA DUFROIX")
            .line5("6 RUE GEORGE SAND")
            .line6("87000 Limoges TEST")
            .build())
        .build();

    var argument1 = Arguments.arguments(clientDto1, clientEntity, new HashMap<>(),
        BadRequestException.class);
    var argument2 = Arguments.arguments(clientDto2, clientEntity, new HashMap<>(),
        BadRequestException.class);
    var argument3 = Arguments.arguments(clientDto3, clientEntity, new HashMap<>(),
        BadRequestException.class);
    return Stream.of(argument1, argument2, argument3);
  }
}