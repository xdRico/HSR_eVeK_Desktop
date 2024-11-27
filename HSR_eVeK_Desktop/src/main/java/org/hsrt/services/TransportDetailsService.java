package org.hsrt.services;

import org.hsrt.database.models.TransportDetails;

public class TransportDetailsService {
    public void saveTransportDetails(TransportDetails transportDetails) {
        // Logic to save transport details
        System.out.println("TransportDetails saved: " + transportDetails.getTransportId());
    }
}