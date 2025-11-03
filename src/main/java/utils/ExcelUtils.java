package utils;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    private static final String RESOURCE_PATH = "src/test/resources/";
    private static final String FILE_NAME = "SwagLabs credentials.xlsx";
    private static final String SHEET_NAME = "Credentials";

    private static String getCellStringValue(Cell cell) 
    {
        if (cell == null) return "";
        switch (cell.getCellType()) 
        {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                DataFormatter formatter = new DataFormatter();
                return formatter.formatCellValue(cell).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                return cell.toString().trim(); 
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    public static Object[][] getTestDataAsDataProvider() 
    {
        String fullPath = RESOURCE_PATH + FILE_NAME;
        Object[][] data = null;

        try (FileInputStream fis = new FileInputStream(fullPath);
            Workbook workbook = new XSSFWorkbook(fis)) 
            {
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) 
            {
                sheet = workbook.getSheetAt(0);
                System.err.println("Warning: Default sheet 'Credentials' not found. Using the first sheet in the workbook.");
                if (sheet == null) 
                {
                    throw new RuntimeException("No sheets found in Excel file: " + fullPath);
                }
            }

            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount <= 1) 
            {
                System.out.println("No data rows found in Excel file after header.");
                return new Object[0][0];
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) 
            {
                System.out.println("Cannot determine column count as header row is empty.");
                return new Object[0][0];
            }
            int colCount = headerRow.getPhysicalNumberOfCells();

            data = new Object[rowCount - 1][colCount];

            for (int i = 1; i < rowCount; i++) 
            {
                Row row = sheet.getRow(i);
                if (row == null) continue; 

                for (int j = 0; j < colCount; j++) 
                {
                    Cell cell = row.getCell(j);
                    data[i - 1][j] = getCellStringValue(cell); 
                }
            }
            
            System.out.println("Successfully loaded " + data.length + " data rows from " + fullPath);

        } 
        catch (IOException e) 
        {
            System.err.println("CRITICAL ERROR: Could not read Excel file at: " + fullPath);
            e.printStackTrace();
            throw new RuntimeException("Data source error: " + e.getMessage(), e);
        }
        return data;
    }


   
}

