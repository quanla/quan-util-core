package qj.util;

import java.util.concurrent.ThreadFactory;

import qj.util.funct.F0;

public class ThreadUtil4 {
	
	public static final ThreadFactory DEAMON_TF = new ThreadFactory() {
		public Thread newThread(Runnable run) {
			Thread t = new Thread(run);
			t.setDaemon(true);
			return t;
		}
	};

    public static String getLastClassCall() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callingClass = stackTrace[2].getClassName();
        for (int i = 3; i < 3000; i++) {
            String aClass = stackTrace[i].getClassName();
            if (!callingClass.equals(aClass)) {
                return aClass;
            }
        }
        return null;
    }

    public static void sleepForever() {
        sleep(Long.MAX_VALUE);
    }

    public static interface Process {
		void pause();
		void cancel();
		void resume();
	}
	
	public static Runnable canceller(final Process p) {
		return new Runnable() {public void run() {
			System.out.println("Cancelled");
			p.cancel();
		}};
	}

	public static Process timeout(final Runnable run, final int millis) {
		class P implements Runnable, Process {
			boolean alive = true;
			public void run() {
				if (alive) {
					ThreadUtil4.sleep(millis);
				}
				if (alive) {
					run.run();
				}
			}
			public void cancel() {
				alive = false;
			}
			public void pause() {
			}
			public void resume() {
			}
			
		}
		P process = new P();
		new Thread(process).start();
		return process;
	}
	
	public static Process interval(final Runnable run, final int delayMillis) {
		class P implements Runnable, Process {
			boolean alive = true;
			boolean pause = false;
			public void run() {
				while (alive) {
					ThreadUtil4.sleep(delayMillis);
					if (pause) {
						ThreadUtil4.wait(this);
					}
					if (alive) {
						run.run();
					}
				}
			}
			public void cancel() {
				alive = false;
			}
			public synchronized void pause() {
				pause = true;
			}
			public void resume() {
				pause = false;
				ThreadUtil4.notifyAll(this);
			}
		}
		P process = new P();
		new Thread(process).start();
		return process;
	}

	/**
	 * Sleep and wake on InterruptedException
	 * @param timeToSleep in milliseconds
	 */
	public static void sleep(long timeToSleep) {
		if (timeToSleep <=0)
			return;
		try {
			Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Sleep and ignore InterruptedException
	 * Goto sleep again if wake 1ms ealier
	 * @param timeToSleep in milliseconds
	 */
	public static void sleep_force(long timeToSleep) {
		if (timeToSleep <=0)
			return;
		long target = System.currentTimeMillis() + timeToSleep;
		while (true)
			try {
				Thread.sleep(timeToSleep);
				timeToSleep = target - System.currentTimeMillis();
				if (timeToSleep < 2)
					return;
			} catch (InterruptedException e) {
				timeToSleep = target - System.currentTimeMillis();
			}
	}

	/**
	 * Sleep and ignore InterruptedException
	 * Goto sleep again if wake 1ms ealier
	 * @param timeToSleep in milliseconds
	 */
	public static void sleepExact(long timeToSleep) {
		if (timeToSleep <=0)
			return;
		long target = System.currentTimeMillis() + timeToSleep;
		
		if (timeToSleep > 200) {
			try {
				Thread.sleep(timeToSleep - 200);
			} catch (InterruptedException e) {
			}
		}
		
		try {
			Thread.sleep(target - System.currentTimeMillis());
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Sleep and ignore InterruptedException
	 * @param timeToSleep in milliseconds
	 */
	public static void sleepExact(long timeToSleep, F0<Long> progressF) {
		if (timeToSleep <=0)
			return;
		
		if (timeToSleep < 1000) {
			sleep(timeToSleep);
			return;
		}

		long beforeProgress = System.currentTimeMillis();
		if (timeToSleep > 200) {
			sleep(timeToSleep - 200);
//			
//			sleep(beforeProgress + 50 - System.currentTimeMillis());
//			System.out.println("progress 50=" + progressF.e());
//			sleep(beforeProgress + 100 - System.currentTimeMillis());
//			System.out.println("progress100=" + progressF.e());
//			sleep(beforeProgress + 200 - System.currentTimeMillis());
//			System.out.println("progress200=" + progressF.e());
//			sleep(beforeProgress + 400 - System.currentTimeMillis());
//			System.out.println("progress400=" + progressF.e());
//			sleep(beforeProgress + 800 - System.currentTimeMillis());
//			System.out.println("progress800=" + progressF.e());
		}
//		System.out.println("Slept 1: " + (System.currentTimeMillis()-beforeProgress) + 
//				"; Trying to sleep: " + (timeToSleep - 200));
		
		beforeProgress = System.currentTimeMillis();
		Long progress = progressF.e();
//		System.out.println("progress=" + progress);
		long var = System.currentTimeMillis() - beforeProgress;
//		System.out.println("timeToSleep=" + timeToSleep);
//		System.out.println("progress=" + progress);
//		System.out.println("var=" + var);
//		System.out.println("left=" + (timeToSleep - progress - var));
		try {
			long millis = timeToSleep - (progress + var);
			if (millis > 0) {
				Thread.sleep(millis);
			}
		} catch (InterruptedException e) {
		}
	}

	public static void wait(Object o) {
		wait(o, 0);
	}

	public static void notifyAll(Object o) {
		synchronized (o) {
			o.notifyAll();
		}
	}

	public static void wait(Object o, int timeout) {
		synchronized (o) {
			while (true) {
				try {
					o.wait(timeout);
					return;
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
}
