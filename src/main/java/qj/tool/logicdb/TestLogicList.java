package qj.tool.logicdb;

import qj.util.funct.F1;
import qj.util.math.Range;

public class TestLogicList {

	public static void main(String[] args) {
		LogicList<TagRange> logicList = new LogicList<TagRange>(new F1<TagRange,Object>() {public Object e(TagRange obj) {
			return obj.tag;
		}});
		
		logicList.indexString(ValueOf.class, ValueOf.keyF);
		
		
		logicList.add(new TagRange(new Range(1,2), new ValueOf("he")));
		
		System.out.println(logicList.get(new ValueOf("he")));
	}
	
	private static class TagRange implements Comparable<TagRange> {
		public TagRange(Range range, Object marker) {
			this.tag = marker;
			this.range = range;
		}
		public Object tag;
		public Range range;
		public String toString() {
			return "[" + range + "] " + tag;
		}
		public int compareTo(TagRange o) {
			return range.getFrom() - o.range.getFrom();
		}
	}
	
	private static class ValueOf {

		public static F1<ValueOf,String> keyF = new F1<ValueOf, String>() {public String e(ValueOf obj) {
			return obj.key;
		}};
		public final String key;

		public ValueOf(String key) {
			this.key = key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ValueOf other = (ValueOf) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}
	}
}
