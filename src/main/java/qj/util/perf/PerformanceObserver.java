package qj.util.perf;

import java.util.*;
import java.util.Map.Entry;

import qj.util.*;
import qj.util.funct.P0;

public class PerformanceObserver {

	LinkedList<Map<String, int[]>> allRounds = new LinkedList<Map<String, int[]>>();
	Map<String, int[]> roundReport = new HashMap<String, int[]>();
	private final boolean disabled;
	
	public PerformanceObserver(boolean enable) {
		this.disabled = !enable;
		
		reportOnReturn();
	}

	public void finish() {
		if (disabled) return;
		step(null);
		
		allRounds.add(0, roundReport);
		if (allRounds.size() > 1000) {
			allRounds.removeLast();
		}
		clear();
	}

	public void clear() {
		if (disabled) return;
		roundReport = new HashMap<String, int[]>();
		currentStep = null;
	}
	
	public void start(String name) {
		if (disabled) return;
		clear();
		step(name);
	}

	String currentStep;
	long   started;
	public void step(String name) {
		if (disabled) return;
		long currentTimeMillis = System.currentTimeMillis();
		if (currentStep != null) {
			MathUtil.add(currentStep, (int)(currentTimeMillis - started), roundReport);
		}
		
		if (name!=null) {
			started = currentTimeMillis;
			currentStep = name;
		}
	}
	

	public static void main(String[] args) {
		
		PerformanceObserver perf = new PerformanceObserver(true);

		perf.reportOnReturn();
		
		while (true) {
			perf.step("A");
			ThreadUtil.sleep(10);
			perf.step("B");
			ThreadUtil.sleep(50);
			perf.step("C");
			ThreadUtil.sleep(100);

			perf.finish();
		}
	}


	public void reportOnReturn() {
		if (disabled) return;
		SystemUtil.onReturn(new P0() {public void e() {
			report();
		}});
	}


	public void report() {
		if (disabled) return;
		Map<String, int[]> totalReport = new TreeMap<String, int[]>();
		for (Map<String, int[]> report : allRounds) {
			add(report, totalReport);
		}
		
		System.out.println("Perf report (" + allRounds.size() + " rounds):");
		for (Entry<String, int[]> entry : totalReport.entrySet()) {
			System.out.println(" - " + entry.getKey() + ": " + entry.getValue()[0]);
		}
		allRounds.clear();
		System.out.println("Report done.");
	}


	public void add(Map<String, int[]> report, Map<String, int[]> totalReport) {
		for (Entry<String, int[]> entry : report.entrySet()) {
			MathUtil.add(entry.getKey(), entry.getValue()[0], totalReport);
		}
	}
}
