package com.octopus.user_identity_service.service;

import com.octopus.user_identity_service.enums.AddressType;
import com.octopus.user_identity_service.exception.ResourceNotFoundException;
import com.octopus.user_identity_service.model.Address;
import com.octopus.user_identity_service.model.User;
import com.octopus.user_identity_service.repository.AddressRepository;
import com.octopus.user_identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public Address createAddress(Address address) {
        log.info("Creating address");
        return addressRepository.save(address);
    }

    public Address createAddressForUser(Address address, Long userId) {
        log.info("Creating address for user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user already has an address
        if (user.getAddress() != null) {
            throw new IllegalArgumentException("User already has an address. Use update instead.");
        }

        address = addressRepository.save(address);
        user.setAddress(address);
        userRepository.save(user);
        
        return address;
    }

    @Transactional(readOnly = true)
    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Address> getAddressByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(User::getAddress);
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressesByCity(String city) {
        return addressRepository.findByCity(city);
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressesByState(String state) {
        return addressRepository.findByState(state);
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressesByCountry(String country) {
        return addressRepository.findByCountry(country);
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressesByType(AddressType addressType) {
        return addressRepository.findByAddressType(addressType.name());
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressesByStreetAndCityAndState(String street, String city, String state) {
        return addressRepository.findByStreetAndCityAndState(street, city, state);
    }

    @Transactional(readOnly = true)
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Address> getAllAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }

    public Address updateAddress(Long id, Address addressDetails) {
        log.info("Updating address with id: {}", id);
        
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        address.setStreet(addressDetails.getStreet());
        address.setCity(addressDetails.getCity());
        address.setState(addressDetails.getState());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setCountry(addressDetails.getCountry());
        address.setDescriptionOfAddress(addressDetails.getDescriptionOfAddress());
        address.setLongAddress(addressDetails.getLongAddress());
        address.setLongitude(addressDetails.getLongitude());
        address.setLatitude(addressDetails.getLatitude());
        address.setAddressType(addressDetails.getAddressType());

        return addressRepository.save(address);
    }

    public Address updateUserAddress(Long userId, Address addressDetails) {
        log.info("Updating address for user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = user.getAddress();
        if (address == null) {
            throw new ResourceNotFoundException("User does not have an address");
        }

        address.setStreet(addressDetails.getStreet());
        address.setCity(addressDetails.getCity());
        address.setState(addressDetails.getState());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setCountry(addressDetails.getCountry());
        address.setDescriptionOfAddress(addressDetails.getDescriptionOfAddress());
        address.setLongAddress(addressDetails.getLongAddress());
        address.setLongitude(addressDetails.getLongitude());
        address.setLatitude(addressDetails.getLatitude());
        address.setAddressType(addressDetails.getAddressType());

        return addressRepository.save(address);
    }

    public void deleteAddress(Long id) {
        log.info("Deleting address with id: {}", id);
        
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }
        
        addressRepository.deleteById(id);
    }

    public void deleteUserAddress(Long userId) {
        log.info("Deleting address for user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = user.getAddress();
        if (address == null) {
            throw new ResourceNotFoundException("User does not have an address");
        }

        user.setAddress(null);
        userRepository.save(user);
        addressRepository.deleteById(address.getId());
    }

    @Transactional(readOnly = true)
    public boolean userHasAddress(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getAddress() != null)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public long countAddresses() {
        return addressRepository.count();
    }

    @Transactional(readOnly = true)
    public long countAddressesByCity(String city) {
        return addressRepository.findByCity(city).size();
    }

    @Transactional(readOnly = true)
    public long countAddressesByState(String state) {
        return addressRepository.findByState(state).size();
    }

    @Transactional(readOnly = true)
    public long countAddressesByCountry(String country) {
        return addressRepository.findByCountry(country).size();
    }
}
