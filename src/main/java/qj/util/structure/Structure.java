package qj.util.structure;

import java.util.*;
import java.util.Map.Entry;

import qj.tool.logicdb.LogicList;
import qj.tool.logicdb.StringIndex;
import qj.util.Cols;
import qj.util.MathUtil;
import qj.util.ObjectUtil;
import qj.util.RegexUtil.Group;
import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P1;
import qj.util.math.Range;
import qj.util.math.Ranges;

/**
 * LogicDB
 * @author QuanLA
 *
 */
@SuppressWarnings("rawtypes")
public class Structure {

	public final Map<Object, List<TagRange>> allRanges;
	public final String text;
	public final HashMap<Class, StringIndex<TagRange>> stringIndexes;
	public final HashMap<Object, LayerList> layerLists;

	public Structure(Map<Object, List<TagRange>> allRanges, 
			HashMap<Class, StringIndex<TagRange>> stringIndexes,
			HashMap<Object, LayerList> layedLists,
			String text) {
		this.allRanges = allRanges;
		this.stringIndexes = stringIndexes;
		this.layerLists = layedLists;
		this.text = text;
	}

	public Structure(Structure structure) {
		this(structure.allRanges,
				structure.stringIndexes,
				structure.layerLists,
				structure.text);
	}
	
	public IterateBuilder iterate(Object... markups) {
		return new IterateBuilder(markups, this);
	}

	public List<TagRange> getRanges(Object marker) {
		return getRanges(marker, allRanges);
	}
	public LayerList getLayerList(Object marker) {
		return getRanges(marker, layerLists);
	}

	public static <A> A getRanges(Object marker, Map<Object, A> allRanges) {
		
		if (allRanges.containsKey(marker)) {
			return allRanges.get(marker);
		}
		
		A rangeByClass = getRangeByClass(marker instanceof Class ? (Class)marker : marker.getClass(),allRanges);
		if (rangeByClass != null) {
			return rangeByClass;
		}
		return allRanges.get(null);
	}
	
	public void eachRanges(Object marker, P1<List<TagRange>> p) {
		Class<?> clazz;
		if (!(marker instanceof Class)) {
			if (allRanges.containsKey(marker)) {
				p.e(allRanges.get(marker));
			}
			clazz = marker.getClass();
		} else {
			clazz = (Class<?>) marker;
		}
		while (true) {
			List<TagRange> trs = allRanges.get(clazz);
			if (trs != null) {
				p.e(trs);
			}
			clazz = clazz.getSuperclass();
			if (clazz == null) {
				return;
			}
		}
	}

	private static <A> A getRangeByClass(Class<?> class1, Map<Object, A> allRanges) {
		if (class1==null) {
			return null;
		}
		A rangeByClass = allRanges.get(class1);
		if (rangeByClass != null) {
			return rangeByClass;
		}
		return getRangeByClass(class1.getSuperclass(), allRanges);
	}
	
//	public static void main(String[] args) {
//		HashMap<Object, String> ar = new HashMap<Object, String>();
//		ar.put(Object.class, "aaa aaa");
//		System.out.println(getRanges(String.class, ar));
//	}

	public Range getRange(Object tagCond) {
		TagRange tagRange = getTagRange(tagCond);
		return tagRange == null ? null : tagRange.range;
	}

	public boolean hasTag(Object tagCond) {
		return getTagRange(tagCond) != null;
	}
	
	
	public Range getRange(Object tagCond, int pos) {
		TagRange tagRange = getTagRange(tagCond, pos);
		return tagRange != null ? tagRange.range : null;
	}

	@SuppressWarnings("unchecked")
	public <A> Collection<A> getTags(final Class<A> tagCond, int pos) {
		final LinkedList<A> tags = new LinkedList<A>();
		
		eachTagRange(tagCond, new Range(pos,pos), new F1<TagRange, Boolean>() {public Boolean e(TagRange tr) {
			tags.add((A) tr.tag);
			return false;
		}});
		
		return tags;
	}

	@SuppressWarnings("unchecked")
	public <A> Collection<A> getTags(final Class<A> tagCond, Range range) {
		final LinkedList<A> tags = new LinkedList<A>();
		
		eachTagRange(tagCond, range, new F1<TagRange, Boolean>() {public Boolean e(TagRange tr) {
			tags.add((A) tr.tag);
			return false;
		}});
		
		return tags;
	}

	public Object getTag(Object tagCond, Range range) {
		TagRange tr = getTagRange(tagCond, range);
		return tr != null ? tr.tag : null;
	}

	public Range getRange(Object tagCond, Range range) {
		TagRange tr = getTagRange(tagCond, range);
		return tr != null ? tr.range : null;
	}
	
	public Collection<TagRange> getTagRanges(Object tagCond, Range range) {
		F1<Range, Integer> distanceF = MathUtil.distanceF(range);
		
		return getTagRanges(tagCond, distanceF);
	}
	public TagRange getTagRange(Object tagCond, Range range) {
		F1<Range, Integer> distanceF = MathUtil.distanceF(range);

		return getTagRange(tagCond, distanceF);
	}
	public TagRange getTagRangeOverlap(Object tagCond, final Range range) {
		F1<Range, Integer> distanceF = new F1<Range, Integer>() {public Integer e(Range range1) {
			int distance = MathUtil.distance(range1, range);
			if (distance==0) {
				if (ObjectUtil.equals(range1.getFrom(), range.getTo())) {
					return 1;
				} else if (ObjectUtil.equals(range1.getTo(), range.getFrom())) {
					return -1;
				}
			}
			return distance;
		}};

		return getTagRange(tagCond, distanceF);
	}

	private Collection<TagRange> getTagRanges(Object tagCond, F1<Range, Integer> distanceF) {
		// TODO not implemented
//		LayerList layerList = getLayerList(tagCond);
//		if (layerList!=null) {
//			return layerList.getTagRange(tagCond, distanceF);
//		}
		
		return TagRanges.getTagRanges(tagCond, distanceF, getRanges(tagCond));
	}
	private TagRange getTagRange(Object tagCond, F1<Range, Integer> distanceF) {
		LayerList layerList = getLayerList(tagCond);
		if (layerList!=null) {
			return layerList.getTagRange(tagCond, distanceF);
		}
		
		return getTagRange(tagCond, distanceF, getRanges(tagCond));
	}

	@SuppressWarnings("unchecked")
	public <A> A getTag(Object clazz, int pos) {
		TagRange tr = getTagRange(clazz, pos);
		return tr !=null ? (A) tr.tag : null;
	}

	public TagRange getTagRange(int pos, Object... conds ) {
		for (Object cond : conds) {
			TagRange tagRange = getTagRange(cond, pos);
			if (tagRange != null) {
				return tagRange;
			}
		}
		return null;
	}


	public TagRange getTagRange(Object cond, int pos) {
		LayerList layerList = getLayerList(cond);
		if (layerList!=null) {
			F1<Range, Integer> distanceF = MathUtil.distanceF(pos);
			return layerList.getTagRange(cond, distanceF);
		}
		return getTagRange(cond, pos, getRanges(cond));
	}
	
	//***
	public static TagRange getTagRange(Object clazz, int pos,
			final List<TagRange> ranges) {
		F1<Range, Integer> distanceF = MathUtil.distanceF(pos);

		return getTagRange(clazz, distanceF, ranges);
	}


	public Structure insertPoses(ArrayList<Integer> insertedPoses) {
		final HashMap<Object, List<TagRange>> allRanges1 = new HashMap<Object, List<TagRange>>();
		for (Entry<Object, List<TagRange>> entry : allRanges.entrySet()) {
			allRanges1.put(entry.getKey(), insertPoses(insertedPoses, entry.getValue()));
		}

		final HashMap<Object, LayerList> layerLists = new HashMap<Object, LayerList>();
		for (Entry<Object, LayerList> entry : this.layerLists.entrySet()) {
			layerLists.put(entry.getKey(), entry.getValue().insertPoses(insertedPoses));
		}
		
		return new Structure(allRanges1, stringIndexes, layerLists, text);
	}

	public static ArrayList<TagRange> insertPoses(List<Integer> insertedPoses,
			List<TagRange> ranges) {
		ArrayList<TagRange> ranges1 = new ArrayList<TagRange>(ranges.size());
		for (TagRange tagRange : ranges) {
			Range range1 = tagRange.range;
			for (Integer pos : insertedPoses) {
				range1.shiftRightOf(pos);
			}
			ranges1.add(new TagRange(tagRange.tag, range1));
		}
		return ranges1;
	}

	public String getValue(Range range) {
		if (range == null) {
			return null;
		}
		if (range.getTo() <= text.length()) {
			return text.substring(range.getFrom(), range.getTo());
		} else {
			return null;
		}
	}

	/**
	 * Get range by pointer
	 * @param tag
	 * @return 
	 */
	public Range getRangeP(Object tag) {
		for (TagRange tagRange : getRanges(tag)) {
			if (tagRange.tag==tag ) {
				return tagRange.range;
			}
		}
		return null;
	}

	public boolean has(Class<?> class1, final Range tokenRange) {
		final boolean[] has = {false};
		eachTagRange(class1, tokenRange, new F1<TagRange, Boolean>() {public Boolean e(TagRange obj) {
			if (!MathUtil.conflict(obj.range, tokenRange)) {
				return false;
			}
			
			has[0] = true;
			return true;
		}});
		return has[0];
	}

	public void eachRangeNot(Class<?> tagCond, P1<Range> p) {
		Ranges.eachRangeNot(Cols.iterable(getRanges(tagCond), TagRange.rangeF), text.length(), p);
	}
	public void eachRangeNot(Class[] tagConds, P1<Range> p) {
		LinkedList<Iterable<Range>> allRangeList = new LinkedList<Iterable<Range>>();
		for (Class tagCond : tagConds) {
			List<TagRange> tagRanges = getRanges(tagCond);
			
			allRangeList.add(Cols.yield(tagRanges, TagRange.rangeF));
		}
		Ranges.eachRangeNot(Cols.sequenceSorted(allRangeList), text.length(), p);
	}
	public void eachTagRangeSorted(Class[] tagConds, P1<TagRange> p) {
		LinkedList<Iterable<TagRange>> allRangeList = new LinkedList<Iterable<TagRange>>();
		for (Class tagCond : tagConds) {
			List<TagRange> tagRanges = getRanges(tagCond);
			
			allRangeList.add(tagRanges);
		}
		Cols.each(Cols.sequenceSorted(allRangeList), p);
	}

	/**
	 * Go backward
	 * @param target
	 * @param from
	 * @param not
	 * @param p1
	 */
	public void eachRangeBefore(Object target, Integer from, Object not, P1<Range> p1) {
		F1<Object, Boolean> targetTest = LogicList.tagCond(target);
		F1<Object, Boolean> notTest = LogicList.tagCond(not);
		
		List<TagRange> ranges = getRanges(target);
		boolean started = false;
		// TODO Binary search
		for (int i = ranges.size() - 1; i > -1; i--) {
			TagRange tagRange = ranges.get(i);
			if (!started) {
				if (MathUtil.in(from, tagRange.range)) {
					if (targetTest.e(tagRange.tag)) {
						started = true;
						p1.e(tagRange.range);
					} else if (notTest.e(tagRange.tag)) {
						started = true;
					}
				}
			} else {
				if (targetTest.e(tagRange.tag)) {
					p1.e(tagRange.range);
				} else if (notTest.e(tagRange.tag)) {
					return;
				}
			}
		}
	}


	public void eachRange(Object cond, final P1<Range> p1) {
		eachTagRange(cond, new P1<TagRange>() {public void e(TagRange obj) {
			p1.e(obj.range);
		}});
	}
	
	@SuppressWarnings("unchecked")
	public <A> void eachTag(Class<A> clazz, final P1<A> p1) {
		eachTagRange(clazz, new P1<TagRange>() {public void e(TagRange obj) {
			p1.e((A) obj.tag);
		}});
	}
	
	public void eachTagRange(Object cond, P1<TagRange> p1) {
		eachTagRange(cond, Fs.f1(p1, false));
	}
	
	public void eachRange(final Class<?> class1, final Range inRange, final P1<Range> p1) {
		eachTagRange(class1, inRange, new F1<TagRange, Boolean>() {public Boolean e(TagRange tagRange) {
			p1.e(tagRange.range);
			return false;
		}});
	}
	
	public <A> Collection<A> getTags(Class<A> clazz) {
		final LinkedList<A> ret = new LinkedList<A>();
		
		eachTagRange(clazz, new P1<TagRange>() {
			@SuppressWarnings("unchecked")
			public void e(TagRange obj) {
				ret.add((A) obj.tag);
			}
		});
		return ret;
	}
	
	public TagRange lowestTagRange(Class<?> clazz, Range range) {
		final TagRange[] ret = {null};
		eachTagRange(clazz, range, new F1<TagRange,Boolean>() {public Boolean e(TagRange tr) {
			ret[0] = tr;
			return true;
		}});
		return ret[0];
	}
	
	@SuppressWarnings("unchecked")
	public <A> A nextTag(Class<A> tagClass, Integer pos) {
		TagRange tr = nextTagRange(tagClass, pos);
		return tr != null ? (A)tr.tag : null;
	}

	public Range lastRange(Object marker, int from) {
		TagRange lastTagRange = lastTagRange(marker, from);
		return lastTagRange == null ? null : lastTagRange.range;
	}
	public Range nextRange(Object marker, int from) {
		TagRange nextTagRange = nextTagRange(marker, from);
		return nextTagRange == null ? null : nextTagRange.range;
	}

	public String toString() {
		return "Layers:\n" + StructureHelper.toString(this.layerLists) + "\n---\n" + StructureHelper.toString(this.allRanges);
	}

	public Structure only(Class<Group> class1) {
		Map<Object, List<TagRange>> map = Cols.map(class1, getRanges(class1));
		return new Structure(map, stringIndexes, layerLists, text);
	}

	

	// ***
	/**
	 * Find single tr with same tag
	 */
	public TagRange getTagRange(Object tagCond) {
		StringIndex<TagRange> stringIndex = stringIndexes.get(tagCond.getClass());
		if (stringIndex!=null) {
			// Use index
			return stringIndex.get(stringIndex.keyF.e(new TagRange(tagCond, null)));
		}
		
		LayerList layerList = getLayerList(tagCond);
		if (layerList != null) {
			return layerList.getTagRange(tagCond);
		}

		F1<Object, Boolean> t = LogicList.tagCond(tagCond);
		for (TagRange tagRange : getRanges(tagCond, allRanges)) {
			if (t.e(tagRange.tag)) {
				return tagRange;
			}
		}
		return null;
	}
	
	//***
	public List<TagRange> getTagRanges(final Class<?> tagCond) {
//		LayerList layerList = getLayerList(tagCond);
//		if (layerList!= null) {
//			return layerList.getTagRanges(tagCond);
//		}
		
		List<TagRange> ranges = getRanges(tagCond);
		ArrayList<TagRange> result = new ArrayList<TagRange>();
		for (TagRange tagRange : ranges) {
			if (tagCond.isInstance(tagRange.tag)) {
				result.add(tagRange);
			}
		}
		return result;
	}
	//***
	public static TagRange getTagRange(Object clazz,
			F1<Range, Integer> distanceF, final List<TagRange> ranges) {
		final F1<Object, Boolean> f = LogicList.tagCond(clazz);
		
		final TagRange[] retRef = {null};

		Cols.eachZeroDistances(
				distanceF, 
				Cols.randomAccessCol(ranges, TagRange.rangeF), 
				new F1<Integer, Boolean>() {public Boolean e(Integer index) {
					TagRange found = ranges.get(index);
					if (f.e(found.tag)) {
						retRef[0] = found;
						return true;
					} else {
						return false;
					}
				}});
		return retRef[0];
	}
	//***
	public void eachTagRange(Object cond, F1<TagRange, Boolean> f1) {
		LayerList layerList = getLayerList(cond);
		if (layerList!=null) {
			layerList.eachTagRange(f1);
			return;
		}
		
		F1<Object, Boolean> f = LogicList.tagCond(cond);
		
		for (TagRange tagRange : getRanges(cond)) {
			if (f.e(tagRange.tag)) {
				if (f1.e(tagRange)) return;
			}
		}
	}


	/**
	 * Not by order
	 * @param class1
	 * @param inRange
	 * @param f1
	 */
	public void eachTagRange(final Class<?> class1, final Range inRange, final F1<TagRange, Boolean> f1) {
		LayerList layerList = getLayerList(class1);
		if (layerList!=null) {
			layerList.eachTagRange(inRange, f1);
			return;
		}
		
		StructureHelper.eachTagRange_sorted(MathUtil.distanceF(inRange), getRanges(class1), new F1<TagRange, Boolean>() {public Boolean e(TagRange tagRange) {
			if (class1.isInstance(tagRange.tag)) {
				return f1.e(tagRange);
			}
			return false;
		}});
	}
	
	public void eachTagRange( Class<?> class1, Range inRange, P1<TagRange> p, P1<Range> notRangeP) {
		ArrayList<TagRange> trs = new ArrayList<TagRange>();
		eachTagRange(class1, inRange, Fs.<TagRange,Boolean>f1(Fs.store(trs), false));
		Collections.sort(trs);
		
		int index = inRange.getFrom();
		for (TagRange tr : trs) {
			if (tr.range.getFrom() > index) {
				notRangeP.e(new Range(index, tr.range.getFrom()));
			}
			
			p.e(tr);
			index = tr.range.getTo();
		}
		
		if (index < inRange.getTo()) {
			notRangeP.e(new Range(index, inRange.getTo()));
		}
	}

	//***
	public TagRange nextTagRange(Object marker, int from) {
		List<TagRange> ranges = getRanges(marker);
		F1<Object, Boolean> f = LogicList.tagCond(marker);
		
		int index = Cols.searchIndexedBinary(ranges, TagRange.rangeF, new Range(from, from));
		if (index < 0) {
			index = -index -2;
		}
			
		for (int i = index + 1; i < ranges.size(); i++) {
			TagRange tagRange = ranges.get(i);
			if (f.e(tagRange.tag)) {
				return tagRange;
			}
		}
		
		return null;
	}


	//***
	public TagRange lastTagRange(Object marker, int from) {
		List<TagRange> ranges = getRanges(marker);
		F1<Object, Boolean> f = LogicList.tagCond(marker);

		int index = Cols.searchIndexedBinary(ranges, TagRange.rangeF, new Range(from, from));
		if (index < 0) {
			index = -index -1;
		}
		
		if (index > 0) {
			
			for (int i = index - 1; i > -1; i--) {
				TagRange tagRange = ranges.get(i);
				if (f.e(tagRange.tag)) {
					return tagRange;
				}
			}
			
			return null;
		} else {
			return null;
		}
	}


	public Collection<TagRange> getTagRanges(int pos) {
		return null;
	}
}
