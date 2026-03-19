package org.openldes.server.pagination.valueobjects.pagenumber;

public interface PageNumber {
	String PAGE_NUMBER_KEY = "pageNumber";

	String asString();

	PageNumber getNextPageNumber();
}
