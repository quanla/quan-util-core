package qj.util.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import qj.util.Cols;
import qj.util.funct.F1;
import qj.util.math.Range;

public class StructureHelper {

	public static void eachTagRange_sorted(F1<Range, Integer> distanceF,
			final List<TagRange> ranges,
			final F1<TagRange, Boolean> f) {
		Cols.eachZeroDistances(distanceF, Cols.randomAccessCol(ranges, TagRange.rangeF), new F1<Integer,Boolean>() {public Boolean e(Integer i) {
			TagRange tagRange = ranges.get(i.intValue());
			return f.e(tagRange);
		}});
	}

	public static String toString(Map<Object, List<TagRange>> allRanges) {
		StringBuilder sb = new StringBuilder();
		if (allRanges.isEmpty()) {
			sb.append("Empty Structure");
		} else {
			//		sb.append(false)
			for (List<TagRange> tagRanges : allRanges.values()) {
				outputList(sb, tagRanges);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static void outputList(StringBuilder sb, List<TagRange> tagRanges) {
		for (TagRange tagRange : tagRanges) {
			sb.append(tagRange.tag);
			sb.append(": ");
			sb.append(tagRange.range);
			sb.append("; ");
		}
	}

	public static String toString(HashMap<Object, LayerList> layerLists) {
		StringBuilder sb = new StringBuilder();
		for (Entry<Object, LayerList> entry : layerLists.entrySet()) {
			Object key = entry.getKey();
			int i = 0;
			for (ArrayList<TagRange> ranges : entry.getValue().layers) {
				sb.append(key + "/L" + i++ + ": ");
				outputList(sb, ranges);
				sb.append("\n");
			}
		}
		return sb.toString();
	}


}
