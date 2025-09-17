package com.octopus.user_identity_service.repository;

import com.octopus.user_identity_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.street = :street AND a.city = :city AND a.state = :state")
    List<Address> findByStreetAndCityAndState(@Param("street") String street, 
                                            @Param("city") String city, 
                                            @Param("state") String state);

    @Query("SELECT a FROM Address a WHERE a.city = :city")
    List<Address> findByCity(@Param("city") String city);

    @Query("SELECT a FROM Address a WHERE a.state = :state")
    List<Address> findByState(@Param("state") String state);

    @Query("SELECT a FROM Address a WHERE a.country = :country")
    List<Address> findByCountry(@Param("country") String country);

    @Query("SELECT a FROM Address a WHERE a.addressType = :addressType")
    List<Address> findByAddressType(@Param("addressType") String addressType);
}
