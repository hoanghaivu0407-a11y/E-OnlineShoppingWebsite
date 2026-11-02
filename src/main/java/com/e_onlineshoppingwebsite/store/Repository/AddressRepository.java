package com.e_onlineshoppingwebsite.store.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_onlineshoppingwebsite.store.Model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

        List<Address> findByMember_UserId(Long userId);

        Optional<Address> findByMember_UserIdAndIsDefaultTrue(Long userId);

        Optional<Address> findByAddressIdAndMember_UserId(
                        Long addressId,
                        Long userId);

        boolean existsByAddressIdAndMember_UserId(
                        Long addressId,
                        Long userId);
}