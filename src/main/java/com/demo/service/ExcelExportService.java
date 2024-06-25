package com.demo.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.demo.model.DuplicateEmployeeModel;

@Service
public class ExcelExportService {
	
	public ByteArrayInputStream exportToExcel(List<DuplicateEmployeeModel> employees) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Duplicates");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("employeeId");
            headerRow.createCell(1).setCellValue("fullname");
            headerRow.createCell(2).setCellValue("number");
            headerRow.createCell(3).setCellValue("company");

            // Populate data rows
            int rowIdx = 1;
            for (DuplicateEmployeeModel employee : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(employee.getEmployeeId());
                row.createCell(1).setCellValue(employee.getFullname());
                row.createCell(2).setCellValue(employee.getNumber());
                row.createCell(3).setCellValue(employee.getCompany());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel file: " + e.getMessage());
        }
    }
	
}
