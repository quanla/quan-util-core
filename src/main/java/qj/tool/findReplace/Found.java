//package qj.tool.findReplace;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import qj.util.string.Range;
//
//public class Found {
//	private List foundRanges = new ArrayList();
//	private int position = -1;
//	private String data;
//	
//	public int getStart() {
//		return ((Range)foundRanges.get(position)).getFrom();
//	}
//	
//	public int getEnd() {
//		return ((Range)foundRanges.get(position)).getTo();
//	}
//	
//	public String getFound() {
//		Range range = ((Range)foundRanges.get(position));
//		return data.substring(range.getFrom(), range.getTo());
//	}
//	
//	public boolean next() {
//		if (position < foundRanges.size() - 1) {
//			position ++;
//			return true;
//		} else
//			return false;
//	}
//	
//	public int getPosition() {
//		return position;
//	}
//	public void setPosition(int position) {
//		this.position = position;
//	}
//
//	public String getData() {
//		return data;
//	}
//
//	public void setData(String data) {
//		this.data = data;
//	}
//
//	public void setFoundRanges(List foundRanges) {
//		this.foundRanges = foundRanges;
//	}
//
//	public String getBefore(int length) {
//		int start = getStart();
//		if (start < length)
//			start = length;
//		return data.substring(start - length, start);
//	}
//}
