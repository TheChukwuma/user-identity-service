package com.octopus.user_identity_service.repository;

import com.octopus.user_identity_service.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByUserId(Long userId);

    Optional<Device> findByUserIdAndIsPrimaryTrue(Long userId);

    List<Device> findByUserIdAndIsActiveTrue(Long userId);

    List<Device> findByIpAddress(String ipAddress);

    List<Device> findByMacAddress(String macAddress);

    @Query("SELECT d FROM Device d WHERE d.user.id = :userId AND d.type = :type")
    List<Device> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    @Query("SELECT d FROM Device d WHERE d.os = :os")
    List<Device> findByOs(@Param("os") String os);

    @Query("SELECT d FROM Device d WHERE d.isPrimary = true AND d.user.id = :userId")
    Optional<Device> findPrimaryDeviceByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
