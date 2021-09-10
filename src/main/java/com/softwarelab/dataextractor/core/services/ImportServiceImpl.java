package com.softwarelab.dataextractor.core.services;

import com.softwarelab.dataextractor.core.services.usecases.ImportService;
import com.softwarelab.dataextractor.core.services.usecases.LibraryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * Created by Wilson
 * on Sat, 04/09/2021.
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportServiceImpl implements ImportService {

    LibraryService libraryService;

    @Override
    public void importClassifiedLibrary(XSSFWorkbook workbook) {

        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        boolean skippedFirstRow = false;
        //Iterate through each rows one by one
        for (Row row : sheet) {
            //skip first row bcos it contains the headers
            if(!skippedFirstRow){
                skippedFirstRow = true;
                continue;
            }

            //For each row, iterate through all the columns
            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {
                //column 1
                String library = cellIterator.next().getStringCellValue();
                //column 2
                String provider = cellIterator.next().getStringCellValue();
                //column 3
                String category = cellIterator.next().getStringCellValue();

                libraryService.updateLibrary(library,provider,category);
            }
        }
    }

}
