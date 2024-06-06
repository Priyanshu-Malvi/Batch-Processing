package com.demo.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

import com.demo.dto.EmployeeDtoClass;

public class DelegatingItemStreamReader implements ItemStreamReader<EmployeeDtoClass> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingItemStreamReader.class);

	private final String filePath;
//	private final String[] columnNames;
	private ItemStreamReader<EmployeeDtoClass> delegate;

	public DelegatingItemStreamReader(String filePath) {
		this.filePath = filePath;
		this.delegate = createDelegate();
	}

	private ItemStreamReader<EmployeeDtoClass> createDelegate() {

		if (filePath.endsWith(".csv")) {
			// Read the header line to get the column names
			String[] columnNames = readHeader(filePath);

			FlatFileItemReader<EmployeeDtoClass> reader = new FlatFileItemReaderBuilder<EmployeeDtoClass>()
					.name("employeeCsvItemReader")
					.resource(new FileSystemResource(filePath))
					.linesToSkip(1)
					.lineMapper(lineMapper(columnNames))
					.build();
			
			LOGGER.info("Reading the csv file...");
			return reader;
		} else if (filePath.endsWith(".xlsx")) {

			String[] columnNames = readXlsxHeader(filePath);

			PoiItemReader<EmployeeDtoClass> reader = new PoiItemReader<>();
			reader.setLinesToSkip(1); // Skip header row
			reader.setResource(new FileSystemResource(filePath));
			reader.setRowMapper((RowMapper<EmployeeDtoClass>) new EmployeeDtoRowMapper(columnNames));
			
			LOGGER.info("Reading the excel file...");
			return reader;
		} else {
			throw new IllegalArgumentException("Unsupported file type: " + filePath);
		}

	}

	private String[] readHeader(String filePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String headerLine = reader.readLine();
			if (headerLine != null) {
				return headerLine.split(",");
			} else {
				throw new IllegalArgumentException("CSV file is empty: " + filePath);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read CSV header: " + filePath, e);
		}
	}

	private String[] readXlsxHeader(String filePath) {
		try (FileInputStream fis = new FileInputStream(filePath)) {
			Workbook workbook = WorkbookFactory.create(fis);
	        if (workbook.getNumberOfSheets() == 0) {
	            throw new IllegalArgumentException("XLSX file is empty: " + filePath);
	        }
	        
	        Sheet sheet = workbook.getSheetAt(0);
			
			Row headerRow = sheet.getRow(0);
			
			if (headerRow == null || headerRow.getLastCellNum() == -1) {
	            throw new IllegalArgumentException("XLSX file has no columns: " + filePath);
	        }
			
			List<String> columnNames = new ArrayList<>();
			headerRow.forEach(cell -> columnNames.add(cell.getStringCellValue()));
			return columnNames.toArray(new String[0]);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read XLSX header: " + filePath, e);
		}
	}

	private LineMapper<EmployeeDtoClass> lineMapper(String[] columnNames) {
		DefaultLineMapper<EmployeeDtoClass> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer() {
			@Override
			protected List<String> doTokenize(String line) {
				List<String> tokens = super.doTokenize(line);
				while (tokens.size() < columnNames.length) {
					tokens.add(null);
				}
				return tokens;
			}
		};
		tokenizer.setNames(columnNames);

		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new EmployeeMapper(columnNames));

		return lineMapper;
	}

	@Override
	public EmployeeDtoClass read() throws Exception {
		return delegate.read();
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		delegate.close();
	}
}

