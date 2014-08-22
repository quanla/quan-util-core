package qj.util.structure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import qj.tool.logicdb.LogicList;
import qj.tool.logicdb.StringIndex;
import qj.util.Cols;
import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P1;
import qj.util.math.Range;

@SuppressWarnings("rawtypes")
public class StructureBuilder extends Structure {

	LinkedList<TagRange> pendings = new LinkedList<TagRange>();
	Map<Object, P1> onEnds = new HashMap<Object, P1>();
	
	public StructureBuilder(String text) {
		super(new HashMap<Object, List<TagRange>>(),
				new HashMap<Class, StringIndex<TagRange>>(),
				new HashMap<Object, LayerList>(),
				text);
		allRanges.put(null, new ArrayList<TagRange>());
	}
	
	public StructureBuilder(Structure structure) {
		super(structure.allRanges,
				structure.stringIndexes,
				structure.layerLists,
				structure.text);
	}


	private void index(TagRange tagRange) {
		StringIndex<TagRange> stringIndex = stringIndexes.get(tagRange.tag.getClass());
		if (stringIndex!= null) {
			stringIndex.accept(tagRange);
		}
	}

	public TagRange fromto(int from, int to, Object tag) {
		TagRange tagRange = new TagRange(tag,new Range(from,to));
		
		return addTagRangeDirectly(tagRange);
//		index = to;
	}

	//***
	public TagRange addTagRangeDirectly(final TagRange tr) {
		if (!layer(tr)) {
			eachRanges(tr.tag,  new P1<List<TagRange>>() {public void e(List<TagRange> trs) {
				trs.add(tr);
			}});
		}
		
		index(tr);
		return tr;
	}

	public void addTagRange(TagRange tr) {
		if (!layer(tr)) {
			List<TagRange> ranges = getRanges(tr.tag); // TODO
			int index = Cols.searchIndexedBinary(ranges, TagRange.rangeF, tr.range);
			if (index < 0) {
				index = -index -1;
			}
			ranges.add(index, tr);
		}
		
		index(tr);
		
	}
	
	private boolean layer(TagRange tr) {
		LayerList layerList = layerLists.get(tr.tag.getClass());
		if (layerList == null) {
			return false;
		}
		
		layerList.add(tr);
		return true;
	}

	public void range(Range range, Object marker) {
		TagRange tagRange = new TagRange(marker,range);
		addTagRangeDirectly(tagRange);
	}
	public void rangeWSort(Range range, Object tag) {
		if (range==null) {
			throw new IllegalArgumentException("range is null");
		}
		TagRange tr = new TagRange(tag,range);
		addTagRange(tr);
	}


	public TagRange fromplus(int from, int length, Object marker) {
		int to = from + length;
		return fromto(from, to, marker);
	}

	public String lastValue(Object marker) {
		List<TagRange> ranges = getRanges(marker);
		F1<Object, Boolean> f = LogicList.tagCond(marker);
		for (int i = ranges.size() - 1; i > -1; i--) {
			TagRange tagRange = ranges.get(i);
			if (f.e(tagRange.tag)) {
				return text.substring(tagRange.range.getFrom(), tagRange.range.getTo());
			}
		}
		throw new RuntimeException();
	}

	public Range lastRange(Object marker) {
		List<TagRange> ranges = getRanges(marker);
		F1<Object, Boolean> f = LogicList.tagCond(marker);
		for (int i = ranges.size() - 1; i > -1; i--) {
			TagRange tagRange = ranges.get(i);
			if (f.e(tagRange.tag)) {
				return tagRange.range;
			}
		}
		throw new RuntimeException();
	}

	public void end(int end) {
		end(Fs.f1(true), end);
	}

	public Structure build() {
		end();
		
		return build(Structure.class);
	}

	public <A extends Structure> A build(Class<A> clazz) {
		end();
		try {
			Constructor<A> constructor = clazz.getConstructor(Structure.class);
			return constructor.newInstance(this);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean in(Object marker) {
		
		F1<Object, Boolean> t = LogicList.tagCond(marker);
		for (TagRange tagRange : pendings) {
			if (t.e(tagRange.tag)) {
				return true;
			}
		}
		return false;
	}
	public TagRange getPending(Object marker) {
		
		F1<Object, Boolean> t = LogicList.tagCond(marker);
		
		Iterator<TagRange> it = pendings.descendingIterator();
		while (it.hasNext()) {
			TagRange tagRange = it.next();
			if (t.e(tagRange.tag)) {
				return tagRange;
			}
		}
		return null;
	}

	public void into(Object marker, int start) {
		pendings.add(new TagRange(marker, new Range(start, null)));
	}

	public void end(Object marker, int end) {
		F1<Object, Boolean> t = LogicList.tagCond(marker);
		end(t, end);
	}

	private void end(F1<Object, Boolean> t, int end) {
		Iterator<TagRange> it = pendings.descendingIterator();
		while (it.hasNext()) {
			TagRange tagRange = it.next();
			if (t.e(tagRange.tag)) {
				it.remove();
				tagRange.range.setTo(end);
				addTagRange(tagRange);
				onEnd(tagRange);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void onEnd(TagRange tagRange) {
		P1 onEnd = getRanges(tagRange.tag, onEnds);
		if (onEnd != null) {
			onEnd.e(tagRange.tag);
		}
	}

	public TagRange endSingle(Object marker, int end) {
		F1<Object, Boolean> t = LogicList.tagCond(marker);
		Iterator<TagRange> it = pendings.descendingIterator();
		while (it.hasNext()) {
			TagRange tagRange = it.next();
			if (t.e(tagRange.tag)) {
				it.remove();
				tagRange.range.setTo(end);
				addTagRange(tagRange);
				onEnd(tagRange);
				return tagRange;
			}
		}
		return null;
	}

	public void add(Structure structure, Integer index) {
		for (Entry<Object, LayerList> entry : structure.layerLists.entrySet()) {
			getLayerList(entry.getKey()).add(entry.getValue(), index);
		}
		
		for (List<TagRange> list : structure.allRanges.values()) {
			for (TagRange tagRange : list) {
				TagRange newM = new TagRange(
						tagRange.tag, 
						tagRange.range.shiftRight(index));
				getRanges(tagRange.tag).add(newM);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getTag(Object clazz, int pos) {
		TagRange tagRange = Structure.getTagRange(clazz, pos, pendings);
		if (tagRange !=null) {
			return (A) tagRange.tag;
		} else {
			return super.getTag(clazz, pos);
		}
	}
	
	public String peek(int length, int from) {
		return text.substring(from, Math.min(text.length(), from + length));
	}

	public void changeTo(Object marker, int start) {
		Object currentToken = this.getTag(marker.getClass(), start);
		if (currentToken!=null && !currentToken.equals(marker)) {
			this.end(currentToken, start);
		}
		if (!this.in(marker)) {
			this.into(marker, start);
		}
	}

	public void end() {
		end(text.length());
	}
	
	static class SubStructure {
		final F1<Object, Boolean> testF;
		final F1<Range, Structure> extractF;

		public SubStructure(F1<Object, Boolean> testF,
				F1<Range, Structure> extractF) {
			this.testF = testF;
			this.extractF = extractF;
		}
		
	}

	public void addAll(Collection<TagRange> ranges) {
		for (TagRange tagRange : ranges) {
			getRanges(tagRange.tag).add(tagRange); // TODO
		}
	}
	
	public void cacheSeparate(Object... tags) {
		ArrayList<TagRange> list = new ArrayList<TagRange>();
		for (Object tag : tags) {
			if (!allRanges.containsKey(tag)) {
				allRanges.put(tag, list);
			}
		}
	}

	public <A> void indexString(Class<A> class1, final F1<A, String> keyF) {
		if (!stringIndexes.containsKey(class1)) {
			stringIndexes.put(class1, new StringIndex<TagRange>(new F1<TagRange, String>() {
				@SuppressWarnings("unchecked")
				public String e(TagRange obj) {
					return keyF.e((A) obj.tag);
				}
			}));
		}
	}

	public void sort(Class<?> tagCond) {
		Cols.sort(getRanges(tagCond), TagRange.rangeF);
	}

	public void layered(Class<?> class1) {
		if (!layerLists.containsKey(class1)) {
			layerLists.put(class1, new LayerList());
		}
	}

	public void onEnd(Object clazz, P1<?> p1) {
		onEnds.put(clazz, p1);
	}

}
