package com.test.demo.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class JasperReportService {

    public byte[] generateSimpleReport() throws JRException {
        // Load and compile template
        InputStream jrXml = getClass().getResourceAsStream("/reports/simple_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrXml);

        // Prepare data (simple Java data list)
        Map<String, Object> record = new HashMap<>();
        record.put("message", "Hello from JasperReports!");
        List<Map<String, Object>> dataList = Collections.singletonList(record);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

        // Fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}