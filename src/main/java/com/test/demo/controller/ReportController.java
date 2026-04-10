package com.test.demo.controller;

import com.test.demo.model.AnswerEntry;
import com.test.demo.model.DataEntry;
import com.test.demo.model.Options;
import com.test.demo.model.Question;
import com.test.demo.repository.AnswerEntryRepository;
import com.test.demo.repository.OptionsRepository;
import com.test.demo.repository.QuestionRepository;
import com.test.demo.service.DataEntryService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final DataEntryService dataEntryService;
    private final AnswerEntryRepository answerEntryRepository;
    private final QuestionRepository questionRepository;
    private final OptionsRepository optionsRepository;

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam String type,
            @RequestParam String startDate,
            @RequestParam String endDate) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fullSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Filter entries by type and date range (submittedOn is "dd/MM/yyyy HH:mm")
        List<DataEntry> entries = dataEntryService.getAllDataEntries().stream()
                .filter(e -> e.getType() != null && e.getType().equalsIgnoreCase(type))
                .filter(e -> isWithinDateRange(e.getSubmittedOn(), startDate, endDate, sdf, fullSdf))
                .collect(Collectors.toList());

        List<Question> questions = questionRepository.findByType(type).stream()
                .sorted(Comparator.comparing(Question::getId))
                .collect(Collectors.toList());

        // Pre-fetch all AnswerEntries for the filtered entries to avoid N+1 queries
        List<Long> entryIds = entries.stream().map(DataEntry::getId).collect(Collectors.toList());
        Map<Long, AnswerEntry> answerMap = answerEntryRepository.findAll().stream()
                .filter(ae -> ae.getDataEntryId() != null && entryIds.contains(ae.getDataEntryId()))
                .collect(Collectors.toMap(AnswerEntry::getDataEntryId, ae -> ae, (a, b) -> a));

        // Pre-fetch all Options to avoid querying inside the nested loop
        Map<Long, Options> optionsLookup = optionsRepository.findAll().stream()
                .collect(Collectors.toMap(Options::getId, o -> o, (a, b) -> a));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data Entry Report");

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle greenStyle = workbook.createCellStyle();
            greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            greenStyle.setBorderBottom(BorderStyle.THIN);

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] staticHeaders = {"S.No", "Name", "Mobile", "Email", "Assessment Type","Score(Out of 20)"};
            for (int i = 0; i < staticHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(staticHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < questions.size(); i++) {
                Cell cell = headerRow.createCell(staticHeaders.length + i);
                cell.setCellValue(questions.get(i).getName());
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowNum = 1;
            for (DataEntry entry : entries) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(entry.getName());
                row.createCell(2).setCellValue(entry.getMobile());
                row.createCell(3).setCellValue(entry.getEmail());
                row.createCell(4).setCellValue(entry.getType());
                row.createCell(5).setCellValue(entry.getTotalScore());

                AnswerEntry ae = answerMap.get(entry.getId());
                if (ae != null) {
                    for (int i = 0; i < questions.size(); i++) {
                        Integer questionId = questions.get(i).getId() != null ? questions.get(i).getId().intValue() : null;
                        Integer ansId = getAnswerValue(ae, questionId);
                        
                        if (ansId != null) {
                            Options opt = optionsLookup.get(ansId.longValue());
                            if (opt != null) {
                                Cell cell = row.createCell(staticHeaders.length + i);
                                cell.setCellValue(opt.getOptionName());
                                if (opt.getMarks() != null && opt.getMarks() == 1) {
                                    cell.setCellStyle(greenStyle);
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < staticHeaders.length + questions.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=DataEntryReport_" + type + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);
        }
    }

    private boolean isWithinDateRange(String submittedOn, String start, String end, SimpleDateFormat sdf, SimpleDateFormat fullSdf) {
        if (submittedOn == null || submittedOn.isEmpty()) return false;
        try {
            Date submittedDate = fullSdf.parse(submittedOn);
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DATE, 1);
            Date nextDay = c.getTime();
            return !submittedDate.before(startDate) && submittedDate.before(nextDay);
        } catch (ParseException e) {
            return false;
        }
    }

    private Integer getAnswerValue(AnswerEntry ae, Integer index) {
        if (ae == null || index == null || index < 1 || index > 60) return null;
        try {
            Method method = ae.getClass().getMethod("getAns" + index);
            return (Integer) method.invoke(ae);
        } catch (Exception e) {
            return null;
        }
    }
}