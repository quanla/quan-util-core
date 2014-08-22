package qj.util.structure;

import qj.util.funct.F1;
import qj.util.math.Range;

public class TagRange implements Comparable<TagRange> {
	public TagRange(Object tag, Range range) {
		this.tag = tag;
		this.range = range;
	}
	public Object tag;
	public Range range;
	public static F1<TagRange,Range> rangeF = new F1<TagRange,Range>() {public Range e(TagRange tagRange) {
		return tagRange.range;
	}};
	public String toString() {
		return "[" + range + "] " + tag;
	}
	public int compareTo(TagRange o) {
		return range.compareTo(o.range);
	}
}