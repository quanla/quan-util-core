package qj.util.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import qj.tool.logicdb.LogicList;
import qj.util.Cols;
import qj.util.Cols.RandomAccessCol;
import qj.util.MathUtil;
import qj.util.funct.F1;
import qj.util.math.Range;

public class LayerList {
	public LinkedList<ArrayList<TagRange>> layers;
	
	public LayerList() {
		layers = new LinkedList<ArrayList<TagRange>>();
		layers.add(new ArrayList<TagRange>());
	}
	
	protected LayerList(LinkedList<ArrayList<TagRange>> ranges2) {
		this.layers = ranges2;
	}

	public void add(TagRange tr) {
		int result = attemptAdd(tr);
		if (result > -1) {
			// 1. Shift up
			ArrayList<TagRange> highestLayer = layers.getLast();
			int index = Cols.search(highestLayer.size(), Cols.disF(MathUtil.distanceF(tr.range), Cols.randomAccessCol(highestLayer, TagRange.rangeF)));
//			int index = Cols.indexedBinarySearch(highestLayer, TagRange.rangeF, tr.range);
			if (index > -1 && MathUtil.conflict(highestLayer.get(index).range, tr.range)) {
				layers.add(new ArrayList<TagRange>());
			}
			
			Iterator<ArrayList<TagRange>> it = layers.descendingIterator();
			ArrayList<TagRange> upperLayer = it.next(); // i - 1
			int insertPoint = 0;
			for(int i = layers.size() - 2;i>=result;i--) {
				insertPoint = shiftup(tr.range, upperLayer, it.next(), insertPoint);
			}
			
			// 2. Insert
			layers.get(result).add(insertPoint, tr);
		} // result == -1: ok to go
	}
	
	

	private int shiftup(final Range range, ArrayList<TagRange> upperLayer,
			final ArrayList<TagRange> layer, int insertPoint) {
		final int[] minIndex = {Integer.MAX_VALUE};
		final int[] count = {0};
		
		Cols.eachZeroDistances(MathUtil.distanceF(range), Cols.randomAccessCol(layer, TagRange.rangeF), new F1<Integer, Boolean>() {public Boolean e(Integer obj) {

			TagRange tagRange = layer.get(obj);
			if (MathUtil.conflict(range, tagRange.range)) {
				count[0] ++;
				minIndex[0] = Math.min(minIndex[0], obj);
			}
			
			return false;
		}});

		if (count[0] > 0) {
			for (int i = count[0] - 1; i > -1; i--) {
				TagRange remove = layer.remove(minIndex[0] + i);
				upperLayer.add(insertPoint, remove);
			}
			return minIndex[0];
		} else {
			return 0;
		}
	}

	private int attemptAdd(final TagRange tr) {
		int i = -1;
		for (final ArrayList<TagRange> ranges : layers) {			
			i++;
			
			final int[] found = {0};
			
			RandomAccessCol<Range> randomAccessCol = Cols.randomAccessCol(ranges, TagRange.rangeF);
			F1<Range, Integer> distanceF = MathUtil.distanceF(tr.range);
			
			Cols.eachZeroDistances(distanceF, randomAccessCol, new F1<Integer, Boolean>() {public Boolean e(Integer i) {
				TagRange tagRange = ranges.get(i);
				if (!MathUtil.conflict(tr.range, tagRange.range)) {
					found[0] = 1; // found, but not conflict
				} else {
					// Conflict
					if (tr.range.size() > tagRange.range.size()) {
						found[0] = 3;
					} else {
						found[0] = 2;
					}
				}
				return found[0] > 1;
			}});
			
			if (found[0]==0) {
				int index = Cols.searchIndexedBinary(ranges, TagRange.rangeF, tr.range);
				ranges.add(-index - 1, tr);
//				System.out.println("Not found, adding");
				return -1;
			} else if (found[0]==1) { // Found but non conflict
				int index = Cols.search(ranges.size(), Cols.disF(distanceF, randomAccessCol));
				// Non conflicting, ok to add
//				System.out.println("Found Non conflict");
				ranges.add(tr.range.getTo() == ranges.get(index).range.getFrom() ? index : index + 1, tr);
				return -1;
			} else if (found[0]==2) { // Conflict move on
//				System.out.println("Conflict moveon");
			} else {
//				System.out.println("Conflict insert");
				return i;
			}
		}
		
		// Not enough list, add more
		ArrayList<TagRange> newList = new ArrayList<TagRange>();
		newList.add(tr);
		layers.add(newList);
//		System.out.println("Add to new list");
		return -1;
	}

	/**
	 * Look top down, if found, quit immediately
	 * @param tagCond
	 * @param distanceF
	 * @return
	 */
	public TagRange getTagRange(
			Object tagCond,
			final F1<Range, Integer> distanceF) {
		final F1<Object, Boolean> f = LogicList.tagCond(tagCond);
		Iterator<ArrayList<TagRange>> it = layers.descendingIterator();
		final TagRange[] ret ={null};
		while (it.hasNext()) {
			final ArrayList<TagRange> ranges = it.next();
			Cols.eachZeroDistances(distanceF, Cols.randomAccessCol(ranges, TagRange.rangeF), new F1<Integer, Boolean>() {
				public Boolean e(Integer index) {
					TagRange tr = ranges.get(index);
					if (f.e(tr.tag)) {
						ret[0] = tr;
						return true;
					}
					return false;
				}
			});
			
			if (ret[0] != null) {
				return ret[0];
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		
		LayerList layerList = new LayerList();
		layerList.add(new TagRange("g", new Range(1, 8)));
		layerList.add(new TagRange("g", new Range(13, 18)));
		layerList.add(new TagRange("g", new Range(10, 19)));
//		System.out.println(layerList.ranges2.size());
		System.out.println(layerList.layers.get(0));
//		System.out.println(layerList.layers.get(1));
		System.out.println(layerList.getTagRange("g", MathUtil.distanceF(12)));
//		System.out.println(layerList.getTagRange("g2", MathUtil.distanceF(new Range(1,60))));
//		System.out.println(layerList.getTagRange("g3", MathUtil.distanceF(new Range(1,60))));
	}
	
	public TagRange getTagRange(Object tagCond) {
		F1<Object, Boolean> t = LogicList.tagCond(tagCond);
		for (ArrayList<TagRange> ranges : layers) {
			for (TagRange tagRange : ranges) {
				if (t.e(tagRange.tag)) {
					return tagRange;
				}
			}
		}
		return null;
	}

	public LayerList insertPoses(List<Integer> insertedPoses) {
		LinkedList<ArrayList<TagRange>> ranges2 = new LinkedList<ArrayList<TagRange>>();
		for (ArrayList<TagRange> ranges : this.layers) {
			ranges2.add(Structure.insertPoses(insertedPoses, ranges));
		}
		return new LayerList(ranges2);
	}

	public void add(LayerList layerList, int index) {
		int i = 0;
		for (ArrayList<TagRange> ranges : layerList.layers) {
			ArrayList<TagRange> myList;
			if (i < layers.size()) {
				myList = layers.get(i);
			} else {
				myList = new ArrayList<TagRange>();
				layers.add(myList);
			}
			
			for (TagRange tagRange : ranges) {
				myList.add(new TagRange(tagRange.tag, tagRange.range.shiftRight(index)));
			}
			i++;
		}
	}

	/**
	 * Loop bottom up, if not found in one layer, quit
	 * @param inRange
	 * @param f1
	 */
	public void eachTagRange(Range inRange, final F1<TagRange, Boolean> f1) {
		final boolean[] laterInterrupted = {true};
		for (final ArrayList<TagRange> ranges : layers) {
			Cols.eachZeroDistances(MathUtil.distanceF(inRange), Cols.randomAccessCol(ranges, TagRange.rangeF), new F1<Integer, Boolean>() {public Boolean e(Integer index) {
				return laterInterrupted[0] = f1.e(ranges.get(index.intValue()));
			}});
			if (laterInterrupted[0]) {
				return;
			}
		}
	}

	/**
	 * Loop every layers
	 * @param inRange
	 * @param f1
	 */
	public void eachTagRange(final F1<TagRange, Boolean> f1) {
		for (final ArrayList<TagRange> ranges : layers) {
			for (TagRange tagRange : ranges) {
				if (f1.e(tagRange)) return;
			}
		}
	}
}
