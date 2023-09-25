package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.dto.ClientDto;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.ReturnAddress;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.repository.ReturnAddressRepository;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import com.tessi.cxm.pfl.shared.utils.AddressValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnAddressService {

  private final ReturnAddressRepository addressRepository;
  private final AddressValidator addressValidator;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public List<ReturnAddress> getReturnAddress(long clientId) {
    return addressRepository.findAllByClientId(clientId);
  }

  public List<ReturnAddress> saveAll(ClientDto clientDto, Client clientEntity) {
    List<ReturnAddress> addressList = new ArrayList<>();

    if (ObjectUtils.isNotEmpty(clientDto.getAddress())) {
      this.addressValidator.validate(clientDto.getAddress());
      ReturnAddress clientAddresses =
          this.modelMapper.map(clientDto.getAddress(), ReturnAddress.class);
      this.addingAddress(
          addressList, clientAddresses, clientEntity, clientEntity.getId(), AddressType.CLIENT);
    }

    clientDto
        .getDivisions()
        .forEach(
            divisionDto ->
                clientEntity.getDivisions().stream()
                    .filter(division -> division.getName().equals(divisionDto.getName()))
                    .findFirst()
                    .ifPresent(
                        division -> {
                          if (ObjectUtils.isNotEmpty(divisionDto.getAddress())) {
                            this.addressValidator.validate(divisionDto.getAddress());
                            ReturnAddress divisionAddresses =
                                this.modelMapper.map(divisionDto.getAddress(), ReturnAddress.class);
                            this.addingAddress(
                                addressList,
                                divisionAddresses,
                                clientEntity,
                                division.getId(),
                                AddressType.DIVISION);
                          }

                          divisionDto
                              .getServices()
                              .forEach(
                                  departmentDto ->
                                      division.getDepartments().stream()
                                          .filter(
                                              department ->
                                                  department
                                                      .getName()
                                                      .equals(departmentDto.getName()))
                                          .findFirst()
                                          .ifPresent(
                                              department ->
                                                  saveServiceAddress(
                                                      clientEntity,
                                                      addressList,
                                                      departmentDto,
                                                      department)));
                        }));

    return this.addressRepository.saveAll(addressList);
  }

  public List<ReturnAddress> updateAll(ClientDto clientDto, Client clientEntity,
      Map<Long, AddressType> addressRemoved) {
    Map<String, Long> refIdMap = new HashMap<>();
    clientEntity.getDivisions().forEach(division -> {
      refIdMap.put(division.getName(), division.getId());
      division.getDepartments().forEach(department ->
          refIdMap.put(division.getName().concat("_".concat(department.getName())),
              department.getId()));
    });

    if (addressRemoved.size() > 0) {
      addressRemoved.forEach(this.addressRepository::deleteByRefIdAndType);
    }

    List<ReturnAddress> addressList = new ArrayList<>();
    // Client address.
    if (clientDto.getAddress() != null) {
      this.addressValidator.validate(clientDto.getAddress());
      var addressOptional = this.addressRepository.findByRefIdAndType(clientDto.getId(),
          AddressType.CLIENT);
      if (addressOptional.isPresent()) {
        var address = addressOptional.get();
        this.modelMapper.map(clientDto.getAddress(), address);
        addressList.add(address);
      } else {
        var newAddress = this.modelMapper.map(clientDto.getAddress(),
            ReturnAddress.class);
        this.addingAddress(addressList, newAddress, clientEntity, clientEntity.getId(),
            AddressType.CLIENT);
      }
    }

    // Division address.
    clientDto.getDivisions().forEach(divisionDto -> {
      if (ObjectUtils.isNotEmpty(divisionDto.getAddress())) {
        this.addressValidator.validate(divisionDto.getAddress());
        var addressOptional = this.addressRepository.findByRefIdAndType(divisionDto.getId(),
            AddressType.DIVISION);
        if (addressOptional.isPresent()) {
          var address = addressOptional.get();
          this.modelMapper.map(divisionDto.getAddress(), address);
          addressList.add(address);
        } else {
          var refId = refIdMap.get(divisionDto.getName());
          var newAddress = this.modelMapper.map(divisionDto.getAddress(),
              ReturnAddress.class);
          this.addingAddress(addressList, newAddress, clientEntity, refId,
              AddressType.DIVISION);
        }
      }

      // Service address.
      divisionDto.getServices().forEach(departmentDto -> {
        if (ObjectUtils.isNotEmpty(departmentDto.getAddress())) {
          this.addressValidator.validate(departmentDto.getAddress());
          var addressOptional = this.addressRepository.findByRefIdAndType(departmentDto.getId(),
              AddressType.SERVICE);
          if (addressOptional.isPresent()) {
            var address = addressOptional.get();
            this.modelMapper.map(departmentDto.getAddress(), address);
            addressList.add(address);
          } else {
            var refId = refIdMap.get(
                divisionDto.getName().concat("_".concat(departmentDto.getName())));
            var newAddress = this.modelMapper.map(departmentDto.getAddress(),
                ReturnAddress.class);
            this.addingAddress(addressList, newAddress, clientEntity, refId,
                AddressType.SERVICE);
          }
        }
      });
    });

    return this.addressRepository.saveAll(addressList);
  }

  private void saveServiceAddress(
      Client clientEntity,
      List<ReturnAddress> addressList,
      DepartmentDto departmentDto,
      Department department) {
    if (ObjectUtils.isNotEmpty(departmentDto.getAddress())) {
      this.addressValidator.validate(departmentDto.getAddress());
      if (ObjectUtils.isNotEmpty(departmentDto.getAddress())) {
        ReturnAddress serviceAddresses =
            this.modelMapper.map(departmentDto.getAddress(), ReturnAddress.class);
        this.addingAddress(
            addressList, serviceAddresses, clientEntity, department.getId(), AddressType.SERVICE);
      }
    }
  }

  private void addingAddress(
      List<ReturnAddress> addressList,
      ReturnAddress address,
      Client clientEntity,
      long refId,
      AddressType addressType) {
    address.setClient(clientEntity);
    address.setType(addressType);
    address.setRefId(refId);
    addressList.add(address);
  }

  /**
   * Save a new ReturnAddress and delete old returnAddress.
   *
   * @return {@link ReturnAddress} else return null if addressType is not user level.
   */
  public ReturnAddress saveReturnAddress(UserEntity userEntity, AddressDto addressDto) {
    var clientEntity = userEntity.getDepartment().getDivision().getClient();
    // Validate user returnAddress level.
    this.addressValidator.validate(addressDto);
    // Map user AddressType level.
    ReturnAddress returnAddress  = this.modelMapper.map(addressDto, ReturnAddress.class);
    var oldReturnAddress =
        this.addressRepository.findByRefIdAndTypeAndClientId(
            userEntity.getId(), AddressType.USER, clientEntity.getId());
    oldReturnAddress.ifPresent(address -> returnAddress.setId(address.getId()));

    // set reference info to a new returnAddress.
    returnAddress.setClient(clientEntity); // set ref client.
    returnAddress.setRefId(userEntity.getId()); // Set ref owner.
    returnAddress.setType(AddressType.USER); // Set type owner.
    return this.addressRepository.save(returnAddress);
  }

  public void deleteOldReturnAddress(Long refId, AddressType addressType, long clientId) {
    this.addressRepository
        .findByRefIdAndTypeAndClientId(refId, addressType, clientId)
        .ifPresent(this.addressRepository::delete);
  }

  public void deleteReturnAddressByClient(long clientId) {
    this.addressRepository.deleteByClientId(clientId);
  }

  public Optional<ReturnAddress> findReturnAddress(
      Long refId, AddressType addressType, long clientId) {
    return this.addressRepository.findByRefIdAndTypeAndClientId(refId, addressType, clientId);
  }
}
