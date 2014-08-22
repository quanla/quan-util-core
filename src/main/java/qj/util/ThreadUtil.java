package qj.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import qj.util.funct.F0;
import qj.util.funct.F2;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;
import qj.util.funct.P2;

public class ThreadUtil extends ThreadUtil4 {
	
	public static class Locker<A> {
		A value;
		long until;
		
		public Locker() {
		}
	}
	
	static <A> void cleanupLockers(HashMap<String, Locker<A>> lockers) {
		HashSet<String> needRemoves = new HashSet<String>();
		
		long now = System.currentTimeMillis();
		for (Entry<String, Locker<A>> entry : lockers.entrySet()) {
			if (entry.getValue().until < now) {
				needRemoves.add(entry.getKey());
			}
		}
		
		for (String key : needRemoves) {
			lockers.remove(key);
		}
	}
	
	public static <A> F2<String, F0<A>, A> preventMultiCallsF(final int lockWait) {
		final HashMap<String, Locker<A>> lockers = new HashMap<String,Locker<A>>();
		
		return new F2<String, F0<A>, A>() {public A e(String callId, F0<A> f0) {
			Locker<A> locker = lockers.get(callId);
			if (locker != null) {
//				locker.waiterCount.incrementAndGet();
				try {
					ThreadUtil.wait(locker);
				} catch (Exception e) {
				}
				
				cleanupLockers(lockers);
//				if (locker.waiterCount.decrementAndGet() <=0) {
//					lockers.remove(callId);
//				}
				return locker.value;
			} else {
				locker = new Locker<A>();
				lockers.put(callId, locker);
				
				A value = f0.e();

				locker.value = value;
				locker.until = System.currentTimeMillis() + lockWait;
				
				ThreadUtil.notifyAll(locker);

				return locker.value;
			}
		}};
	}

	public static P1<P0> asyncF = new P1<P0>() {public void e(P0 obj) {
		run(obj);
//		SwingUtilities.invokeLater(Fs.runnable(obj));
	}};
	
	public static final ThreadFactory daemonThreadFactory = new ThreadFactory() {
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		}
	};
	public static final ThreadFactory lowDaemonThreadFactory = new ThreadFactory() {
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setPriority(3);
			return thread;
		}
	};
	static final ExecutorService executorServiceDaemon = Executors.newCachedThreadPool(daemonThreadFactory);
	private static final ExecutorService executorService = Executors.newCachedThreadPool();
	

    public static void lowPriority(P0 p0) {
        Thread thread = new Thread(Fs.runnable(p0));
        thread.setDaemon(true);
        thread.setPriority(3);
        thread.start();
    }
    
    static int count = 0;
    public static void pauseSometime() {
        if (count++ % 1000 == 0) {
            ThreadUtil.sleep(50);
        }
    }

    public static void run(Runnable runnable) {
        run(1, runnable);
    }
    public static void run(int num, Runnable runnable) {
        for (int i = 0; i < num; i++) {
        	executorServiceDaemon.execute(runnable);
        }
    }
    
    public static void wait(final P0 wait, final P0 ok, final P0 fail) {
    	final boolean[] run = {false};
    	
    	executorServiceDaemon.execute(new Runnable() {public void run() {
        	try {
    			wait.e();
    			run[0] = true;
    		} catch (Exception e) {
    		}
			ThreadUtil.notifyAll(run);
		}});
    	
    	wait(run, 20000);
    	
		if (run[0]) {
			ok.e();
		} else {
			run[0] = true;
			fail.e();
		}
    	
    }

	public static P0 async(final P0 p0) {
		return new P0() {public void e() {
			run(p0);
		}};
	}
	public static P0 asyncStrong(final P0 p0) {
		return new P0() {public void e() {
			runStrong(p0);
		}};
	}
	
	public static <A> P1<A> async(final P1<A> p1) {
		return new P1<A>() {public void e(final A a) {
			ThreadUtil.async(Fs.p0(p1, a));
		}};
	}
	
	public static <A,B> P2<A,B> async(final P2<A, B> p2) {
		return new P2<A, B>() {public void e(final A a,final  B b) {
			ThreadUtil.async(Fs.p0(p2, a,b));
		}};
	}

	public static boolean attempt(int wait, final F0<Boolean> f0) {
		final boolean[] result = {false};
		executorServiceDaemon.execute(new Runnable() {public void run() {
			result[0] = f0.e();
		}});
		ThreadUtil.sleep(wait);
		return result[0];
	}

	public static int performance(final P0 tested) {
		return SpeedUtil.performance(tested);
	}
	
	public static P0 execSometimes(final P0 p0) {
	    final long[] lastRun = {0};
		return new P0(){public void e() {
			long currentTimeMillis = System.currentTimeMillis();
			if (currentTimeMillis - lastRun[0] > 200) {
				p0.e();
				lastRun[0] = currentTimeMillis;
	        }
		}};
	}

	public static void run(P0 p0) {
		run(Fs.runnable(p0));
	}

	public static void runNice(P0 p0) {
		run(Fs.nice(p0));
	}

	
	public static void runStrong(final P0 p0) {
		executorService.execute(Fs.runnable(p0));
	}

	/**
	 * Single job worker
	 * @param p1 interupted?
	 * @return Interruptor
	 */
	public static P0 run(final P1<F0<Boolean>> p1) {
		final Boolean[] interrupted = {false};

		final F0<Boolean> tester = Fs.booleanRef(interrupted);
		
		run(Fs.p0(p1, tester));
		
		return new P0() {public void e() {
			interrupted[0] = true;
		}};
	}

//	/**
//	 * Single job worker
//	 * @param p1
//	 * @return 
//	 */
//	public static P0 worker(final P1<F0<Boolean>> p1) {
//		final Boolean[] interrupted = {false};
//
//		final F0<Boolean> tester = Fs.f0(interrupted);
//		
//		lowPriority(new P0() {
//			public void e() {
//				while (true) {
//					if (!interrupted[0]) {
//						ThreadUtil.wait(interrupted);
//					}
//					interrupted[0] = false;
////					long start = System.currentTimeMillis();
//					p1.e(tester);
////					System.out.println("Processed in " + (System.currentTimeMillis() - start) + "ms");
//				}
//			}
//		});
//		
//		return new P0() {public void e() {
//			interrupted[0] = true;
//			ThreadUtil.notifyAll(interrupted);
//		}};
//	}
	
	public static <A> A waitNotNull(F0<A> f, int timeout) {
		boolean timeoutEnabled = timeout!=0;
        A ret = null;
		while ((ret = f.e())==null) {
			if (timeoutEnabled && timeout <=0 ) {
				return ret;
			}
            sleep(100);
            timeout-=100;
        }
		return ret;
	}
	/**
	 * Wait until returns true
	 * @param until
	 */
    public static void wait(F0<Boolean> until) {
        while (!until.e()) {
            sleep(500);
        }
    }

	public static P0 notifyAllF(final Object lock) {
		return new P0(){public void e() {
			ThreadUtil.notifyAll(lock);
		}};
	}

	/**
	 * Never call to p0 twice
	 * @param p0
	 * @return
	 */
	public static P0 invokeOnce(final P0 p0) {
		final boolean[] interrupted = {false};
		return new P0() {public void e() {
			if (!interrupted[0]) {
				interrupted[0] = true;
				p0.e();
			}
		}};
	}

	public static interface DelayedExecution {
		void delayRun();
		void cancel();
	}
	
	public static void delayedExec(final long delay, final P0 task) {
		run(new P0() {public void e() {
			ThreadUtil.sleep(delay);
			task.e();
		}});
	}
	

	/**
	 * Render only one running thread a time.
	 * Each time this P0 is called, it will try to launch a thread to execute the P1<F0>
	 * Also, it will send the interrupt message to all previously launched threads so that this
	 * new Thread is the only one running
	 * @param p1
	 * @return
	 */
	public static P0 async1(final P1<F0<Boolean>> p1) {
		return Fs.p0(async1F(), p1);
	}

	/**
	 * Render only one running thread a time.
	 * Each time this P1<P1<F0>> is called, it will try to launch a thread to execute the P1<F0>
	 * Also, it will send the interrupt message to all previously launched threads so that this
	 * new Thread is the only one running
	 * @return
	 */
	public static P1<P1<F0<Boolean>>> async1F() {
		final Boolean[][] flag = {null};
		return new P1<P1<F0<Boolean>>>() {public void e(final P1<F0<Boolean>> run) {
			if (flag[0] != null) {
				flag[0][0] = true;
			}
			Boolean[] b = new Boolean[] {false};
			flag[0] = b;
			final F0<Boolean> testF = Fs.booleanRef(b);

			if (run != null) {
				ThreadUtil.run(new P0() {public void e() {
					if (testF.e()) return;
						
					run.e(testF);
				}});
			}
		}};
	}
	
	public static P1<P0> asyncF(int threadLimit) {
		final ExecutorService executorService = Executors.newFixedThreadPool(threadLimit, daemonThreadFactory);
		return new P1<P0>() {public void e(P0 p0) {
			executorService.execute(Fs.runnable(p0));
		}};
	}
	
	public static P1<P0> lowAsyncF(int threadLimit) {
		final ExecutorService executorService = Executors.newFixedThreadPool(threadLimit, lowDaemonThreadFactory);
		return new P1<P0>() {public void e(P0 p0) {
			executorService.execute(Fs.runnable(p0));
		}};
	}

	static Timer timer = null;
	public static void schedule(int delay, final P0 p0) {
		if (timer == null) {
			timer = new Timer(true);
		}
		timer.schedule(new TimerTask() {public void run() {
			p0.e();
		}}, delay);
	}

	public static String dumpStack(Thread t) {
		StringBuilder sb = new StringBuilder();
		sb.append(t.getName() + "[" + t.getId() + "] - " + t.getState() + ":\n");
		for (StackTraceElement ste : t.getStackTrace()) {
			sb.append("\tat " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\n");
		}
		return sb.toString();
	}

	public static P0 sleepF(final long time) {
		return new P0() {public void e() {
			sleep(time);
		}};
	}

	public static void interval(final P0 p0, final int interval) {
		run(new P0() {public void e() {
			long lastStart = System.currentTimeMillis();
			while (true) {
				ThreadUtil.sleep(interval - (System.currentTimeMillis() - lastStart) );
				lastStart = System.currentTimeMillis();
				p0.e();
			}
		}});
	}
	
	public static <A> ThreadLocalCache<A> threadLocalCache(final F0<A> f) {
		
		final ThreadLocal<A> threadLocal = new ThreadLocal<A>();
		
		ThreadLocalCache<A> ret = new ThreadLocalCache<A>();
		
		ret.cacheF = new F0<A>() {public A e() {
			A a = threadLocal.get();
			if (a==null) {
				a = f.e();
				threadLocal.set(a);
			}
			return a;
		}};
		ret.removeF = new F0<A>() {public A e() {
			A a = threadLocal.get();
			threadLocal.set(null);
			return a;
		}};
		
		return ret;
	}
	
	public static class ThreadLocalCache <A> {
		public F0<A> cacheF;
		public F0<A> removeF;
	}

	public static boolean waitUntil(F0<Boolean> until, int timeout) {
		final long start = System.currentTimeMillis();
        while (!until.e()) {
            sleep(500);
			if (System.currentTimeMillis() - start > timeout) {
				return false;
			}
        }
        return true;
	}
	
}
