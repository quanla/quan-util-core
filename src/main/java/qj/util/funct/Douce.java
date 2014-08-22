package qj.util.funct;

import java.io.Serializable;

public class Douce<A,B> implements Serializable {
	private A _1;
	private B _2;
	public Douce(A _1, B _2) {
		this._1 = _1;
		this._2 = _2;
	}
	
	public static <A,B> F1<Douce<A,B>,B> get2F() {
		return new F1<Douce<A,B>, B>() {public B e(Douce<A, B> obj) {
			return obj.get2();
		}};
	}
	public static <A,B> F1<Douce<A,B>,A> get1F() {
		return new F1<Douce<A,B>, A>() {public A e(Douce<A, B> obj) {
			return obj.get1();
		}};
	}
	
	public A get1() {
		return _1;
	}
	public B get2() {
		return _2;
	}
	public void set1(A _1) {
		this._1 = _1;
	}
	public void set2(B _2) {
		this._2 = _2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
		result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Douce other = (Douce) obj;
		if (_1 == null) {
			if (other._1 != null)
				return false;
		} else if (!_1.equals(other._1))
			return false;
		if (_2 == null) {
			if (other._2 != null)
				return false;
		} else if (!_2.equals(other._2))
			return false;
		return true;
	}

	public String toString() {
		return "(" + _1 + ", " + _2 + ")";
	}
	
}
