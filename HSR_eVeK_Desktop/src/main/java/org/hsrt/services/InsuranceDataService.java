package org.hsrt.services;

import org.hsrt.database.models.InsuranceData;

public class InsuranceDataService {
    public void saveInsuranceData(InsuranceData insuranceData) {
        // Logic to save insurance data
        System.out.println("InsuranceData saved: " + insuranceData.getInsuranceDataId());
    }
}