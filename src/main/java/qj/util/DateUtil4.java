package qj.util;

import static java.util.Calendar.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil4 {
	public static final long SECOND_LENGTH = 1000;
	public static final long MINUTE_LENGTH = 60 * SECOND_LENGTH;
	public static final long HOUR_LENGTH = 60 * MINUTE_LENGTH;
	public static final long DAY_LENGTH = 24 * HOUR_LENGTH;
	public static final long YEAR_LENGTH = 365 * DAY_LENGTH;
	
	public static final String DEFAULT_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	private static final SimpleDateFormat DF_SHORT = new SimpleDateFormat("yy MM dd");

    public static String format(Date time, String dateFormat) {
    	if (time==null) {
    		return "";
    	}
        return new SimpleDateFormat(dateFormat).format(time);
    }

    /**
     * Format the time to dataFormat and timeZone
     * @param time The java Date object to be formatted
     * @param dateFormat The string format to be use to do conversion
     * @param timeZone The timezone for the dateFormat
     * @return The String result of the formatted time.
     */
    public static String format(Date time, String dateFormat, TimeZone timeZone) {
    	SimpleDateFormat df = new SimpleDateFormat(dateFormat);
    	if (timeZone!=null) {
    		df.setTimeZone(timeZone);
    	}
		String date = df.format(time);
    	
		return date;
    }

	/**
	 * Truncate to begin of day: 0 hour 0 minute 0 second 0 millisecond
	 * @param d The date to be truncated
	 * @return the result truncated date
	 */
	public static Date truncate(Date d) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(d);
		truncate(ca);
		return ca.getTime();
	}
	
	/**
	 * Truncate to begin of day: 0 hour 0 minute 0 second 0 millisecond
	 * @param d The date to be truncated
	 * @return the result truncated date
	 */
	public static Date truncateMonth(Date d) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(d);
		truncateMonth(ca);
		return ca.getTime();
	}
	/**
	 * Truncate to begin of day: 0 hour 0 minute 0 second 0 millisecond
	 * @param d The date to be truncated
	 * @return the result truncated date
	 */
	public static Date truncateMonth(Date d, TimeZone timeZone) {
		Calendar ca = Calendar.getInstance(timeZone);
		ca.setTime(d);
		truncateMonth(ca);
		return ca.getTime();
	}
	/**
	 * Truncate to begin of day: 0 hour 0 minute 0 second 0 millisecond
	 * @param d The date to be truncated
	 * @return the result truncated date
	 */
	public static Date truncateYear(Date d, TimeZone timeZone) {
		Calendar ca = Calendar.getInstance(timeZone);
		ca.setTime(d);
		truncateYear(ca);
		return ca.getTime();
	}

	/**
	 * Truncate to begin of day: 0 hour 0 minute 0 second 0 millisecond
	 * @param d The date to be truncated
	 * @return the result truncated date
	 */
	public static Date truncate(Date d, TimeZone zone) {
		Calendar ca = Calendar.getInstance(zone);
		ca.setTime(d);
		truncate(ca);
		return ca.getTime();
	}

	/**
	 * Truncate to begin of day: 0 hour 0 minute 0 second 0 millisecond
	 * @param ca The calendar to be truncated
	 */
	public static Calendar truncate(Calendar ca) {
		truncateHour(ca);
		ca.set(Calendar.HOUR_OF_DAY, 0);
        return ca;
	}
	
	/**
	 * Truncate to begin of month: 0 hour 0 minute 0 second 0 millisecond
	 * @param ca The calendar to be truncated
	 */
	public static Calendar truncateMonth(Calendar ca) {
		ca.set(Calendar.DAY_OF_MONTH, 1);
		truncate(ca);
		return ca;
	}
	
	/**
	 * Truncate to begin of month: 0 hour 0 minute 0 second 0 millisecond
	 * @param ca The calendar to be truncated
	 */
	public static Calendar truncateYear(Calendar ca) {
		ca.set(Calendar.MONTH, 0);
		truncateMonth(ca);
		return ca;
	}

	public static void truncateHour(Calendar ca) {
		ca.set(Calendar.MILLISECOND, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MINUTE, 0);
	}

	public static long truncateTime(Date d) {
		return truncate(d).getTime();
	}

	public static String reformat_force(String str, SimpleDateFormat df) {
		try {
			return df.format(df.parse(str));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Parse to GMT date
	 * @param time
	 * @param df
	 * @return
	 */
	public static Date parse(String time, SimpleDateFormat df) {
		return parse(time, df, null);
	}

	public static Date parse(String time, SimpleDateFormat df, TimeZone timeZone) {

		try {
			Date date = df.parse(time);
			if (timeZone != null) {
				date = fromLocalTZ(date, timeZone);
			}
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Parse to GMT date
	 * @param time
	 * @param df
	 * @return
	 */
	public static Date parse(String time, String df) {
		return parse(time, df, null);
	}
	public static Date parse(String time, String df, TimeZone timeZone) {
		try {
			Date date = new SimpleDateFormat(df).parse(time);
			if (timeZone != null) {
				date = fromLocalTZ(date, timeZone);
			}
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parse(String time, String[] dfs, TimeZone timeZone) {
		for (String df : dfs) {
			Date parse = parse(time, df, timeZone);
			if (parse != null) {
				return parse;
			}
		}
		return null;
	}
	
//	private static final SimpleDateFormat DF3 = new SimpleDateFormat("HH:mm dd/MM/yyyy z");
//	public static void main(String[] args) {
//		System.out.println(parse("10:00 16/05/2007 GMT+07:00", DF3).getTime());
//	}
//
	public static Calendar getCalendar(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return ca;
	}

	public static String getDateStrShort() {
		return getDateStrShort(new Date());
	}
	public static String getDateStrShort(Date date) {
		return DF_SHORT.format(date);
	}
	public static Date parseDateStrShort(String date) {
		return parse(date, DF_SHORT);
	}

	/**
	 * Check if date is between d1 and d2
	 * @param date the target date, not null
	 * @param d1 lower threadhold, if null then no lower threadhold
	 * @param d2 higher threadhold, if null then no higher threadhold
	 * @return true if it is between the two threadhold, false other wise
	 * @throws NullPointerException if date is null
	 */
	public static boolean isBetween(Date date, Date d1, Date d2) {
		long time = date.getTime();
		long time1;
		if (d1 != null) {
			time1 = d1.getTime();
		} else {
			time1 = Long.MIN_VALUE;
		}
		
		long time2;
		if (d2 != null) {
			time2 = d2.getTime();
		} else {
			time2 = Long.MAX_VALUE;
		}
		return time > Math.min(time1, time2) && time < Math.max(time1, time2);
	}

	public static Date today() {
		return truncate(new Date());
	}
	public static Date today(TimeZone timeZone) {
		Date date = new Date();
		return truncate(date, timeZone);
	}

	public static Integer getDay(Date date) {
		if (date == null) {
			return null;
		}
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return Integer.valueOf(ca.get(DAY_OF_MONTH));
	}

	public static Integer getMonth(Date date) {
		if (date == null) {
			return null;
		}
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return Integer.valueOf(ca.get(MONTH) + 1);
	}

	public static int getYear() {
		return getYear(new Date()).intValue();
	}
	
	public static Integer getYear(Date date) {
		if (date == null) {
			return null;
		}
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return Integer.valueOf(ca.get(YEAR));
	}

	public static Date getDate(int day, int month, int year) {
		Calendar ca = Calendar.getInstance();
		truncate(ca);
		ca.set(DAY_OF_MONTH, day);
		ca.set(MONTH, month - 1);
		ca.set(YEAR, year);
		return ca.getTime();
	}

	public static Date getYearBegin(int year) {
		Calendar ca = Calendar.getInstance();
		truncate(ca);
		ca.set(DAY_OF_MONTH, 1);
		ca.set(MONTH, 0);
		ca.set(YEAR, year);
		return ca.getTime();
	}

	public static Date getYearEnd(int year) {
		return new Date(getYearBegin(year + 1).getTime() - 1);
	}

    public static Date tomorrow(int hour, int minute) {
        Calendar ca = truncate(Calendar.getInstance());
        ca.add(DATE, 1);
        ca.set(HOUR_OF_DAY, hour);
        ca.set(MINUTE, minute);
        return ca.getTime();
    }

    public static Date tomorrow(TimeZone zone) {
        Calendar ca = truncate(Calendar.getInstance(zone));
        ca.add(DATE, 1);
        return ca.getTime();
    }

    public static Date next(int hour, int minute) {
    	return next(hour, minute, null);
	}
    
	public static Date next(int hour, int minute, TimeZone timeZone) {
        Calendar ca = truncate(Calendar.getInstance());
        if (timeZone != null) {
        	ca.setTimeZone(timeZone);
        }
        ca.set(HOUR_OF_DAY, hour);
        ca.set(MINUTE, minute);
        if (ca.getTime().before(new Date())) {
        	ca.add(DAY_OF_WEEK, 1);
        }
        return ca.getTime();
	}


    public static boolean isSaturday(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(DAY_OF_WEEK) == SATURDAY;
    }

    public static boolean isSunday(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public static Date nextMonday(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (ca.getTime().before(new Date())) {
        	ca.add(Calendar.DAY_OF_WEEK, 7);
        }
        return ca.getTime();
    }

	public static Date parse(String dateStr) {
		return parse(dateStr, DEFAULT_FORMAT);
	}

	public static Date fromLocalTZ(Date time, TimeZone localTimeZone) {
		return new Date(time.getTime() - (localTimeZone.getRawOffset() - TimeZone.getDefault().getRawOffset()) );
	}
	
	/**
	 * 
	 * @param time
	 * @param localTimeZone
	 * @return
	 */
	public static Date toLocalTZ(Date time, TimeZone localTimeZone) {
		if (localTimeZone==null) {
			return time;
		}
		return new Date(time.getTime() + (localTimeZone.getRawOffset() - TimeZone.getDefault().getRawOffset()) );
	}

}
