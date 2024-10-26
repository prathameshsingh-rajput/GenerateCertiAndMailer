package com.generatecertiandmailer;

import com.generatecertiandmailer.models.UserInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public List<UserInfo> readExcel(String filePath) {
        List<UserInfo> userInfoList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String certId = getCellValueAsString(row.getCell(0));
                String name = getCellValueAsString(row.getCell(1)).toUpperCase();
                String grade = getCellValueAsString(row.getCell(2));
                String emailId = getCellValueAsString(row.getCell(3));

                if (!emailId.isEmpty() && !name.isEmpty()) {
                    UserInfo userInfo = new UserInfo(emailId, name, grade, certId);
                    userInfoList.add(userInfo);
                } else {
                    System.err.println("Skipping row " + row.getRowNum() + " due to missing required fields.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
        }
        return userInfoList;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
