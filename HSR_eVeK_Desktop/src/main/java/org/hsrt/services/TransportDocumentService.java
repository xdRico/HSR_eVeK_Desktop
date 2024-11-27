package org.hsrt.services;

import org.hsrt.database.models.TransportDocument;

public class TransportDocumentService {
    public void saveTransportDocument(TransportDocument transportDocument) {
        // Logic to save transport document
        System.out.println("TransportDocument saved: " + transportDocument.getTransportDocumentId());
    }
}