package com.teamtyphoon.simple_excel_report;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;

import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class QueryService {
	private QueryJDBCDAO dao = new QueryJDBCDAO("jdbc:hsqldb:hsql://localhost/xdb", "SA", "");

	public List<Object[]> queryForList(String sql, Object... params) {
		List<Object[]> query = dao.query(sql, params);
		return query;
	}

	public void queryForExcel(String excelName, String sql, Object... params) {
		List<Object[]> query = queryForList(sql, params);
		WritableWorkbook book = null;
		try {
			book = Workbook.createWorkbook(new File(excelName + ".xls"));
			WritableSheet sheet = book.createSheet("Report", 0);
			int j = 0;
			for (Object[] row : query) {
				for (int i = 0; i < row.length; i++) {
					if (row[i] instanceof Integer) {
						jxl.write.Number number = new jxl.write.Number(i, j,
								new Double(((Integer) row[i]).doubleValue()));
						sheet.addCell(number);
					} else if (row[i] instanceof java.sql.Date) {
						DateTime label = new DateTime(i, j, new Date(((java.sql.Date) row[i]).getTime()));
						sheet.addCell(label);
					} else {
						Label label = new Label(i, j, (String) row[i]);
						sheet.addCell(label);
					}
				}
				j++;
			}

			book.write();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (book != null) {
				try {
					book.close();
				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static void main(String[] args) {
//		DOMConfigurator.configure("/home/eric/workspace/java/simple-excel-report/src/config/log4j2.xml");
		QueryService qs = new QueryService();
		String sql = "SELECT id 序号,nm 名字,test_date 时间 FROM Test";
		qs.queryForExcel("ggg", sql, null);
	}
}
