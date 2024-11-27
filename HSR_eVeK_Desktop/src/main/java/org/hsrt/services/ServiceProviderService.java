package org.hsrt.services;

import org.hsrt.database.models.ServiceProvider;

public class ServiceProviderService {
    public void saveServiceProvider(ServiceProvider serviceProvider) {
        // Logic to save service provider
        System.out.println("ServiceProvider saved: " + serviceProvider.getServiceProviderId());
    }
}