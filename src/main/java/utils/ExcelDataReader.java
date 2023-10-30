package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelDataReader {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        String excelFilePath = "C:\\Users\\rizas\\Downloads\\LoginData2.xlsx"; 
        FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0); 

        int rowCount = sheet.getLastRowNum();
        int colCount = 3; 

        
        Object[][] data = new Object[rowCount][colCount]; // Created a 2D object array with rowCount rows

        for (int i = 1; i <= rowCount; i++) { 
            Row row = sheet.getRow(i);

            String username = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();
            String expectedResult = row.getCell(2).getStringCellValue();

            data[i - 1][0] = username;
            data[i - 1][1] = password;
            data[i - 1][2] = expectedResult;
        }

        workbook.close();
        fileInputStream.close();
        return data;
    }
}