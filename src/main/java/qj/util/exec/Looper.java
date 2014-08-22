package qj.util.exec;

import qj.util.ThreadUtil;
import qj.util.funct.F0;
import qj.util.funct.P0;
import qj.util.funct.P1;

public class Looper<R> {
		
	public F0<R> runF;
	private int rate;
	private P1<R> onResult;
	private boolean active = true;
	private boolean paused = false;
	private int normalSpeedRate;
	private int slowSpeedRate;

	public void run() {
		rate = normalSpeedRate;
		while (active) {
			try {
				ThreadUtil.sleep(rate);
				
				while (paused) {
					ThreadUtil.sleep(500);
				}
				
				R result = runF.e();
				
				onResult.e( result );
			} catch (Throwable e) {
				e.printStackTrace();
				ThreadUtil.sleep(500);
			}
		}
	}

	public void setNormalSpeedRate(int normalSpeedRate) {
		this.normalSpeedRate = normalSpeedRate;
	}
	public void setSlowSpeedRate(int slowSpeedRate) {
		this.slowSpeedRate = slowSpeedRate;
	}

	public P1<Boolean> setPausedF() {
		return new P1<Boolean>() {public void e(Boolean pau) {
			paused = pau;
		}};
	}

	public P1<Boolean> setNormalSpeedF() {
		return new P1<Boolean>() {public void e(Boolean obj) {
			if (obj) {
				rate = normalSpeedRate;
			} else {
				rate = slowSpeedRate;
			}
		}};
	}

	public void onResult(P1<R> onResult) {
		this.onResult = onResult;
	}

	public P0 stopF() {
		return new P0() {public void e() {
			active = false;
		}};
	}
	
}