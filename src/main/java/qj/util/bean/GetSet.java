package qj.util.bean;

import qj.util.funct.F0;
import qj.util.funct.P1;

public class GetSet<A> {
	public GetSet(F0<A> getter, P1<A> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	public F0<A> getter;
	public P1<A> setter;
}
