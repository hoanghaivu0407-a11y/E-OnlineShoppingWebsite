package com.e_onlineshoppingwebsite.store.Service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_onlineshoppingwebsite.store.Model.Address;
import com.e_onlineshoppingwebsite.store.Repository.AddressRepository;
import com.e_onlineshoppingwebsite.store.Service.AddressesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressesServiceImpl implements AddressesService {

    private final AddressRepository addressRepository;

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByMember_UserId(userId);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ"));
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        return addressRepository.findByMember_UserIdAndIsDefaultTrue(userId)
                .orElse(null);
    }

    @Override
    public void save(Address address) {
        addressRepository.save(address);
    }

    @Override
    public void update(Address address) {

        if (address == null || address.getAddressId() == null) {
            throw new IllegalArgumentException("Thiếu ID địa chỉ");
        }

        Address old = getAddressById(address.getAddressId());

        old.setReceiverName(address.getReceiverName());
        old.setReceiverPhone(address.getReceiverPhone());
        old.setProvince(address.getProvince());
        old.setWard(address.getWard());
        old.setStreetAddress(address.getStreetAddress());

        addressRepository.save(old);
    }

    @Override
    public void deleteAddress(Long id, Long userId) {

        Address address = addressRepository
                .findByAddressIdAndMember_UserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Địa chỉ không thuộc người dùng"));

        addressRepository.delete(address);
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {

        addressRepository
                .findByAddressIdAndMember_UserId(addressId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ"));

        List<Address> addresses = addressRepository.findByMember_UserId(userId);

        for (Address address : addresses) {
            address.setIsDefault(
                    address.getAddressId().equals(addressId));
        }

        addressRepository.saveAll(addresses);
    }
}
