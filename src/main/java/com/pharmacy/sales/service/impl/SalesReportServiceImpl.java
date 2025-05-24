package com.pharmacy.sales.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.pharmacy.sales.entity.Sales;
import com.pharmacy.sales.repository.SalesRepository;
import com.pharmacy.sales.service.SalesReportService;

@Service
public class SalesReportServiceImpl implements SalesReportService {

    private static final Logger logger = LoggerFactory.getLogger(SalesReportServiceImpl.class);

    @Autowired
    private SalesRepository salesRepo;

    @Override
    public byte[] generateSalesReport() throws IOException {
        logger.info("Generating PDF sales report...");

        // Fetch all sales records from the database
        List<Sales> salesList = salesRepo.findAll();

        // Log warnings or stats based on record availability
        if (salesList.isEmpty()) {
            logger.warn("No sales records found to include in the report.");
        } else {
            logger.info("Total sales records found: {}", salesList.size());
        }

        // Setup PDF output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Add title to the document
        document.add(new Paragraph("Sales Report").setBold().setFontSize(18));

        // Define column widths and create the table
        float[] columnWidths = {100f, 100f, 100f, 80f, 165f, 100f, 100f, 100f};
        Table table = new Table(columnWidths);

        // Add table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Order Id").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Drug Name").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Batch Id").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Date and Time").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total Amount").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Paid Amount").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Balance").setBold()));

        // Populate the table with sales data
        for (Sales sale : salesList) {
            table.addCell(String.valueOf(sale.getOrderId()));
            table.addCell(safeString(sale.getDrugName()));
            table.addCell(safeString(sale.getBatchId()));
            table.addCell(String.valueOf(sale.getQuantity()));
            table.addCell(String.valueOf(sale.getSaleDate()));
            table.addCell(String.valueOf(sale.getTotalPrice()));
            table.addCell(String.valueOf(sale.getPaidAmount()));
            table.addCell(String.valueOf(sale.getBalance()));
        }

        // Add the table to the document and close it
        document.add(table);
        document.close();

        logger.info("Sales report PDF generated successfully.");
        // Return the PDF as a byte array
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Utility method to safely convert null strings to "N/A" to prevent null entries in the PDF.
     */
    private String safeString(String value) {
        return value == null ? "N/A" : value;
    }
}
