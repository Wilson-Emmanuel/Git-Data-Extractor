package com.softwarelab.dataextractor.core.utilities;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;

/**
 * Created by Wilson
 * on Tue, 08/06/2021.
 */
public class GeneralUtil {
    public static String getProgramPath(){
        File defaultLoc  = new File(System.getProperty("user.home"),"Data_Extractor");
        boolean created = defaultLoc.exists();
        if(!created){
            created = defaultLoc.mkdir();
        }
        return created?defaultLoc.getPath():System.getProperty("user.home");
    }

    public static String getProjectLocation(ActionEvent actionEvent, String title){
        Window window = ((Node)actionEvent.getSource()).getScene().getWindow();
        DirectoryChooser directoryChooser  = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(GeneralUtil.getProgramPath()));
        directoryChooser.setTitle(title);
        File directoryLocation = directoryChooser.showDialog(window);
        if(directoryLocation == null)return "";
        return directoryLocation.getAbsolutePath();
    }

    private static CellStyle setHeaderFont(Workbook workbook){
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }
    public static XSSFSheet setByLibHeaderRow(XSSFWorkbook workbook, String sheetName, String[] headers){
        CellStyle headerCellStyle = setHeaderFont(workbook);
        XSSFSheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }
        return sheet;
    }
}
