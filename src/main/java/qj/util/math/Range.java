package qj.util.math;

import java.util.Map;
import java.util.regex.Matcher;

import qj.util.Cols;
import qj.util.MathUtil;
import qj.util.ObjectUtil;
import qj.util.funct.F1;

/**
 * TODO replaced by commons range
 */
public class Range implements Comparable<Range> {
	private Integer from;
	private Integer to;

    public Range() {
    }

    public Range(Integer from, Integer to) {
        this.from = from;
        this.to = to;
        if (to != null && to < from) {
        	throw new RuntimeException("to(" + to + ") < from(" + from + ")");
        }
    }

    public Range(Long start, Long end) {
        this.from 	= start	== null ? null : start.intValue();
        this.to 	= end   == null ? null : end.intValue();
        if (to != null && to < from) {
        	throw new RuntimeException();
        }
	}

	public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public String toString() {
        return from + "-" + to;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        Range o2 = (Range) obj;
        return ObjectUtil.equals(from, o2.from)
                && ObjectUtil.equals(to, o2.to);
    }


    public int length() {
        return to - from;
    }
    public boolean isEmpty() {
        return to.equals(from);
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public int compareTo(Range o) {
        return from.compareTo(((Range)o).getFrom());
//    	return MathUtil.distance(this, o);
    }
    
    
    
    public static void main(String[] args) {
//		System.out.println(new Integer(2).compareTo(1));
    	System.out.println(new Range(1,2).compareTo(new Range(5,6)));
	}

	public int size() {
		return length();
	}

	public void shiftRightOf(Integer pos) {
		if (from > pos) {
			from +=1;
		}
		if (to >= pos) {
			to+=1;
		}
	}

	public static Range fromlength(int from, int length) {
		return new Range(from, from + length);
	}

	public Range shiftRight(Integer length) {
		return new Range(from + length, to + length);
	}

	public boolean contains(int pos) {
		return MathUtil.in(pos, this);
	}

	public Range shrink(int length) {
		return expand(-length);
	}

	public Range shrinkLeft(int length) {
		return new Range(from + length, to);
	}
	public Range expandLeft(int length) {
		return new Range(from - length, to);
	}
	public Range expandRight(int length) {
		return new Range(from, to + length);
	}

	public Range expand(int length) {
		return new Range(from - length, to + length);
	}

	public static Range group(int i, Matcher matcher) {
		return new Range(matcher.start(i), matcher.end(i));
	}
	
	public static F1<Range,Map> toMap = new F1<Range, Map>() {public Map e(Range range) {
//		return "{\"from\":" + range.from + ",\"to\":" + range.to + "}";
		return Cols.map("from", range.from, "to", range.to);
	}};
}
