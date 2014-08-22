package qj.util.col;

import qj.util.StringUtil;

public class Trees {
	public static <A> String toStringTree(Tree<A> tree, int indent) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(StringUtil.createString(indent * 2, ' ') + "- " + tree.value);
		for (Tree<A> down : tree.downs) {
			sb.append("\n");
			sb.append(toStringTree(down, indent + 1));
//			sb.append("- " + down.value);
		}
		
		return sb.toString();
	}
}
