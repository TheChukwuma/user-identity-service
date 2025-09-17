package com.octopus.user_identity_service.service;

import com.octopus.user_identity_service.exception.ResourceNotFoundException;
import com.octopus.user_identity_service.model.Device;
import com.octopus.user_identity_service.model.User;
import com.octopus.user_identity_service.repository.DeviceRepository;
import com.octopus.user_identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public Device registerDevice(Device device, Long userId) {
        log.info("Registering device {} for user with id: {}", device.getName(), userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        device.setUser(user);
        
        // If this is the first device or marked as primary, make it primary
        if (device.getIsPrimary() || deviceRepository.countByUserId(userId) == 0) {
            setAsPrimaryDevice(device.getId(), userId);
        }

        return deviceRepository.save(device);
    }

    public Device createDevice(Device device) {
        log.info("Creating device: {}", device.getName());
        return deviceRepository.save(device);
    }

    @Transactional(readOnly = true)
    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Device> getDevicesByUserId(Long userId) {
        return deviceRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Device> getPrimaryDeviceByUserId(Long userId) {
        return deviceRepository.findPrimaryDeviceByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Device> getActiveDevicesByUserId(Long userId) {
        return deviceRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Transactional(readOnly = true)
    public List<Device> getDevicesByIpAddress(String ipAddress) {
        return deviceRepository.findByIpAddress(ipAddress);
    }

    @Transactional(readOnly = true)
    public List<Device> getDevicesByMacAddress(String macAddress) {
        return deviceRepository.findByMacAddress(macAddress);
    }

    @Transactional(readOnly = true)
    public List<Device> getDevicesByType(String type) {
        return deviceRepository.findAll().stream()
                .filter(device -> device.getType() != null && device.getType().name().equals(type))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Device> getAllDevices(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }

    public Device updateDevice(Long id, Device deviceDetails) {
        log.info("Updating device with id: {}", id);
        
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        device.setName(deviceDetails.getName());
        device.setType(deviceDetails.getType());
        device.setOs(deviceDetails.getOs());
        device.setOsVersion(deviceDetails.getOsVersion());
        device.setDeviceModel(deviceDetails.getDeviceModel());
        device.setIpAddress(deviceDetails.getIpAddress());
        device.setMacAddress(deviceDetails.getMacAddress());
        device.setUserAgent(deviceDetails.getUserAgent());
        device.setIsActive(deviceDetails.getIsActive());

        return deviceRepository.save(device);
    }

    public Device setAsPrimaryDevice(Long deviceId, Long userId) {
        log.info("Setting device {} as primary for user {}", deviceId, userId);
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        if (!device.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Device does not belong to the specified user");
        }

        // First, unset all primary devices for this user
        List<Device> userDevices = deviceRepository.findByUserId(userId);
        userDevices.forEach(d -> d.setIsPrimary(false));
        deviceRepository.saveAll(userDevices);

        // Set this device as primary
        device.setIsPrimary(true);
        return deviceRepository.save(device);
    }

    public Device updateLastLogin(Long deviceId) {
        log.info("Updating last login for device with id: {}", deviceId);
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        device.setLastLoginAt(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    public Device deactivateDevice(Long id) {
        log.info("Deactivating device with id: {}", id);
        
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        device.setIsActive(false);
        
        // If this was the primary device, set another active device as primary
        if (device.getIsPrimary()) {
            List<Device> activeDevices = deviceRepository.findByUserIdAndIsActiveTrue(device.getUser().getId());
            if (!activeDevices.isEmpty()) {
                activeDevices.get(0).setIsPrimary(true);
                deviceRepository.save(activeDevices.get(0));
            }
        }
        
        return deviceRepository.save(device);
    }

    public Device activateDevice(Long id) {
        log.info("Activating device with id: {}", id);
        
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        device.setIsActive(true);
        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        log.info("Deleting device with id: {}", id);
        
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        // If this was the primary device, set another device as primary
        if (device.getIsPrimary()) {
            List<Device> remainingDevices = deviceRepository.findByUserId(device.getUser().getId());
            remainingDevices.remove(device);
            if (!remainingDevices.isEmpty()) {
                remainingDevices.get(0).setIsPrimary(true);
                deviceRepository.save(remainingDevices.get(0));
            }
        }

        deviceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countDevicesByUserId(Long userId) {
        return deviceRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countActiveDevicesByUserId(Long userId) {
        return deviceRepository.findByUserIdAndIsActiveTrue(userId).size();
    }

    @Transactional(readOnly = true)
    public boolean isDevicePrimary(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .map(Device::getIsPrimary)
                .orElse(false);
    }
}
