package com.demo.config;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.demo.dto.EmployeeDto;
import com.demo.dto.EmployeeDtoClass;

import java.util.HashMap;
import java.util.Map;

public class EmployeeDtoRowMapper implements RowMapper<EmployeeDtoClass> {

    private final String[] columnNames;
    private final Map<String, Integer> columnIndexMap;

    public EmployeeDtoRowMapper(String[] columnNames) {
        this.columnNames = columnNames;
        this.columnIndexMap = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            columnIndexMap.put(columnNames[i], i);
        }
    }

    @Override
    public EmployeeDtoClass mapRow(RowSet rs) throws Exception {
        String employeeId = null;
        String fullname = null;
        Integer number = null;
        String company = null;

        String[] currentRow = rs.getCurrentRow();

        for (String columnName : columnNames) {
            Integer index = columnIndexMap.get(columnName);
            if (index != null && index < currentRow.length) {
                String cellValue = currentRow[index];
                if (cellValue != null) {
                    switch (columnName) {
                        case "employeeId":
                            employeeId = cellValue;
                            break;
                        case "fullname":
                            fullname = cellValue;
                            break;
                        case "number":
                            number = Integer.parseInt(cellValue);
                            break;
                        case "company":
                            company = cellValue;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        return new EmployeeDtoClass(employeeId, fullname, number, company, false);
    }
}
