package com.teamtyphoon.simple_excel_report;

import java.util.List;

public interface QueryDAO {
	List<Object[]> query(String sql, Object... params);
}
