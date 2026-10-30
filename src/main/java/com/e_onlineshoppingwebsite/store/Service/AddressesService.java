package com.e_onlineshoppingwebsite.store.Service;

import java.util.List;

import com.e_onlineshoppingwebsite.store.Model.Address;

public interface AddressesService {

    List<Address> getAllAddresses();

    List<Address> getAddressesByUserId(Long userId);

    Address getAddressById(Long id);

    Address getDefaultAddress(Long userId);

    void save(Address address);

    void update(Address address);

    void deleteAddress(Long id, Long userId);

    void setDefaultAddress(Long userId, Long addressId);
}