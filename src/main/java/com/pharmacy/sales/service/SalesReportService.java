package com.pharmacy.sales.service;

import java.io.IOException;

public interface SalesReportService {
    byte[] generateSalesReport() throws IOException;
}