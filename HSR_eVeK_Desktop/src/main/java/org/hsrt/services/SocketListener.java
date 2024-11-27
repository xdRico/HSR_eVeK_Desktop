package org.hsrt.services;

import org.hsrt.config.SocketConfig;
import org.hsrt.database.models.*;

import java.io.IOException;
import java.net.SocketException;

public class SocketListener implements Runnable{
    private final SocketConfig socketConfig;
    private volatile boolean running = true;

    public SocketListener(SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Object receivedObject = socketConfig.receiveObject();
                processReceivedObject(receivedObject);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                // Optionally handle reconnection or error state
            }
        }
    }

private void processReceivedObject(Object receivedObject) {
    switch (receivedObject.getClass().getSimpleName()) {
        case "User":
            User user = (User) receivedObject;
            UserService userService = new UserService();
            userService.saveUser(user);
            System.out.println("Received and processed User object: " + user.getUserName());
            break;
        case "Address":
            Address address = (Address) receivedObject;
            AddressService addressService = new AddressService();
            addressService.saveAddress(address);
            System.out.println("Received and processed Address object: " + address.getAddressId());
            break;
        case "Insurance":
            Insurance insurance = (Insurance) receivedObject;
            InsuranceService insuranceService = new InsuranceService();
            insuranceService.saveInsurance(insurance);
            System.out.println("Received and processed Insurance object: " + insurance.getInsuranceId());
            break;
        case "Patient":
            Patient patient = (Patient) receivedObject;
            PatientService patientService = new PatientService();
            patientService.savePatient(patient);
            System.out.println("Received and processed Patient object: " + patient.getPatientId());
            break;
        case "InsuranceData":
            InsuranceData insuranceData = (InsuranceData) receivedObject;
            InsuranceDataService insuranceDataService = new InsuranceDataService();
            insuranceDataService.saveInsuranceData(insuranceData);
            System.out.println("Received and processed InsuranceData object: " + insuranceData.getInsuranceDataId());
            break;
        case "ServiceProvider":
            ServiceProvider serviceProvider = (ServiceProvider) receivedObject;
            ServiceProviderService serviceProviderService = new ServiceProviderService();
            serviceProviderService.saveServiceProvider(serviceProvider);
            System.out.println("Received and processed ServiceProvider object: " + serviceProvider.getServiceProviderId());
            break;
        case "TransportDocument":
            TransportDocument transportDocument = (TransportDocument) receivedObject;
            TransportDocumentService transportDocumentService = new TransportDocumentService();
            transportDocumentService.saveTransportDocument(transportDocument);
            System.out.println("Received and processed TransportDocument object: " + transportDocument.getTransportDocumentId());
            break;
        case "TransportDetails":
            TransportDetails transportDetails = (TransportDetails) receivedObject;
            TransportDetailsService transportDetailsService = new TransportDetailsService();
            transportDetailsService.saveTransportDetails(transportDetails);
            System.out.println("Received and processed TransportDetails object: " + transportDetails.getTransportId());
            break;
        default:
            System.out.println("Received an unknown object type: " + receivedObject.getClass().getName());
            break;
    }
}

    public void stop() {
        running = false;
    }
}
