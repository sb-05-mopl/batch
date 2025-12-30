package com.mopl.mopl_batch.batch.batch.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatUtil {

	private static final DateTimeFormatter DATE_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static String toString(LocalDate date) {
		if (date == null) {
			return null;
		}
		return date.format(DATE_FORMATTER);
	}
}
