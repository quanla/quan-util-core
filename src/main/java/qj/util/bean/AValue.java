package qj.util.bean;

import java.util.LinkedList;

import qj.util.ObjectUtil;
import qj.util.funct.F0;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;

public class AValue <V> {
	private V value;
	private LinkedList<P1<V>> listeners = new LinkedList<P1<V>>();

	public AValue(V value) {
		this.value = value;
	}
	
	public V get() {
		return value;
	}
	
	public P1<V> setter() {
		return new P1<V>() {public void e(V newValue) {
			set(newValue);
		}};
	}
	public P0 setter(final V newValue) {
		return new P0() {public void e() {
			set(newValue);
		}};
	}
	
	public void on(final V equals, final P0 p) {
		listeners.add(new P1<V>() {public void e(V obj) {
			if (ObjectUtil.equals(obj, equals)) {
				p.e();
			}
		}});
	}

	public void set(V newValue) {
		if (!ObjectUtil.equals(newValue, value)) {
			value = newValue;
			
			Fs.invokeAll(listeners, value);
		}
	}

	public F0<V> getter() {
		return new F0<V>() {public V e() {
			return value;
		}};
	}

	public void onChanged(P1<V> p1) {
		listeners.add(p1);
	}
}
