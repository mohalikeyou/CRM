package com.bjpowernode.crm.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtils {
    // 根据单元格，获得单元格的值
    public static String getCellValue(HSSFCell cell) {
        CellType cellType = cell.getCellType();
        if (cellType == CellType.NUMERIC) {
            return cell.getNumericCellValue() + "";
        } else if (cellType == CellType.STRING) {
            return cell.getStringCellValue() + "";
        } else if (cellType == CellType.FORMULA) {
            return cell.getCellFormula() + "";
        } else if (cellType == CellType.BOOLEAN) {
            return cell.getBooleanCellValue() + "";
        } else {
            return "";
        }
    }
}
