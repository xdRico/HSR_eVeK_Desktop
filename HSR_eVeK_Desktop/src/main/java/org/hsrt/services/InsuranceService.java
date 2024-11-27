package org.hsrt.services;

import org.hsrt.database.models.Insurance;

public class InsuranceService {
    public void saveInsurance(Insurance insurance) {
        // Logic to save insurance to the database
        System.out.println("Insurance saved: " + insurance.getInsuranceId());
    }
}