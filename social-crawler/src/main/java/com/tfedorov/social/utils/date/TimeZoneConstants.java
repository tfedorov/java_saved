package com.tfedorov.social.utils.date;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;

public final class TimeZoneConstants {

	private TimeZoneConstants() {
		throw new UnsupportedOperationException();
	}

	public static final String TIME_ZONE_ID = "PST";
	public static final DateTimeZone TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone(TIME_ZONE_ID));

}
