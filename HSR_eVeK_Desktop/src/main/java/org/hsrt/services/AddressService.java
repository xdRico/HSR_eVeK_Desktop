package org.hsrt.services;

import org.hsrt.database.models.Address;

public class AddressService {
    public void saveAddress(Address address) {
        // Logic to save address
        System.out.println("Address saved: " + address.getAddressId());
    }
}