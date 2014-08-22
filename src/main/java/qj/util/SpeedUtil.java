package qj.util;

import qj.util.funct.P0;

public class SpeedUtil {

	public static int performance(final P0 tested) {
		int testTime = 500;
		return performance(testTime, tested);
	}

	public static int performance(int testTime, final P0 tested) {
		ThreadUtil.executorServiceDaemon.execute(new Runnable() {public void run() {
		}});
		
		final int[] count = {0};
		final boolean[] interrupted = {false};
		ThreadUtil.executorServiceDaemon.execute(new Runnable() {public void run() {
			while (!interrupted[0]) {
				tested.e();
				count[0]++;
			}
		}});
		ThreadUtil.sleep(testTime);
		interrupted[0] = true;
		return count[0];
	}

}
