package qj.util.desktop;

import qj.util.DesktopUtil;
import qj.util.funct.F0;
import qj.util.math.Point;

public class MouseUtil {

	public static F0<Boolean> notMoveObserver(final int duration) {
		final long[] lastCheck = {System.currentTimeMillis()};
		final Point[] pos = {DesktopUtil.getMousePos2()};
		return new F0<Boolean>() {public Boolean e() {
			Point pos1 = DesktopUtil.getMousePos2();
			
			if (!pos1.equals(pos[0])) {
				pos[0] = pos1;
				lastCheck[0] = System.currentTimeMillis();
				return false;
			} else {
				return System.currentTimeMillis() - lastCheck[0] > duration;
			}
		}};
	}
	
}
