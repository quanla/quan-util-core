package qj.util.math;

import java.util.*;

import qj.util.Cols;
import qj.util.MathUtil;
import qj.util.funct.*;

public class RangedJobs {
	ArrayList<RangeJob> list = new ArrayList<RangeJob>();
	int activeCount = 0;

	public void add(Range range, P1<Range> job) {
		RangeJob rangeJob = new RangeJob(range, job);
		add(rangeJob);
	}

	public void addDirectly(Range range, P1<Range> job) {

		RangeJob lastJob;
		
		if (list.size() > 0 && (lastJob = list.get(list.size() - 1)).job == job && lastJob.range.getTo().equals(range.getFrom())) {
			lastJob.range.expandRight(range.size());
		} else {
			RangeJob rangeJob = new RangeJob(range, job);
			list.add(rangeJob);
			activeCount++;
		}
		
	}

	public void add(RangeJob rangeJob) {
		int index = Cols.searchIndexedBinary(list, RangeJob.rangeF, rangeJob.range);
		if (index < 0) {
			index = -index - 1;
		}
		
		list.add(index, rangeJob);
		activeCount++;
		// TODO 2 steps, 1 add (LinkedList), 1 sort?
	}
	
	public void act(Range range, final F0<Boolean> interrupted) {
		Cols.eachZeroDistances(
				MathUtil.distanceF(range), 
				Cols.randomAccessCol(list, RangeJob.rangeF), 
				new F1<Integer, Boolean>() {public Boolean e(Integer index) {
					RangeJob rangeJob = list.get(index);
					if (rangeJob.active) {
						rangeJob.job.e(rangeJob.range);
						rangeJob.active = false;
						activeCount--;
					}
					return interrupted.e();
				}}
		);
	}
	
	public static class RangeJob {
		public Range range;
		public P1<Range> job;
		boolean active = true;
		public RangeJob(Range range, P1<Range> job) {
			this.range = range;
			this.job = job;
		}
		static F1<RangeJob, Range> rangeF = new F1<RangeJob, Range>() {public Range e(RangeJob obj) {
			return obj.range;
		}};
	}

	public boolean retired() {
		return activeCount == 0;
	}

	public void accept(RangedJobs rangedJobs) {
		for (RangeJob rangeJob : rangedJobs.list) {
			accept(rangeJob);
		}
	}

	public void accept(final RangeJob newRangeJob) {
		final Collection<Integer> removeds = new TreeSet<Integer>();
		final LinkedList<RangeJob> adds = new LinkedList<RangeJob>();
		
		// 1. Make room for the new job
		Cols.eachZeroDistances(
				MathUtil.distanceF(newRangeJob.range), 
				Cols.randomAccessCol(list, RangeJob.rangeF), 
				new F1<Integer, Boolean>() {public Boolean e(Integer index) {
					
					RangeJob rangeJob = list.get(index);
					if (rangeJob.range.equals(newRangeJob.range)) {
						removeds.add(index);
					} else {
						List<Range> andNots = MathUtil.andNot(rangeJob.range, newRangeJob.range);
						
						boolean first = true;
						for (Range andNot : andNots) {
							if (first) {
								first = false;
								rangeJob.range = andNot;
							} else {
								// Add
								adds.add(new RangeJob(andNot, rangeJob.job));
							}
						}
					}
					
					return false;
				}}
		);
		
		// 1.2 Remove
		ArrayList<Integer> removedList = new ArrayList<Integer>(removeds);
		for (int i = removedList.size() -1; i > -1; i--) {
			Integer removed = removedList.get(i);
			list.remove(removed.intValue());
		}
		
		// 1.3 Add old
		for (RangeJob rangeJob : adds) {
			add(rangeJob);
		}
		
		// 2. Add new Job
		add(newRangeJob);
	}
}
