package qj.tool.logicdb;

import qj.util.funct.F1;

public class StringIndexHolder<A> {
	StringIndex<A> index;
	@SuppressWarnings("rawtypes")
	F1 keyToString;
	public StringIndexHolder(StringIndex<A> index, F1 keyToString) {
		this.index = index;
		this.keyToString = keyToString;
	}
}