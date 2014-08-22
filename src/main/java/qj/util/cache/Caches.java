package qj.util.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import qj.util.DateUtil;
import qj.util.ThreadUtil;
import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P0;

public class Caches {

	public static <A> F0<A> timedCache(final int time, final F0<A> f0) {
		final boolean[] inited = {false};
		final AtomicReference<A> ref = new AtomicReference<A>(null);
		return new F0<A>() {public A e() {
			if (inited[0]) {
				return ref.get();
			} else {
				A val = f0.e();
				ref.set(val);
				inited[0] = true;
				ThreadUtil.run(new P0() {public void e() {
					ThreadUtil.sleep(time);
					inited[0] = false;
					ref.set(null);
				}});
				return val;
			}
		}};
	}
	public static <A> F0<A> fixM(final Integer minutes, final F0<A> f0) {
		if (minutes==null || minutes==0) {
			return f0;
		}
		
		final long[] nextCheck = {0};
		final AtomicReference<A> cache = new AtomicReference<A>();
		
		return new F0<A>() {public A e() {
			if (System.currentTimeMillis() > nextCheck[0]) {
				A newValue = f0.e();
				cache.set(newValue);
				nextCheck[0] = nextCheck(minutes);
			}
			
			return cache.get();
		}};
	}
	
	public static <A,T> F1<A,T> fixMMap(final Integer minutes, final F1<A,T> f1) {
		final HashMap<A, F0<T>> cache = new HashMap<A, F0<T>>();
		return new F1<A,T>() {public T e(A a) {
			F0<T> cachedF = cache.get(a);
			if (cachedF==null) {
				cachedF = fixM(minutes, Fs.f0(f1, a));
				cache.put(a, cachedF);
			}
			return cachedF.e();
		}};
	}

	public static long nextCheck(int minutes) {
		if (minutes<=0) {
			throw new IllegalArgumentException("Minutes must be > 0");
		}
		Calendar ca = Calendar.getInstance();
		Date now = new Date();
		ca.setTime(now);
		DateUtil.truncateHour(ca);
		long currentMillis = now.getTime();
		
		long step = minutes * DateUtil.MINUTE_LENGTH;
		for (long i = ca.getTime().getTime(); ; i+=step) {
			if (i > currentMillis) {
				return i;
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new Date(nextCheck(15)));;
	}

}
