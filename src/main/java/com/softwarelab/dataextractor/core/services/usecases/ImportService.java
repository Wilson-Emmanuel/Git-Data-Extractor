package com.softwarelab.dataextractor.core.services.usecases;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by Wilson
 * on Sat, 04/09/2021.
 */
public interface ImportService {
    void importClassifiedLibrary(XSSFWorkbook workbook);
}
