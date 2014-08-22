package qj.tool.scheduler;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;

import qj.util.PropertiesUtil;
import qj.util.RegexUtil;
import qj.util.StringUtil;
import qj.util.bean.AValue;
import qj.util.funct.P0;

public class Scheduler {
	private String pattern;
	private AValue<String> persist;
	private P0 action;

	public P0 every5m() {
		Matcher m;
		if ((m=RegexUtil.matcher("(\\d\\d):(\\d\\d) ([\\w/]+) every day", pattern)).matches()) {
			final int hour = Integer.parseInt(m.group(1));
			final int minute = Integer.parseInt(m.group(2));
			final TimeZone tz = TimeZone.getTimeZone(m.group(3));
			return new P0() {public void e() {
				Calendar ca = Calendar.getInstance(tz);
				int nowHour = ca.get(Calendar.HOUR_OF_DAY);
				int nowMinute = ca.get(Calendar.MINUTE);
				
				
				if (nowHour == hour && nowMinute >= minute && nowMinute <= minute + 8) {
					DecimalFormat nf = new DecimalFormat("00");
					String today = nf.format(ca.get(Calendar.DAY_OF_MONTH)) + "/" + nf.format((ca.get(Calendar.MONTH) + 1));
					String lastRun = persist.get();
					if (StringUtil.isEmpty(lastRun)
							 || !lastRun.equals(today)
							) {
						action.e();
						persist.set(today);
					}
				}
			}};
		}
		throw new RuntimeException();
	}
	
	public Scheduler(String pattern, AValue<String> persist, P0 action) {
		this.pattern = pattern;
		this.persist = persist;
		this.action = action;
	}

	public static void main(String[] args) {
//		System.out.println(TimeZone.getDefault());
//		System.out.println(TimeZone.getTimeZone("Asia/Bangkok"));
//		System.out.println(Arrays.toString(TimeZone.getAvailableIDs()));
		AValue<String> persist = PropertiesUtil.persistValue("last_run", new File("temp.properties"));
		Scheduler scheduler = new Scheduler("11:38 Asia/Bangkok every day", persist, new P0() {public void e() {
			System.out.println("Running");
		}});
		
		P0 every5m = scheduler.every5m();
		every5m.e();
		every5m.e();
		
	}
}
