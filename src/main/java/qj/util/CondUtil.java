package qj.util;

import qj.util.funct.F0;
import qj.util.funct.P0;

public class CondUtil {

	public static boolean attempt(F0<Boolean> testF, P0 resetF, F0<Boolean>... corrections) {
		if (testF.e()) {
			return true;
		}
		
		for (F0<Boolean> correctF : corrections) {
			if (!correctF.e()) {
				continue;
			}
			if (testF.e()) {
				return true;
			}
			resetF.e();
		}
		return false;
	}
	
	public static boolean attempt(F0<Boolean> testF, F0<Boolean>... attempts) {
		for (F0<Boolean> attempt : attempts) {
			if (!attempt.e()) {
				continue;
			}
			if (testF.e()) {
				return true;
			}
		}
		return false;
	}

}
