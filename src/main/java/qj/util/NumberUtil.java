package qj.util;

import java.text.DecimalFormat;

import qj.util.funct.F1;
import qj.util.funct.Fs;

public class NumberUtil {
	public static float parseFloat(String num, float defau) {
		try {
			return Float.parseFloat(num);
		} catch (Exception e) {
			return defau;
		}		
	}
	
	static F1<String, DecimalFormat> formatF = Fs.cache(new F1<String,DecimalFormat>() {public DecimalFormat e(String format) {
		return new DecimalFormat(format);
	}});

	public static String format(Long l, String format) {
		if (l == null) {
			l = 0L;
		}
		return formatF.e(format).format(l);
	}
	public static String format(Double d, String format) {
		if (d == null) {
			d = 0D;
		}
		return formatF.e(format).format(d);
	}

	public static Integer parseInt(String str) {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
