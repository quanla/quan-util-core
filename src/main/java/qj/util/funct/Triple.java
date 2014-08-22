package qj.util.funct;

public class Triple<A,B,C> {
	private final A _1;
	private final B _2;
	private final C _3;
	public Triple(A a, B b, C c) {
		this._1 = a;
		this._2 = b;
		this._3 = c;
	}
	
	public A get1() {
		return _1;
	}
	public B get2() {
		return _2;
	}
	public C get3() {
		return _3;
	}

	public static <A,B,C> F1<Triple<A,B,C>,A> get1F() {
		return new F1<Triple<A,B,C>, A>() {public A e(Triple<A,B,C> obj) {
			return obj.get1();
		}};
	}
	
	public static <A,B,C> F1<Triple<A,B,C>,B> get2F() {
		return new F1<Triple<A,B,C>, B>() {public B e(Triple<A,B,C> obj) {
			return obj.get2();
		}};
	}
	
	public static <A,B,C> F1<Triple<A,B,C>,C> get3F() {
		return new F1<Triple<A,B,C>, C>() {public C e(Triple<A,B,C> obj) {
			return obj.get3();
		}};
	}
}
