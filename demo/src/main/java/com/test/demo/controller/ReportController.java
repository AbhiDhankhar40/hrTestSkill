package com.test.demo.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.service.JasperReportService;

@RestController
public class ReportController {

    private final JasperReportService reportService;

    public ReportController(JasperReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report/simple")
    public ResponseEntity<ByteArrayResource> getSimpleReport() throws Exception {
        byte[] pdf = reportService.generateSimpleReport();
        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"simpleReport.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}