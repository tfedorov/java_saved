package com.tfedorov.social.utils.date;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author tfedorov
 * 
 */
public final class DateUtils {

  private DateUtils() {}

  public static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_TIME_FORMAT_TIME_ZONE = "yyyy-MM-dd HH:mm:ss ZZZZ";
  private static final String DATE_TIME_H_M_S = "HH:mm:ss:SS";

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat
      .forPattern(DATE_TIME_FORMAT);
  private static final DateTimeFormatter DATE_TIME_FORMATTER_TZ = DateTimeFormat
      .forPattern(DATE_TIME_FORMAT_TIME_ZONE);

  private static final DateTimeFormatter DATE_TIME_FORMATTER_HMS = DateTimeFormat
      .forPattern(DATE_TIME_H_M_S);


  /**
   * Return interval which contain two date beginning of day @param days age and ending of current
   * day
   * 
   * @param days number of days before to day for "From" dateteim
   * @return jodatime interval from (curent day - @param days ) to current day
   */
  public static Interval getIntervalToToday(int days) {

    DateTime from = new DateTime(TimeZoneConstants.TIME_ZONE);
    if (days > 0) {
      from = from.minusDays(days - 1);
    }

    DateTime to = new DateTime(TimeZoneConstants.TIME_ZONE).withTime(23, 59, 59, 0);

    return new Interval(new DateMidnight(from), to);

  }

  /**
   * Return interval which contain two date beginning of day @param days age and ending of current
   * day
   * 
   * @param days number of days before to day for "From" dateteim
   * @return jodatime interval from (curent day - @param days ) to current day
   */
  public static Interval getIntervalToToday(DateTime date, int days) {

    DateTime from = new DateTime(date, TimeZoneConstants.TIME_ZONE);
    if (days > 0) {
      from = from.minusDays(days - 1);
    }

    DateTime to = new DateTime(TimeZoneConstants.TIME_ZONE).withTime(23, 59, 59, 0);

    return new Interval(new DateMidnight(from), to);
  }

  public static DateMidnight getMidnightDaysOr30Ago(int daysRange) {
    DateMidnight startDateTime = new DateMidnight(TimeZoneConstants.TIME_ZONE);
    if (daysRange > 0) {
      startDateTime = startDateTime.minusDays(daysRange);
    } else {
      startDateTime = startDateTime.minusDays(30);
    }
    return startDateTime;
  }

  /**
   * Return string from class "org.joda.time.base.BaseDateTime" (super class for
   * "org.joda.time.DateTime" and "org.joda.time.DateMidnight" classes) according to default app
   * formatter
   * 
   * @param date org.joda.time.base.BaseDateTime
   * @return
   */
  public static String printDate(BaseDateTime date) {
    return DATE_TIME_FORMATTER.print(date);
  }

  public static String printDateTZ(BaseDateTime date) {
    return DATE_TIME_FORMATTER_TZ.print(date);
  }

  public static String printDateMiliSecond(long timeInMilisec) {
    return DATE_TIME_FORMATTER_HMS.print(new DateTime(timeInMilisec, TimeZoneConstants.TIME_ZONE));
  }

  public static DateTime convertToDateTime(Timestamp timestamp) {
    return new DateTime(timestamp, TimeZoneConstants.TIME_ZONE);
  }

  public static DateTime convertToDateTime(Date date) {
    return new DateTime(date, TimeZoneConstants.TIME_ZONE);
  }

  public static DateMidnight convertToDateMidnight(Date utilDate) {
    return new DateMidnight(utilDate, TimeZoneConstants.TIME_ZONE);
  }

  public static Timestamp convertToTimeStamp(BaseDateTime jodaDate) {
    return new Timestamp(jodaDate.getMillis());
  }

  public static DateMidnight getCurrentMidnight() {
    return new DateMidnight(TimeZoneConstants.TIME_ZONE);
  }

  public static DateTime getCurrentDateTime() {
    return new DateTime(TimeZoneConstants.TIME_ZONE);
  }

  public static DateMidnight parseToMidnightDefTZ(String dateString, String formatPattern) {
    DateTimeFormatter formatter =
        DateTimeFormat.forPattern(formatPattern).withZone(TimeZoneConstants.TIME_ZONE);
    return new DateTime(formatter.parseDateTime(dateString), TimeZoneConstants.TIME_ZONE)
        .toDateMidnight();

  }

}
