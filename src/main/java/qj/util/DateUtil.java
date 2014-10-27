package qj.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.util.funct.P1;

public class DateUtil extends DateUtil4 {

    static List<String> DATE_FORMATS = Arrays.asList(
            "yyyy/MM/dd",
            "MM/dd/yyyy"
    );
    static List<String> TIME_FORMATS = Arrays.asList(
            "HH:mm:ss"
    );
    public static String guessFormat(String str) {
        for (String dateFormat : DATE_FORMATS) {
            Matcher m = Pattern.compile(toRegex(dateFormat)).matcher(str);
            if (m.find() && parse(m.group(), dateFormat) != null) {
                // Found
                return dateFormat;
            }
        }
        return null;
    }

    public static String toRegex(String format) {
        return RegexUtil.replaceAll2(format,
                "yyyy", "\\\\d{4}",
                "MM", "\\\\d{2}",
                "dd", "\\\\d{2}",
                "HH", "\\\\d{2}",
                "hh", "\\\\d{2}",
                "mm", "\\\\d{2}",
                "ss", "\\\\d{2}",
                "SSS", "\\\\d{3}",
                "EEE", "\\\\w{3}"
        );
    }

    /**
     * To fix default parsing of SimpleDateFormat, which default adjusting 
     * dates to be within 80 years before and 20 years after the current time.
     * This default parsing prioritize past time (80 years backward), but what
     * we want is prioritizing future time.
     * <p>
     * For example, if now is 2000 and the string to parse is 010150 (format: ddMMyy).
     * The default parsing will result in year 1950. This parsing method will result
     * in year 2050
     * @param dateStr
     * @param dateFormat
     * @return
     */
    public static Date parseFutureDate(String dateStr, SimpleDateFormat dateFormat) {
        Date date = parse(dateStr, dateFormat);

        if (date != null && date.getTime() < System.currentTimeMillis() - 20 * YEAR_LENGTH) {
            date = addYears(date, 100);
        }
        return date;
    }
    
	public static Date useCurrentYear(Date date) {
		Calendar ca = getCalendar(date);
		ca.set(Calendar.YEAR, getYear(new Date()));
		return ca.getTime();
	}

	public static Date addDays(Date date, int days) {
		Calendar calendar = getCalendar(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}
	public static Date addMonths(Date date, int months) {
		Calendar calendar = getCalendar(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}
	public static Date addMonths(Date date, int months, TimeZone timeZone) {
		Calendar calendar = getCalendar(date, timeZone);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}
	public static Date addYears(Date date, int years) {
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }
	public static Date addHours(Date date, int days) {
		Calendar calendar = getCalendar(date);
		calendar.add(Calendar.HOUR_OF_DAY, days);
		return calendar.getTime();
	}

	public static Date getWeekDay(String strWeekDay) {
		int weekDay = (int) (parse(strWeekDay, new SimpleDateFormat("E")).getTime()/DAY_LENGTH - 1);
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -7);
		ca.set(Calendar.DAY_OF_WEEK, weekDay);
		return ca.getTime();
	}
    
	public static List<String> getDays(Date from, Date to, SimpleDateFormat format) {
		String target = format.format(to);
		LinkedList<String> ret = new LinkedList<String>();
		for (long time = from.getTime();; time+=DAY_LENGTH) {
			String str = format.format(new Date(time));
			ret.add(str);
			
			if (str.equals(target)) {
				break;
			}
		}
		return ret;
	}
	
	public static boolean isToday(Date time, TimeZone timeZone) {
		return compareDay(0, time, timeZone) == 0;
	}
	public static boolean isYesterday(Date time, TimeZone timeZone) {
		return compareDay(-1, time, timeZone) == 0;
	}

	public static long compareDay(int day, Date time, TimeZone timeZone) {
		Calendar ca = Calendar.getInstance(timeZone);
		ca.setTime(time);
		ca.add(Calendar.DATE, -1 * day);
		DateUtil.truncate(ca);
		return ca.getTime().getTime() - DateUtil.today(timeZone).getTime();
	}
	
	public static double millisToDays(long millis) {
		return millis/DAY_LENGTH;
	}

	public static Double minutesToDays(int minutes) {
		return  ((double)minutes) / DAY_LENGTH * MINUTE_LENGTH;
	}

	public static SimpleDateFormat dateFormat(String df, TimeZone timeZone) {
		SimpleDateFormat ret = new SimpleDateFormat(df);
		ret.setTimeZone(timeZone);
		return ret;
	}

	public static Date dayEnd(Date dateStart) {
		return new Date(DateUtil.addDays(dateStart, 1).getTime() - 1);
	}

	/**
	 * Only work with month start
	 */
	public static Date monthEnd(Date monthStart) {
		return new Date(DateUtil.addMonths(monthStart, 1).getTime() - 1);
	}
	public static Date monthEnd(Date monthStart, TimeZone timeZone) {
		return new Date(DateUtil.addMonths(monthStart, 1, timeZone).getTime() - 1);
	}
	public static Date weekStart(Date date, TimeZone timeZone) {
		Calendar ca = timeZone== null ?  Calendar.getInstance() : Calendar.getInstance(timeZone);
		ca.setTime(date);
		truncate(ca);
		ca.add(Calendar.DAY_OF_WEEK, -ca.get(Calendar.DAY_OF_WEEK) + 1);
		return ca.getTime();
	}
	
	public static void eachDay(Date from, Date to, P1<Date> dateF) {
		if (to == null) {
			to = new Date();
		}
		for (Date d = from;d.compareTo(to) < 0; d = addDays(d, 1)) {
			dateF.e(d);
		}
	}

}
