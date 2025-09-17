package com.octopus.user_identity_service.controller;

import com.octopus.user_identity_service.exception.ResourceNotFoundException;
import com.octopus.user_identity_service.model.Device;
import com.octopus.user_identity_service.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        log.info("Creating device: {}", device.getName());
        Device createdDevice = deviceService.createDevice(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Device> registerDeviceForUser(@PathVariable Long userId, @RequestBody Device device) {
        log.info("Registering device for user: {}", userId);
        Device registeredDevice = deviceService.registerDevice(device, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredDevice);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @deviceSecurityService.canAccessDevice(authentication.name, #id)")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        return ResponseEntity.ok(device);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<List<Device>> getDevicesByUserId(@PathVariable Long userId) {
        List<Device> devices = deviceService.getDevicesByUserId(userId);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/user/{userId}/primary")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Device> getPrimaryDeviceByUserId(@PathVariable Long userId) {
        Device device = deviceService.getPrimaryDeviceByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No primary device found for user: " + userId));
        return ResponseEntity.ok(device);
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<List<Device>> getActiveDevicesByUserId(@PathVariable Long userId) {
        List<Device> devices = deviceService.getActiveDevicesByUserId(userId);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/ip/{ipAddress}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getDevicesByIpAddress(@PathVariable String ipAddress) {
        List<Device> devices = deviceService.getDevicesByIpAddress(ipAddress);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/mac/{macAddress}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getDevicesByMacAddress(@PathVariable String macAddress) {
        List<Device> devices = deviceService.getDevicesByMacAddress(macAddress);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getDevicesByType(@PathVariable String type) {
        List<Device> devices = deviceService.getDevicesByType(type);
        return ResponseEntity.ok(devices);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Device>> getAllDevices(Pageable pageable) {
        Page<Device> devices = deviceService.getAllDevices(pageable);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getAllDevicesList() {
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @deviceSecurityService.canAccessDevice(authentication.name, #id)")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device deviceDetails) {
        Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
        return ResponseEntity.ok(updatedDevice);
    }

    @PutMapping("/{id}/set-primary/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Device> setAsPrimaryDevice(@PathVariable Long id, @PathVariable Long userId) {
        Device device = deviceService.setAsPrimaryDevice(id, userId);
        return ResponseEntity.ok(device);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or @deviceSecurityService.canAccessDevice(authentication.name, #id)")
    public ResponseEntity<Device> activateDevice(@PathVariable Long id) {
        Device device = deviceService.activateDevice(id);
        return ResponseEntity.ok(device);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or @deviceSecurityService.canAccessDevice(authentication.name, #id)")
    public ResponseEntity<Device> deactivateDevice(@PathVariable Long id) {
        Device device = deviceService.deactivateDevice(id);
        return ResponseEntity.ok(device);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @deviceSecurityService.canAccessDevice(authentication.name, #id)")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Long> countDevicesByUserId(@PathVariable Long userId) {
        long count = deviceService.countDevicesByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Long> countActiveDevicesByUserId(@PathVariable Long userId) {
        long count = deviceService.countActiveDevicesByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}/is-primary")
    @PreAuthorize("hasRole('ADMIN') or @deviceSecurityService.canAccessDevice(authentication.name, #id)")
    public ResponseEntity<Boolean> isDevicePrimary(@PathVariable Long id) {
        boolean isPrimary = deviceService.isDevicePrimary(id);
        return ResponseEntity.ok(isPrimary);
    }
}
