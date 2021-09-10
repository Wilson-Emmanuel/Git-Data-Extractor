package com.softwarelab.dataextractor.core.services.usecases;

import javafx.beans.property.DoubleProperty;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
public interface ExportService {
    void exportProjectLibrary(XSSFWorkbook workbook, Long projectId, String projectName);
    void exportAllLibraries(XSSFWorkbook workbook );
    void exportProjectUniqueLibraries(XSSFWorkbook workbook, Long projectId, String projectName);
    void exportProjectUnclassifiedUniqueLibraries(XSSFWorkbook workbook, Long projectId, String projectName);
    void exportAllUniqueLibraries(XSSFWorkbook workbook);
    void exportUniqueUnclassifiedLibraries(XSSFWorkbook workbook);
}
