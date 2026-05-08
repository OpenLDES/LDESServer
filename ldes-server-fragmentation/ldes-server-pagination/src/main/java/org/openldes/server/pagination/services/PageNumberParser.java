package org.openldes.server.pagination.services;

import static org.openldes.server.pagination.valueobjects.pagenumber.PageNumber.PAGE_NUMBER_KEY;

import org.apache.commons.lang3.math.NumberUtils;
import org.openldes.server.pagination.valueobjects.pagenumber.NumericPageNumber;
import org.openldes.server.pagination.valueobjects.pagenumber.PageNumber;
import org.openldes.server.pagination.valueobjects.pagenumber.UuidPageNumber;

public class PageNumberParser {
	public static PageNumber parse(String pageNumberString) {
		if (!pageNumberString.contains(PAGE_NUMBER_KEY)) {
			throw new IllegalArgumentException("Invalid page number: %s - Expected format: %s=<INTEGER/UUID>".formatted(pageNumberString, PAGE_NUMBER_KEY));
		}
		final String value = pageNumberString.replace(PAGE_NUMBER_KEY + "=", "");
		if(NumberUtils.isCreatable(value)) {
			return new NumericPageNumber(Integer.parseInt(value));
		}
		return UuidPageNumber.fromString(value);
	}

	private PageNumberParser() {}
}
