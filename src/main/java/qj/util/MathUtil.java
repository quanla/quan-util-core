package qj.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import qj.util.funct.Douce;
import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.funct.F2;
import qj.util.funct.P0;
import qj.util.funct.P1;
import qj.util.funct.P2;
import qj.util.math.ArcD;
import qj.util.math.CircleD;
import qj.util.math.LineD;
import qj.util.math.MathUtil4;
import qj.util.math.PointD;
import qj.util.math.PolygonD;
import qj.util.math.Range;
import qj.util.math.RectangleD;
import qj.util.math.TriangleD;

public class MathUtil extends MathUtil4 {
	/**
	 * 
	 * @param from
	 * @param to
	 * @param checkF return &gt;0 target is higher, &lt;0 target is smaller
	 * @return
	 */
	public static int findIntegralMatchI(int from, int to, F1<Integer,Integer> checkF) {
		for (;from <= to;) {
			int middle = (from + to) / 2;
			int check = checkF.e(middle);
			if (check == 0) {
				return middle;
			} else if (check > 0) {
				from = middle+1;
			} else {
				to = middle -1;
			}
		}
		return -1;
	}
	/**
	 * 
	 * @param from
	 * @param to
	 * @param checkF return &gt;0 target is higher, &lt;0 target is smaller
	 * @return
	 */
	public static long findIntegralMatchL(long from, long to, F1<Long,Integer> checkF) {
		for (;from <= to;) {
			long middle = (from + to) / 2;
			long check = checkF.e(middle);
			if (check == 0) {
				return middle;
			} else if (check > 0) {
				from = middle+1;
			} else {
				to = middle -1;
			}
		}
		return -1;
	}
	/**
	 * 
	 * @param from
	 * @param to
	 * @param checkF return &gt;0 target is higher, &lt;0 target is smaller
	 * @return
	 */
	public static BigDecimal findIntegralMatch(BigDecimal from, BigDecimal to, F1<BigDecimal,Integer> checkF) {
		for (;from.compareTo(to)<=0;) {
			BigDecimal middle = from.add(to).divideToIntegralValue(BigDecimal.valueOf(2));
			int check = checkF.e(middle);
			if (check == 0) {
				return middle;
			} else if (check > 0) {
				from = middle.add(BigDecimal.ONE);
			} else {
				to = middle;
			}
		}
		return null;
	}
    public static F0<Boolean> onoff() {
        final boolean[] b = new boolean[1];
        return new F0<Boolean>() {public synchronized Boolean e() {
            return b[0] = !b[0];
        }};
    }
    
    public static F0<Integer> goAround(final int bound) {
    	final int[] i = {0};
    	return new F0<Integer>() {public Integer e() {
			int ret = i[0] ++;
			if (i[0] == bound) {
				i[0] = 0;
			}
			return ret;
		}};
    }
    
    public static PointD angleShift(PointD center, double radius, double angle) {
    	double rAngle = Math.toRadians(angle);
    	return new PointD(
    			Math.cos(rAngle) * radius + center.x,
    			Math.sin(rAngle) * radius + center.y
    			);
    }
    

    public static List<Range> and(List<Range> ranges1, List<Range> ranges2) {
        List<Range> ret = new LinkedList<Range>();
        for (Range range1 : ranges1) {
            ret.addAll(and(range1, ranges2));
        }
        return ret;
    }

    public static List<Range> and(Range range, List<Range> ranges) {
        List<Range> ret = new LinkedList<Range>();

        int fromRange = findRangeAbove(range.getFrom(), ranges);
        int toRange = findRangeBelow(range.getTo(), ranges);
        for (int i = fromRange; i > -1 && i <= toRange; i++) {
            Range and = and(range, ranges.get(i));
            if (and!=null) {
                ret.add(and);
            }
        }

        return ret;
    }

    public static Range and(Range range1, Range range2) {
        return and(range2, range1.getFrom(), range1.getTo());
    }

    private static Range and(Range range, Integer limit1, Integer limit2) {
        int from = Math.max(limit1, range.getFrom());
        int to = Math.min(limit2, range.getTo());

        if (from < to) {
            if (from == range.getFrom() && to == range.getTo()) {
                return range;
            } else {
                return new Range(from, to);
            }
        } else {
            return null;
        }
    }
    

    private static int findRangeAbove(Integer index, List<Range> ranges) {
        for (int i = 0; i < ranges.size(); i++) {
            Range intRange =  ranges.get(i);
            if (intRange != null && intRange.getTo() >= index) {
                return i;
            }
        }
        return -1;
    }

    private static int findRangeBelow(Integer index, List<Range> ranges) {
        for (int i = ranges.size() - 1; i >= 0; i--) {
            Range intRange = ranges.get(i);
            if (intRange != null && intRange.getFrom() <= index) {
                return i;
            }
        }
        return -1;
    }


    public static boolean inWithBound(int pos, List<Range> range) {

        return false;  //To change body of created methods use File | Settings | File Templates.
    }


    public static boolean in(int pos, Range range) {
        return (range.getFrom() == null || pos >= range.getFrom()) &&
                (range.getTo() == null || pos < range.getTo());
    }
	    

    /**
     * Solve the equation a*x*x + b*x + c = 0
     * @param a a
     * @param b b
     * @param c c
     * @param resultF Called on each result found
     */
    public static void solveSquareEquation(int a, int b, int c, P1<Double> resultF) {
        double delta = b*b - 4*a*c;
        if (delta == 0) {
            resultF.e(-1d * b / 2 / a);
        } else if (delta > 0) {
            resultF.e( (-1d *b + Math.sqrt(delta)) / 2 / a );
            resultF.e( (-1d *b - Math.sqrt(delta)) / 2 / a );
        } else {
            // No result
        }
    }

	public static Douce<Double, Double> solvePt2(double a, double b, double c) {
		double delta = b*b - 4*a*c;
		if (delta < 0) {
			return null;
		} else {
			return new Douce<Double, Double>(
					(-1 * b - Math.sqrt(delta)) / 2 / a,
					(-1 * b + Math.sqrt(delta)) / 2 / a
			);
		}
	}

	
	/**
	 * Make use of binary search for seeking
	 * @param from The index that start searching
	 * @param to The index that stop searching
	 * @param indexToValue The function that translate index to real value
	 * @param targetMinValue The expected value, target of this method
	 * @return The index that would return value >= targetMinValue
	 */
	public static Integer seek(final int from, final int to, 
			final F1<Integer, Long> indexToValue, final long targetMinValue)
	{
		return new F2<Integer, Integer, Integer>() {
			public Integer e( Integer from, Integer to ){

				if (to - from == 1) {
					return to;
				}
				Long toVal = indexToValue.e( to );
				if (red( toVal )) {
					return null;
				}
				if ( toVal == targetMinValue ) {
					return to;
				}
				Long fromVal = indexToValue.e( from );
				if (green( fromVal )) {
					return from;
				}
				
				// Now to is green, from is red, next is to scope down
//				float diffRate = ((float)(targetMinValue - fromVal) / (toVal - fromVal));
//				int guessNewIndex = (int)(diffRate * (to - from)) + from;
				int middle = (int)((to - from) / 2) + from;
				Long middleVal = indexToValue.e( middle );
				if (green( middleVal )) {
					return e(from, middle);
				} else {
					return e(middle, to);
				}
			}

			private boolean red( Long val )
			{
				return val < targetMinValue;
			}
			private boolean green( Long val )
			{
				return val >= targetMinValue;
			}
        }.e( from, to );
	}

    public static <A> void increase(A a, Map<A, AtomicInteger> map) {
        AtomicInteger val = map.get(a);
        if (val == null ) {
            val = new AtomicInteger(1);
            map.put(a, val);
        } else {
            val.addAndGet(1);
        }
    }

	public static void increaseBig(String key, Number val,
			Map<String, BigDecimal> map) {
		BigDecimal val1 = map.get(key);
		if (val1==null) {
			map.put(key, new BigDecimal(val.toString()));
		} else {
			map.put(key, val1.add(new BigDecimal(val.toString())));
		}
	}
	
    public static <A> Map<A,Integer> intMap(Map<A, AtomicInteger> map) {
    	HashMap<A, Integer> ret = new HashMap<A, Integer>();
    	for (Entry<A, AtomicInteger> e : map.entrySet()) {
    		ret.put(e.getKey(), Integer.valueOf(e.getValue().get()));
		}
		return ret;
    }


	public static <A> void add(A key, int value, Map<A, int[]> map) {
		int[] valRef = map.get(key);
        if (valRef == null ) {
            valRef = new int[] {value};
            map.put(key, valRef);
        } else {
        	valRef[0] += value;
        }
	}

    public static BigDecimal sumB(Collection<BigDecimal> col) {
    	BigDecimal sum = BigDecimal.valueOf(0);
    	for (BigDecimal num : col) {
			sum = sum.add(num);
		}
		return sum;
    }
    public static double sum(Collection<Double> col) {
    	double sum = 0;
    	for (Double double1 : col) {
			sum += double1;
		}
		return sum;
    }
    public static int sumI(Collection<Integer> col) {
    	int sum = 0;
    	for (Integer double1 : col) {
			sum += double1;
		}
		return sum;
    }
    public static <A> int sum(Map<A, AtomicInteger> map) {
        int val = 0;
        for (AtomicInteger i : map.values()) {
            val += i.intValue();
        }
        return val;
    }

    public static boolean inNoBound(int pos, int from, int to) {
        return pos > from && pos < to;
    }

    public static boolean inNoBound(Collection<Integer> poss, int from, int to) {
        for (Integer pos : poss) {
            if (inNoBound(pos, from, to)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inWithBound(int pos, int from, int to) {
        return pos >= from && pos <= to;
    }
    public static boolean inWithBound(int pos, Range range) {
        return inWithBound(pos, range.getFrom(), range.getTo());
    }

    public static Collection<Integer> inWithBound(List<Integer> poses, int from, int to) {
        List<Integer> ret = new LinkedList<Integer>();
        for (Integer pos : poses) {
            if (inWithBound(pos, from, to)) {
                ret.add(pos);
            }
        }
        return ret;
    }

    public static Collection<Integer> moreOrEquals(List<Integer> targets, int from) {
        List<Integer> ret = new LinkedList<Integer>();
        for (Integer target : targets) {
            if (target >= from) {
                ret.add(target);
            }
        }
        return ret;

    }

    public static List<Range> andNot(Iterable<Range> ranges, Iterable<Range> andNot) {
        List<Range> ret = new LinkedList<Range>();
        for (Range range : ranges) {
            ret.addAll(andNot(range, andNot));
        }
        return ret;
    }
    

    public static List<Range> andNot(Range range, Range andNot) {
    	return andNot(range, Collections.singleton(andNot));
    }

    /**
     * 
     * @param range
     * @param andNot
     * @return Ranges belong to range but not in andNot
     */
    public static List<Range> andNot(Range range, Iterable<Range> andNot) {
        List<Range> ret = new LinkedList<Range>();
        int index = 0;
        for (Range notRange : andNot) {
            if (index < notRange.getFrom()) {
                Range and = and(range, index, notRange.getFrom());
                if (and != null) {
                    ret.add(and);
                }
            }
            index = notRange.getTo();
        }
        if (index < range.getTo()) {
            Range and = and(range, index, range.getTo());
            if (and != null) {
                ret.add(and);
            }
        }
        return ret;
    }

    public static List<Range> not(List<Range> ranges, int limit) {
        List<Range> ret = new LinkedList<Range>();

        int index = 0;
        for (Range remain : ranges) {
            if (remain.getFrom() > index) {
                ret.add(new Range(index, remain.getFrom()));
            }
            index = remain.getTo();
        }
        if (index < limit) {
            ret.add(new Range(index, limit));
        }
        return ret;
    }

    public static Range expand(Range range, List<Range> allows, int limit) {
        int rangeBelowIndex = findRangeBelow(range.getFrom(), allows);
        int from;
        if (rangeBelowIndex == -1) {
            from = 0;
        } else {
            Range rangeBelow = allows.get(rangeBelowIndex);
            if (!inWithBound(range.getFrom(), rangeBelow)) {
                from = rangeBelow.getTo();
            } else {
                from = range.getFrom();
            }
        }

        int rangeAboveIndex = findRangeAbove(range.getTo(), allows);
        int to;
        if (rangeAboveIndex == -1) {
            to = limit;
        } else {
            Range rangeBelow = allows.get(rangeAboveIndex);
            if (!inWithBound(range.getTo(), rangeBelow)) {
                to = rangeBelow.getFrom();
            } else {
                to = range.getTo();
            }
        }
        return new Range(from, to);
    }

    public static String toString(Collection<Range> ranges) {
        int index = 0;
        StringBuilder sb = new StringBuilder();
        for (Range range : ranges) {
            if (range == null) {
                continue;
            }
            if (range.getFrom() > index) {
                sb.append(StringUtil.createString(range.getFrom() - index, ' '));
            }
            sb.append(StringUtil.createString(range.getTo() - range.getFrom(), '-'));
            index = range.getTo();
        }
        return sb.toString();
    }

    public static boolean conflict(Range range, List<Range> ranges) {
        return Cols.isNotEmpty(and(range, ranges));
    }

    public static List<Range> or(Range range, List<Range> ranges) {
        List<Range> ret = new LinkedList<Range>();

        int index = 0;
        for (Range tRange : ranges) {
            if (index < tRange.getFrom()) {
                // Out
                Range and = and(new Range(index, tRange.getFrom()), range);
                if (and != null) {
                    ret.add(and);
                }
            }

            ret.add(tRange);
            index = tRange.getTo();
        }

        if (index < range.getTo()) {
            ret.add(and(new Range(index, range.getTo()), range));
        }

        return join(ret);
    }

    private static List<Range> join(List<Range> ranges) {
        Range last = null;
        List<Range> ret = new LinkedList<Range>();
        for (Range range : ranges) {
            if (last != null) {
                if (last.getTo().equals(range.getFrom())) {
                    last = new Range(last.getFrom(), range.getTo());
                } else {
                    ret.add(last);
                    last = range;
                }
            } else {
                last = range;
            }
        }
        if (last != null) {
            ret.add(last);
        }
        return ret;
    }

    public static boolean outWithBound(int pos, List<Range> ranges) {
        for (Range range : ranges) {
            if (inWithBound(pos, range)) {
                return false;
            }
        }
        return true;
    }

    /**
     *  -------  --         ------
     *  and
     *               -----
     *  become
     *  -------  -----------------
     * @param range
     * @param ranges
     * @return
     */
    public static List<Range> link(Range range, List<Range> ranges) {

        ArrayList<Range> ret = new ArrayList<Range>();

        int fromRange = findRangeAbove(range.getFrom(), ranges);
        int toRange = findRangeBelow(range.getTo(), ranges);
        if (fromRange == -1) {
            fromRange = 0;
        }
        if (toRange == -1) {
            toRange = ranges.size() -1;
        }
        for (int i = 0; i < fromRange && i < ranges.size(); i++) {
            Range intRange = ranges.get(i);
            ret.add(intRange);
        }
        ret.add(new Range(
                ranges.get(fromRange).getFrom(),
                ranges.get(toRange).getTo()
        ));

        for (int i = toRange + 1; i > -1 && i < ranges.size(); i++) {
            Range intRange = ranges.get(i);
            ret.add(intRange);
        }
        return ret;
    }

	public static int center(Range range) {
		return (range.getFrom() + range.getTo()) / 2;
	}

	public static Range shift(int i, Range range) {
		if (i == 0) {
			return range;
		} else {
			return new Range(range.getFrom() + i, range.getTo() + i);
		}
	}

	public static Double average(List<Double> nums) {
		if (nums.size()==0) {
			return 0D;
		}
		return sum(nums) / nums.size();
	}
	public static Double average(Double... nums) {
		return average(Arrays.asList(nums));
	}
	public static Double averageI(Collection<Integer> nums) {
		if (nums.size()==0) {
			return 0D;
		}
		return (double)(sumI(nums)) / nums.size();
	}


	public static BigDecimal average(BigDecimal... nums) {
		return averageB(Arrays.asList(nums));
	}
	public static BigDecimal averageB(List<BigDecimal> nums) {
		if (nums.size()==0) {
			return BigDecimal.valueOf(0);
		}
		return sumB(nums).divide(BigDecimal.valueOf(nums.size()));
	}

	public static Range getRange(int index, Collection<Range> pointRanges) {
		for (Range range : pointRanges) {
			if (inWithBound(index, range)) {
				return range;
			}
		}
		return null;
	}

	public static boolean conflict(Range range1, Range range2) {
		return and(range2, range1) != null;
	}

	public static boolean touch(Range range1, Range range2) {
		if (range1.getFrom() <= range2.getTo() 
				&& range1.getTo() >= range2.getFrom()
		) {
			return true;
		}
		return false;
	}


	public static F1<Range, Integer> distanceF(final int pos) {
		return new F1<Range, Integer>() {public Integer e(Range obj) {
			Range targetRange = (Range) obj;
			if (targetRange.getFrom() > pos) {
				return targetRange.getFrom() - pos;
			} else if (targetRange.getTo() != null && targetRange.getTo() < pos) {
				return targetRange.getTo() - pos;
			} else {
				return 0;
			}
		}};
	}
	
	/**
	 * Distance of 1-2 and 4-5 = -2
	 * @param targetRange 1-2
	 * @param sourceRange 4-5
	 * @return
	 */
	public static int distance(Range targetRange, Range sourceRange) {
		if (targetRange.getTo() < sourceRange.getFrom()) {
			return targetRange.getTo() - sourceRange.getFrom();
		} else if (targetRange.getFrom() > sourceRange.getTo()) {
			return targetRange.getFrom() - sourceRange.getTo();
		} else {
			return 0;
		}
	}
	
	/**
	 * Distance of 1-2 and 4-5 = -2
	 * @param targetRange
	 * @param pos
	 * @return
	 */
	public static int distance(Range targetRange, int pos) {
		if (targetRange.getTo() < pos) {
			return targetRange.getTo() - pos;
		} else if (targetRange.getFrom() > pos) {
			return targetRange.getFrom() - pos;
		} else {
			return 0;
		}
	}

	public static Point plus(Point point1, Point point2) {
		return new Point(point1.x + point2.x, point1.y + point2.y);
	}

	public static Point center(Rectangle rect) {
		return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
	}

	public static F1<Range, Integer> distanceF(final Range sourceRange) {
		return new F1<Range, Integer>() {public Integer e(Range range) {
			return distance(range, sourceRange);
		}};
	}



	public static int minDistance(int pos,
			Iterable<Range> ranges) {
		int minDistance = Integer.MAX_VALUE;
		if (ranges != null) {
			for (Range range : ranges) {
				int distance = distance(range, pos);
				minDistance = Math.min(minDistance, distance);
			}
		}
		return minDistance;
	}



	public static F1<Integer,Integer> add(final int add) {
		return new F1<Integer, Integer>() {
			
			public Integer e(Integer obj) {
				return obj + add;
			}
		};
	}

	public static <A extends Comparable<A>> A min(A a1, A a2) {
		if (a1.compareTo(a2) > 0) {
			return a2;
		}
		return a1;
	}
	public static <A extends Comparable<A>> A max(A a1, A a2) {
		if (a1.compareTo(a2) < 0) {
			return a2;
		}
		return a1;
	}
	
	public static Integer minI(Collection<Integer> col) {
		if (col.isEmpty()) {
			return null;
		}
		
		int min = Integer.MAX_VALUE;
		for (Integer integer : col) {
			if (integer < min) {
				min = integer;
			}
		}
		
		return min;
	}

	public static <A> A min(List<A> list, F1<A,java.lang.Double> f1) {
		int index = -1;
		int minIndex = -1;
		double minDistance = 10000000;
		for (int from = 0, to = list.size(); ; ) {
			if (to<=from) break;
			
			index = ((to-from) / 2) + from;
			double result = f1.e(list.get(index));
	
			double absResult = Math.abs(result);
	
			if (absResult < minDistance) {
				minIndex = index;
				minDistance = absResult;
			}
			
			if (absResult < 5) {
				return list.get(index);
			} else if (result < 0) { // We are behind
				from = index + 1;
			} else { // result > 0
				to = index;
			}
		}
		if (minIndex > -1) {
			return list.get(minIndex);
		} else {
			return null;
		}
	}


	public static F0<Integer> counter(int start) {
		final int[] counter = {start};
		return new F0<Integer>() {public Integer e() {
			return counter[0]++;
		}};
	}
	public static F0<Boolean> counterBoolean(final int min) {
		final int[] counter = {0};
		return new F0<Boolean>() {public Boolean e() {
			return counter[0]++ >= min;
		}};
	}
	
	public static <A> Double sum(Iterable<A> col, F1<A, Double> f1) {
		double total = 0;
		for (A a : col) {
			total += f1.e(a);
		}
		return total;
	}
	public static <A> BigDecimal sumB(Iterable<A> col, F1<A, BigDecimal> f1) {
		BigDecimal total = new BigDecimal(0);
		for (A a : col) {
			total = total.add( f1.e(a) );
		}
		return total;
	}

	public static <A> long sumL(Iterable<A> col, F1<A, Long> f1) {
		long total = 0;
		for (A a : col) {
			total += f1.e(a);
		}
		return total;
	}
	public static <A> int sumI(Iterable<A> col, F1<A, Integer> f1) {
		int total = 0;
		for (A a : col) {
			total += f1.e(a);
		}
		return total;
	}


	public static P0 addF(final double val, final double[] ref) {
		return new P0() {public void e() {
			ref[0] += val;
		}};
	}


	public static qj.util.math.Rectangle merge(Collection<qj.util.math.Rectangle> rects) {
		int minX1 = Integer.MAX_VALUE;
		int minY1 = Integer.MAX_VALUE;
		int maxX2 = Integer.MIN_VALUE;
		int maxY2 = Integer.MIN_VALUE;
		for (qj.util.math.Rectangle rectangle : rects) {
			minX1 = Math.min(minX1, rectangle.x);
			minY1 = Math.min(minY1, rectangle.y);
			maxX2 = Math.max(maxX2, rectangle.x + rectangle.width);
			maxY2 = Math.max(maxY2, rectangle.y + rectangle.height);
		}
		return new qj.util.math.Rectangle(minX1, minY1, maxX2 - minX1, maxY2- minY1);
	}


	public static Double add(double add, Double oriVal) {
		if (oriVal==null) {
			return add;
		}
		return add + oriVal;
	}


	public static String percent(double d) {
		return "" + (int)(d * 100);
	}




	public static final F1<java.awt.Point, Point2D.Double> toPointDAwt = new F1<java.awt.Point, Point2D.Double>() {public Point2D.Double e(java.awt.Point p) {
		return new Point2D.Double(p.x, p.y);
	}};




	public static Polygon polygon(List<PointD> points) {
		Polygon polygon = new Polygon();
		for (PointD point : points) {
			polygon.addPoint(
					(int)Math.round(point.x), 
					(int)Math.round(point.y) 
					);
		}
		return polygon;
	}


	public static <A> F1<A,Integer> negativeF(final F1<A, Integer> f) {
		return new F1<A, Integer>() {public Integer e(A obj) {
			Integer i = f.e(obj);
			if (i==null) {
				return null;
			}
			return -i;
		}};
	}
	public static F1<BigDecimal,BigDecimal> negateF() {
		return new F1<BigDecimal, BigDecimal>() {public BigDecimal e(BigDecimal obj) {
			return obj.negate();
		}};
	}


	public static <A,T extends Comparable<T>> A max(List<A> col,
			F1<A, T> toNum) {
		T max = null;
		A maxVal = null;
		for (A a : col) {
			T num = toNum.e(a);
			if (maxVal == null || num.compareTo(max) > 0) {
				maxVal = a;
				max = num;
			}
		}
		return maxVal;
	}
	public static <A> A maxL(Iterable<A> col,
			F1<A, Long> toNum) {
		Long max = null;
		A maxVal = null;
		for (A a : col) {
			Long num = toNum.e(a);
			if (maxVal == null || num > max) {
				maxVal = a;
				max = num;
			}
		}
		return maxVal;
	}

	public static <A> A max(F1<A, Integer> toNum,
			A... col) {
		return max(Arrays.asList(col), toNum);
	}


	public static <K> void addDecimal(K key, BigDecimal value,
			Map<K, BigDecimal> map) {
		BigDecimal oldVal = map.get(key);
		if (oldVal == null) {
			map.put(key, value);
		} else {
			map.put(key, oldVal.add(value));
		}
	}
	public static <K> void addInteger(K key, int value,
			Map<K, Integer> map) {
		Integer oldVal = map.get(key);
		if (oldVal == null) {
			map.put(key, value);
		} else {
			map.put(key, oldVal.intValue() + value);
		}
	}


	/**
	 * Looking for Cols.repeat?
	 * @return
	 */
	@Deprecated
	public static F0<Integer> repeater() {
		return null;
	}

	
	public static void eachCirclePoint(PointD p, double diameterMin, double diameterMax, F1<qj.util.math.Point,Boolean> f) {
		int startX = (int) Math.floor(p.x - diameterMax);
		int startY = (int) Math.floor(p.y - diameterMax);
		
		int maxWidth = (int) Math.ceil(diameterMax * 2);
		int maxHeight = maxWidth;
		
		int innerWidth = (int) Math.floor(diameterMin * Math.sqrt(2));
		int innerHeight = innerWidth;
		int innerX = (int) Math.ceil(p.x - innerWidth / 2);
		int innerY = (int) Math.ceil(p.y - innerHeight / 2);
		qj.util.math.Rectangle innerRect = new qj.util.math.Rectangle(innerX, innerY, innerWidth, innerHeight);
		
		for (int x = startX; x < maxWidth + startX; x++) {
			for (int y = startY; y < maxHeight + startY; y++) {
				if (innerRect.contains(x, y)) {
					continue;
				}
				double distance = p.distance(x, y);
				if (distance > diameterMin && distance < diameterMax) {
					if (f.e(new qj.util.math.Point(x, y))) {
						return;
					}
				}
			}
		}
		
	}

	public static F1<BigDecimal, Boolean> lessF(int i) {
		final BigDecimal val = new BigDecimal(i);
		return new F1<BigDecimal, Boolean>() {public Boolean e(BigDecimal obj) {
			return obj.compareTo(val) < 0;
		}};
	}
	
	/**
	 * Note: only work if circle center is outside and only meet rect at 2 points
	 * @param rect
	 * @param circle
	 * @return
	 */
	public static double intersectArea(RectangleD rect, CircleD circle) {
		// If start point too far away: return
		PointD circleCenter = circle.center;
		PointD nearestCorner = rect.nearestCorner(circleCenter);
		if (nearestCorner.distance(circleCenter) >= circle.r) {
			return 0;
		}
		
		// If too near: add a*a
		PointD farthestCorner = rect.farthestCorner(circleCenter);
		if (farthestCorner.distance(circleCenter) <= circle.r) {
			return rect.width * rect.height;
		}
		
		// Intersect
		List<PointD> otherCorners = Cols.toList(rect.getCorners());
		otherCorners.remove(nearestCorner);
		otherCorners.remove(farthestCorner);
		PointD c0 = otherCorners.get(0);
		PointD c1 = otherCorners.get(1);
		boolean c0in = circle.isInside(c0);
		boolean c1in = circle.isInside(c1);
		if (!c0in && !c1in) {
			PointD meet0 = new LineD(nearestCorner, c0).meet1(circle);
			PointD meet1 = new LineD(nearestCorner, c1).meet1(circle);
			ArcD arc = circle.arc(meet0, meet1);
			
//			System.out.println("arc.areaMoon()=" + arc.areaMoon());
//			System.out.println("triangle=" + new TriangleD(nearestCorner, meet0, meet1).area());
			return arc.areaMoon() + new TriangleD(nearestCorner, meet0, meet1).area();
		} else if (c0in && c1in) {
			PointD meet0 = new LineD(c0, farthestCorner).meet1(circle);
			PointD meet1 = new LineD(c1, farthestCorner).meet1(circle);
			ArcD arc = circle.arc(meet0, meet1);
			return arc.areaMoon() + new PolygonD(nearestCorner, c0, meet0, meet1, c1).area();
		} else if (!c0in && c1in) {
			PointD meet0 = new LineD(nearestCorner, c0).meet1(circle);
			PointD meet1 = new LineD(c1, farthestCorner).meet1(circle);
			
			ArcD arc = circle.arc(meet0, meet1);
			return arc.areaMoon() + new PolygonD(nearestCorner, meet0, meet1, c1).area();
		} else {// if (c0in && !c1in) {
			PointD meet0 = new LineD(c0, farthestCorner).meet1(circle);
			PointD meet1 = new LineD(nearestCorner, c1).meet1(circle);
			
			ArcD arc = circle.arc(meet0, meet1);
			return arc.areaMoon() + new PolygonD(nearestCorner, c0, meet0, meet1).area();
		}
	}

	public static BigDecimal commonDivision(List<BigDecimal> nums) {
		
		for (;;) {
			if (nums.size() == 1) {
				return nums.get(0);
			}
			Collections.sort(nums);
			BigDecimal smallest = nums.get(0);
			for (int i = 1; i < nums.size(); i++) {
				if (!nums.get(i).remainder(smallest).equals(BigDecimal.ZERO)) {
					break;
				}
				if (i == nums.size() - 1) {
					return smallest;
				}
			}
			BigDecimal largest = nums.remove(nums.size() - 1);
			BigDecimal subtract = largest.subtract(nums.get(nums.size() - 1));
			if (!subtract.equals(BigDecimal.ZERO)) {
				nums.add(subtract);
			}
		}
	}
	
	public static BigInteger gcd(Iterable<BigInteger> nums) {
		BigInteger ret = null;
		for (BigInteger num : nums) {
			if (ret == null) {
				ret = num;
			} else {
				ret = ret.gcd(num);
			}
		}
		return ret;
	}
	
	public static BigInteger lcm(BigInteger... nums) {
		return lcm(Arrays.asList(nums));
	}
	
	public static BigInteger lcm(Iterable<BigInteger> nums) {
		BigInteger ret = null;
		for (BigInteger num : nums) {
			if (ret == null) {
				ret = num;
			} else {
				ret = lcm(ret, num);
			}
		}
		return ret;
	}
	
	public static BigInteger lcm(BigInteger num1, BigInteger num2) {
		return num1.multiply(num2.divide(num1.gcd(num2)));
	}
	
	public static List<BigInteger> toPrimes(long num) {
		return toPrimes(BigInteger.valueOf(num));
	}
	
	public static List<BigInteger> toPrimes(BigInteger num) {
		LinkedList<BigInteger> ret = new LinkedList<BigInteger>();
		F0<BigInteger> primeF = primeF();
		for (BigInteger prime = primeF.e();prime.compareTo(num) <=0;) {
			if (num.remainder(prime).equals(BigInteger.ZERO)) {
				ret.add(prime);
				
				num = num.divide( prime );
//				System.out.println("divided by " + prime + " new num: " + num);
			} else {
				prime = primeF.e();
			}
		}
		return ret;
	}
	
	public static void main(String[] args) {
		toPrimes(BigInteger.valueOf(211658986894L));
	}
	
	static ArrayList<BigInteger> primes = new ArrayList<BigInteger>(Arrays.asList(
			BigInteger.valueOf(2), 
			BigInteger.valueOf(3),
			BigInteger.valueOf(5), 
			BigInteger.valueOf(7), 
			BigInteger.valueOf(11)));
	private static F0<BigInteger> primeF() {
		final int[] index = {0};
		return new F0<BigInteger>() {public BigInteger e() {
			int i = index[0]++;
			if (i == primes.size()) {
				BigInteger nextPrime = genNextPrime();
				primes.add(nextPrime);
				return nextPrime;
			} else {
				return primes.get(i);
			}
		}};
	}
	protected static BigInteger genNextPrime() {
		L1:
		for (BigInteger i = primes.get(primes.size() - 1); ; i=i.add(BigInteger.ONE)) {
			for (BigInteger prime : primes) {
				if (i.remainder(prime).equals(BigInteger.ZERO)) {
					continue L1;
				}
			}
			return i;
		}
	}
	
	@SuppressWarnings("unchecked")
	static ArrayList<List<BigDecimal>> fibonacci = new ArrayList<List<BigDecimal>>(Arrays.asList(Arrays.asList(BigDecimal.ONE)));
	public static List<BigDecimal> getFibonacci(int i) {
		if (i < fibonacci.size()) {
			return fibonacci.get(i);
		}
		
		for (int j = fibonacci.size();j<=i+1;j++) {
			List<BigDecimal> lastLine = fibonacci.get(j-1);
			ArrayList<BigDecimal> line = new ArrayList<BigDecimal>(lastLine.size() + 1);
			line.add(BigDecimal.ONE);
			for (int k = 1; k < lastLine.size(); k++) {
				line.add(lastLine.get(k-1).add(lastLine.get(k)));
			}
			line.add(BigDecimal.ONE);
			fibonacci.add(line);
		}
		return fibonacci.get(i);
	}
	
	public static <A extends Comparable<A>> A minOf(P1<P1<A>> p1) {
		final AtomicReference<A> min = new AtomicReference<A>();
		p1.e(new P1<A>() {public void e(A obj) {
			if (min.get() == null || min.get().compareTo(obj) > 0) {
				min.set(obj);
			}
		}});
		return min.get();
	}
	public static <A extends Comparable<A>, B> Douce<A,B> minOf2(P1<P2<A,B>> p2) {
		final AtomicReference<A> min = new AtomicReference<A>();
		final AtomicReference<B> val = new AtomicReference<B>();
		p2.e(new P2<A,B>() {public void e(A a, B b) {
			if (min.get() == null || min.get().compareTo(a) > 0) {
				min.set(a);
				val.set(b);
			}
		}});
		return new Douce<>(min.get(), val.get());
	}

	public static BigDecimal add(BigDecimal... bigs) {
		BigDecimal total = BigDecimal.ZERO;
		for (BigDecimal big : bigs) {
			if (big != null) {
				total = total.add(big);
			}
		}
		return total;
	}

	public static BigDecimal subtract(BigDecimal b1, BigDecimal b2) {
		if (b1 == null) {
			b1 = BigDecimal.ZERO;
		}
		if (b2 == null) {
			b2 = BigDecimal.ZERO;
		}

		return b1.subtract(b2);
	}
}
