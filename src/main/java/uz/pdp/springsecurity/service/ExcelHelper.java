package uz.pdp.springsecurity.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.springsecurity.payload.ProductViewDto;
import uz.pdp.springsecurity.payload.ProductViewDtos;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static String SHEET = "Tutorials";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<ProductViewDtos> excelToTutorials(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<ProductViewDtos> productViewDtosList = new ArrayList<ProductViewDtos>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                ProductViewDtos productViewDtos = new ProductViewDtos();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            productViewDtos.setProductName(currentCell.getStringCellValue());
                            break;
                        case 1:
                            productViewDtos.setBranch(currentCell.getStringCellValue());
                            break;
                        case 2:
                            productViewDtos.setBuyPrice(currentCell.getNumericCellValue());
                            break;
                        case 3:
                            productViewDtos.setSalePrice(currentCell.getNumericCellValue());
                            break;
                        case 4:
                            productViewDtos.setMeasurementId(currentCell.getStringCellValue());
                            break;
                        case 5:
                            productViewDtos.setBarcode(currentCell.getStringCellValue());
                            break;
                        case 6:
                            productViewDtos.setAmount(currentCell.getNumericCellValue());
                            break;
                        case 7:
                            productViewDtos.setBrandName(currentCell.getStringCellValue());
                            break;
                        case 8:
                            productViewDtos.setMinQuantity(currentCell.getNumericCellValue());
                            break;
                        case 9:
                            productViewDtos.setExpiredDate(currentCell.getStringCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }

                productViewDtosList.add(productViewDtos);
            }

            workbook.close();

            return productViewDtosList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
