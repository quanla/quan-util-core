package qj.util.structure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import qj.tool.logicdb.LogicList;
import qj.util.Cols;
import qj.util.funct.F1;
import qj.util.math.Range;

public class TagRanges {

	public static Collection<TagRange> getTagRanges(Object clazz,
			F1<Range, Integer> distanceF, final List<TagRange> ranges) {
		final F1<Object, Boolean> f = LogicList.tagCond(clazz);
		
		final LinkedList<TagRange> ret = new LinkedList<TagRange>();
		
		Cols.eachZeroDistances(
				distanceF, 
				Cols.randomAccessCol(ranges, TagRange.rangeF), 
				new F1<Integer, Boolean>() {public Boolean e(Integer index) {
					TagRange found = ranges.get(index);
					if (f.e(found.tag)) {
						ret.add(found);
					}
					return false;
				}});
		return ret;
	}

}
