package qj.util.math;

import java.util.LinkedList;
import java.util.List;

import qj.util.funct.P1;

public class Ranges {
	public static List<Range> join(List<Range> sortedRanges) {
//		System.out.println(sortedRanges);
		Range iRange = null;
		LinkedList<Range> ret = new LinkedList<Range>();
		for (Range range : sortedRanges) {
			if (iRange == null) {
				iRange = range;
			} else if (iRange.getTo().equals(range.getFrom())) {
				iRange = new Range(iRange.getFrom(), range.getTo());
			} else {
				ret.add(iRange);
				iRange = range;
			}
		}
		if (iRange != null) {
			ret.add(iRange);
		}
		return ret;
	}

	public static void eachRangeNot(Iterable<Range> rangeIter, int length, P1<Range> p) {
		int index = 0;
		for (Range range : rangeIter) {
			if (range.getFrom() > index) {
				p.e(new Range(index, range.getFrom()));
			}
			index = range.getTo();
		}
		
		if (index < length) {
			p.e(new Range(index, length));
		}
	}
}
