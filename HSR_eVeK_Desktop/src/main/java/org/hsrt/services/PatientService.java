package org.hsrt.services;

import org.hsrt.database.models.Patient;

public class PatientService {
    public void savePatient(Patient patient) {
        // Logic to save patient
        System.out.println("Patient saved: " + patient.getPatientId());
    }
}