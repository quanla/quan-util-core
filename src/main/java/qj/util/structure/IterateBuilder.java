package qj.util.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import qj.tool.logicdb.LogicList;
import qj.util.funct.F1;
import qj.util.funct.P1;
import qj.util.funct.P2;
import qj.util.math.Range;

public class IterateBuilder {

	private final Structure structure;
	ArrayList<F1<Object,Boolean>> markupConditions = new ArrayList<F1<Object,Boolean>>();
	HashSet<List<TagRange>> lists = new HashSet<List<TagRange>>();
	
	public IterateBuilder(Object[] markups, Structure structure) {
		this.structure = structure;

		for (Object markup : markups) {
			with(markup);
		}
	}

	public IterateBuilder with(final Object markupCond) {
		F1<Object, Boolean> t;
		t = LogicList.tagCond(markupCond);
		List<TagRange> list = structure.allRanges.get(markupCond);
		lists.add(list);
		markupConditions.add(t);
		return this;
	}

	public void forwardFull(P2<Object, Range> p2) {
		int index = 0;

		for (List<TagRange> list : lists) {
			for (TagRange tagRange : list) {
				if (good(tagRange)) {
					if (tagRange.range.getFrom() > index) {
						p2.e(null, new Range(index, tagRange.range.getFrom()));
					}
					p2.e(tagRange.tag, tagRange.range);
					index = tagRange.range.getTo();
				}
			}
		}
		if (index < structure.text.length()) {
			p2.e(null, new Range(index, structure.text.length()));
		}
	}
	
	public void forward(P2<Object, Range> p2) {
		for (List<TagRange> list : structure.allRanges.values()) {
			for (TagRange tagRange : list) {
				if (good(tagRange)) {
					p2.e(tagRange.tag, tagRange.range);
				}
			}
		}
	}

	private boolean good(TagRange tagRange) {
		for (F1<Object, Boolean> f1 : markupConditions) {
			if (f1.e(tagRange.tag)) {
				return true;
			}
		}
		return false;
	}

	public void forward(final P1<Range> p1) {
		forward(new P2<Object, Range>() {public void e(Object marker, Range range) {
			p1.e(range);
		}});
	}
	
}