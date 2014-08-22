package qj.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.funct.P1;

public class TimingUtil {
	public static <A,B> F1<A,B> timeSwitch(long delay, F1<A,B> f1a, final F1<A,B> f1b) {
		final long until = System.currentTimeMillis() + delay;
		final F0<Boolean> every5 = every(5);
		final AtomicReference<F1<A,B>> ref = new AtomicReference<F1<A,B>>(f1a);
		f1a = null;
		final boolean[] switched = {false};
		return new F1<A,B>() {public B e(A a) {
			if (!switched[0] && every5.e() && System.currentTimeMillis() > until) {
				switched[0] = true;
				ref.set(f1b);
			}
			
			return ref.get().e(a);
		}};
	}
	
	public static F0<Boolean> every(final int count) {
		final int[] i = {0};
		return new F0<Boolean>() {public Boolean e() {
			i[0]++;
			if (i[0]<count) {
				return false;
			} else {
				i[0]=0;
				return true;
			}
		}};
	}
	
	public static <A> List<A> invokeLimitByTime(List<A> list, int time, P1<A> f) {
		long start = System.currentTimeMillis();
		int i=0;
		for (A a : list) {
			if (System.currentTimeMillis() - start > time) {
				return list.subList(i, list.size());
			}
			
			f.e(a);
			i++;
		}
				
		return Collections.emptyList();
	}
	
	public static void main(String[] args) {
		System.out.println("Remain: " + invokeLimitByTime(Arrays.asList("1", "2", "3"), 600, new P1<String>() {public void e(String obj) {
			System.out.println("Invoke " + obj);
			ThreadUtil.sleep(100);
		}}));
	}

	public static <A> F0<A> invokeControl(final F0<A> f,
			final int timeControl) {
		final AtomicReference<A> cache = new AtomicReference<A>();
		final long[] lastInvoke = {0L};
		return new F0<A>() {public A e() {
			long now = System.currentTimeMillis();
			if (cache.get() == null || lastInvoke[0] < now - timeControl) {
				cache.set(f.e());
				lastInvoke[0] = now;
			}
			return cache.get();
		}};
	}

	public static F1<Boolean, Boolean> trueForSoLong(final int length) {
		final Long[] lastTimeFalse = {null};
		return new F1<Boolean, Boolean>() {public Boolean e(Boolean obj) {
			long now = System.currentTimeMillis();
			if (obj == null || obj == Boolean.FALSE) {
				lastTimeFalse[0] = now;
				return false;
			}
			
			if (lastTimeFalse[0] == null) {
				return false;
			}
			
			if (now - lastTimeFalse[0] > length) {
				lastTimeFalse[0] = null;
				return true;
			}
			return false;
		}};
	}
}
