package qj.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import qj.util.funct.P0;

public class SchedulerUtil {
	static ArrayList taskHolderList = new ArrayList();
	static ExecutorService executorService = Executors.newCachedThreadPool();

	public static SchedulerTask schedule(long duration, Runnable proc) {
		return schedule(duration, false, proc);
	}

    public static SchedulerTask schedule(Date startTime, Runnable proc) {
		return schedule(startTime.getTime() - System.currentTimeMillis(), false, proc);
    }

	public static SchedulerTask schedule(long duration, boolean repeat, Runnable proc) {

        String className = ThreadUtil4.getLastClassCall();
		SchedulerTask task = new SchedulerTask(duration, proc);
		task.repeating = repeat;
		taskHolderList.add(new TaskHolder(className, task));
		
		executorService.execute(task);
		return task;
	}
	
	public static int cancel() {
		String className = ThreadUtil4.getLastClassCall();
        int count = 0;
		for (int i = taskHolderList.size() - 1; i > -1; i--) {
			TaskHolder taskHolder = (TaskHolder) taskHolderList.get(i);
			if (className.equals(taskHolder.className)) {
				taskHolder.task.run = false;
				taskHolderList.remove(i);
                count ++;
			}
		}
        return count;
	}

    static class TaskHolder {
		String className;
		SchedulerTask task;
		public TaskHolder(String className, SchedulerTask task) {
			this.className = className;
			this.task = task;
		}
	}
	
	public static class SchedulerTask implements Runnable {
		Runnable proc;
		long delay;
		boolean run = true;
		boolean repeating = false;
		private Thread runner;
		public SchedulerTask(long duration, Runnable proc) {
			this.delay = duration;
			this.proc = proc;
		}

		long sleepTime = 0;
		long sleepSince = -1;
		public void run() {
			while (true) {
				try {
					sleepTime = delay;
					runner = Thread.currentThread();
					while (sleepTime > 0) {
						sleepSince = System.currentTimeMillis();
						long sleep = sleepTime;
						sleepTime = 0;
						Thread.sleep(sleep);
					}
					runner = null;
					if (run) {
						proc.run();
					}
					if (! repeating) {
						break;
					}
				} catch (InterruptedException e) {
					// Loop again
				}
			}
		}

		public void reschedule() {
			if (runner != null) {
				sleepTime = System.currentTimeMillis() - sleepSince;
//				runner.interrupt();
			}
		}
        
        public Runnable reschedule = new Runnable() {public void run() {
            reschedule();
        }};
	}

	public static TimerTask timerTask(final P0 p0) {
		return new TimerTask() {
			@Override
			public void run() {
				p0.e();
			}
		};
	}

	public static P0 p0(final TimerTask timerTask) {
		return new P0() {public void e() {
			timerTask.run();
		}};
	}
}
