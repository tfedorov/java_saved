/**
 * 
 */
package com.tfedorov.social.utils.date;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

/**
 * @author tfedorov
 * 
 */
public class DateUtilsTest {

  private static final long STANDART_TIME_LONG = 517890800000L;

  @Test
  public void testPrintDate() {
    DateMidnight dateIn1970 = new DateMidnight(0,  DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
    Assert.assertEquals("1970-01-01 00:00:00", DateUtils.printDate(dateIn1970));
    
    DateMidnight dateIn1970PST = new DateMidnight(0,  TimeZoneConstants.TIME_ZONE);
    Assert.assertEquals("1969-12-31 00:00:00", DateUtils.printDate(dateIn1970PST));

    DateMidnight dateMidnight = new DateMidnight(1366120754638L);
    Assert.assertEquals("2013-04-16 00:00:00", DateUtils.printDate(dateMidnight));

    DateTime dateFull = new DateTime(1366120754638L,TimeZoneConstants.TIME_ZONE);
    Assert.assertEquals("2013-04-16 06:59:14", DateUtils.printDate(dateFull));

    DateTime dateBrth = new DateTime(STANDART_TIME_LONG,TimeZoneConstants.TIME_ZONE);
    Assert.assertEquals("1986-05-30 19:33:20", DateUtils.printDate(dateBrth));
  }

  @Test
  public void testPrintDateTZ() {
    DateMidnight dateIn1970 = new DateMidnight(0L, DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
    Assert.assertEquals("1970-01-01 00:00:00 UTC", DateUtils.printDateTZ(dateIn1970));

    DateMidnight dateMidnight = new DateMidnight(1366120754638L, TimeZoneConstants.TIME_ZONE);
    Assert.assertEquals("2013-04-16 00:00:00 America/Los_Angeles", DateUtils.printDateTZ(dateMidnight));

    DateTime dateFull = new DateTime(1366120754638L, TimeZoneConstants.TIME_ZONE);
    Assert.assertEquals("2013-04-16 06:59:14 America/Los_Angeles", DateUtils.printDateTZ(dateFull));

    DateTime dateBrth = new DateTime(517890800000L, TimeZoneConstants.TIME_ZONE);
    Assert.assertEquals("1986-05-30 19:33:20 America/Los_Angeles", DateUtils.printDateTZ(dateBrth));
  }

  @Test
  public void testGetIntervalToToday() {

    DateTime toDate = new DateTime(TimeZoneConstants.TIME_ZONE).withTime(23, 59, 59, 0);

    // Check one day for getIntervalToToday function
    DateMidnight yesterdayMidnight = dateMinuNumDay(1);
    Interval interval1Day = DateUtils.getIntervalToToday(2);
    Assert.assertEquals(yesterdayMidnight, interval1Day.getStart());
    Assert.assertEquals(toDate, interval1Day.getEnd());

    // Check two day for getIntervalToToday function
    DateMidnight twoDayAgoMidnight = dateMinuNumDay(2);
    Interval interval2Today = DateUtils.getIntervalToToday(3);
    Assert.assertEquals(twoDayAgoMidnight, interval2Today.getStart());
    Assert.assertEquals(toDate, interval2Today.getEnd());

    // Check week for getIntervalToToday function
    DateMidnight weekAgoMidnight = dateMinuNumDay(7);
    Interval intervalWeekToToday = DateUtils.getIntervalToToday(8);
    Assert.assertEquals(weekAgoMidnight, intervalWeekToToday.getStart());
    Assert.assertEquals(toDate, intervalWeekToToday.getEnd());

    // Check 0 day for getIntervalToToday function
    DateMidnight todayMidnight = DateUtils.getCurrentMidnight();
    Interval intervalToToday = DateUtils.getIntervalToToday(0);
    Assert.assertEquals(todayMidnight, intervalToToday.getStart());
    Assert.assertEquals(toDate, intervalToToday.getEnd());

  }

  @Test
  public void testGetIntervalToTodayDateTime() {
    DateTime dateBrth = new DateTime(STANDART_TIME_LONG,TimeZoneConstants.TIME_ZONE);
    Interval resultInterval = DateUtils.getIntervalToToday(dateBrth,2);
    
    DateTime expectedEnd = DateUtils.getCurrentMidnight().plusDays(1).toDateTime().minus(1000);
    
    Assert.assertEquals(517734000000L, resultInterval.getStartMillis());
    Assert.assertEquals("1986-05-29 00:00:00 America/Los_Angeles", DateUtils.printDateTZ(resultInterval.getStart()));
    Assert.assertEquals(DateUtils.printDateTZ(expectedEnd), DateUtils.printDateTZ(resultInterval.getEnd()));
    Assert.assertEquals(expectedEnd, resultInterval.getEnd());
    
    resultInterval = DateUtils.getIntervalToToday(dateBrth,3);
    Assert.assertEquals(517647600000L, resultInterval.getStartMillis());
    Assert.assertEquals(expectedEnd.getMillis(), resultInterval.getEndMillis());
    
    resultInterval = DateUtils.getIntervalToToday(dateBrth,0);
    Assert.assertEquals(517820400000L, resultInterval.getStartMillis());
    Assert.assertEquals(expectedEnd.getMillis(), resultInterval.getEndMillis());
    
  }
  
  private DateMidnight dateMinuNumDay(int dayNumb) {
    DateTime now = new DateTime(TimeZoneConstants.TIME_ZONE);
    DateMidnight fromDate = new DateMidnight(now.minusDays(dayNumb));
    return fromDate;
  }


  @Test
  public void testGetMidnightDaysOr30Ago() {
    DateMidnight expectedMidnight = new DateMidnight(TimeZoneConstants.TIME_ZONE);
    DateMidnight resultMidnight = DateUtils.getMidnightDaysOr30Ago(2);
    
    Assert.assertEquals(expectedMidnight.minusDays(2), resultMidnight);
    
    resultMidnight = DateUtils.getMidnightDaysOr30Ago(3);
    Assert.assertEquals(expectedMidnight.minusDays(3), resultMidnight);
    
    resultMidnight = DateUtils.getMidnightDaysOr30Ago(0);
    Assert.assertEquals(expectedMidnight.minusDays(30), resultMidnight);
  }

  @Test
  public void testConvertToDateTimeTimestamp() {
    Timestamp convertToTimeStamp = new Timestamp(1366182000001L);
    DateTime actualDateTime = DateUtils.convertToDateTime(convertToTimeStamp);
    Assert.assertEquals(convertToTimeStamp.getTime(), actualDateTime.getMillis());
    Assert.assertEquals(TimeZoneConstants.TIME_ZONE,actualDateTime.getZone());
    
    DateTime expectedDateTime = new DateTime(1366182000001L,TimeZoneConstants.TIME_ZONE);
    Assert.assertEquals(expectedDateTime, actualDateTime);
    
  }

  @Test
  public void testConvertToDateTimeDate() {
    Date curentDate = new Date();
    DateTime actualDateTime = DateUtils.convertToDateTime(curentDate);
    Assert.assertEquals(curentDate.getTime(), actualDateTime.getMillis());
    Assert.assertEquals(TimeZoneConstants.TIME_ZONE, actualDateTime.getZone());
  }
  

  @Test
  public void testConvertToDateTimeDateMidnight() {
    Date curentDate = new Date();
    DateMidnight actualDateMidnight = DateUtils.convertToDateMidnight(curentDate);
    Assert.assertTrue(curentDate.getTime() > actualDateMidnight.getMillis());
    Assert.assertEquals(TimeZoneConstants.TIME_ZONE, actualDateMidnight.getZone());

    Date concreteDate = new Date(1366197393744L);
    actualDateMidnight = DateUtils.convertToDateMidnight(concreteDate);
    Assert.assertTrue(concreteDate.getTime() > actualDateMidnight.getMillis());
    Assert.assertEquals(1366182000000L, actualDateMidnight.getMillis());
    Assert.assertEquals(TimeZoneConstants.TIME_ZONE, actualDateMidnight.getZone());
  }

  @Test
  public void testConvertToTimeStamp() {
    DateMidnight expectedMidnightDate = new DateMidnight(0L, DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
    Timestamp convertToTimeStamp = DateUtils.convertToTimeStamp(expectedMidnightDate);
    Assert.assertEquals(expectedMidnightDate.getMillis(), convertToTimeStamp.getTime());
    
    DateTime expectedDateTime = new DateTime(0L, DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
    convertToTimeStamp = DateUtils.convertToTimeStamp(expectedDateTime);
    Assert.assertEquals(expectedDateTime.getMillis(), convertToTimeStamp.getTime());
    
    expectedMidnightDate = new DateMidnight(1366182111111L,TimeZoneConstants.TIME_ZONE);
    convertToTimeStamp = DateUtils.convertToTimeStamp(expectedMidnightDate);
    Assert.assertEquals(expectedMidnightDate.getMillis(), convertToTimeStamp.getTime());
 
    expectedDateTime = new DateTime(1366182000001L,TimeZoneConstants.TIME_ZONE);
    convertToTimeStamp = DateUtils.convertToTimeStamp(expectedDateTime);
    Assert.assertEquals(expectedDateTime.getMillis(), convertToTimeStamp.getTime());
    
  }

  @Test
  public void testGetCurrentDateTime() {
    DateTime expectedCurentDate = new DateTime(TimeZoneConstants.TIME_ZONE);
    DateTime actualCurrentDate = DateUtils.getCurrentDateTime();
    Assert.assertTrue(actualCurrentDate.getMillis() == expectedCurentDate.getMillis());
    Assert.assertEquals(DateUtils.printDateTZ(expectedCurentDate), DateUtils.printDateTZ(actualCurrentDate));
  }  
  
  @Test
  public void testParse() {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(TimeZoneConstants.TIME_ZONE);
    DateMidnight dateMidnightExpected = new DateTime(formatter.parseDateTime("2013-02-19"), TimeZoneConstants.TIME_ZONE).toDateMidnight();;

    DateMidnight dateMidnightActual = DateUtils.parseToMidnightDefTZ("2013-02-19", DateUtils.YYYY_MM_DD_FORMAT);
    Assert.assertEquals(dateMidnightExpected, dateMidnightActual);
    Assert.assertEquals(dateMidnightExpected, dateMidnightActual);
    Assert.assertEquals("2013-02-19 00:00:00 America/Los_Angeles", DateUtils.printDateTZ(dateMidnightActual));
    
  }
}
