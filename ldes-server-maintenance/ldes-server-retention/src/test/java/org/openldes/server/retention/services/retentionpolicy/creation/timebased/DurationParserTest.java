package org.openldes.server.retention.services.retentionpolicy.creation.timebased;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Period;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DurationParserTest {

	@Test
	void when_StringIsDuration() {
		Duration duration = Duration.ofMinutes(4000);
		String durationString = duration.toString();
		Duration toTest = DurationParser.parseText(durationString);
		Assertions.assertEquals(duration, toTest);

	}

	@Test
	void when_StringIsPeriod() {
		Duration duration = Duration.ofDays(30);
		Period period = Period.ofMonths(1);
		String periodString = period.toString();
		Duration toTest = DurationParser.parseText(periodString);
		Assertions.assertEquals(duration, toTest);
	}

	@Test()
	void when_StringIsInvalid() {
		String invalidString = "PT5F9";
		assertThrows(DurationParserException.class, () -> DurationParser.parseText(invalidString));
	}
}
