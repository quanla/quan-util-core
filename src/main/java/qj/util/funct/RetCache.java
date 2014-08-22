package qj.util.funct;

import java.util.LinkedList;

public class RetCache<R> implements F0<R> {
	private F0<R> f0;
	public LinkedList<R> cache = new LinkedList<R>();

	public RetCache(F0<R> f0) {
		this.f0 = f0;
	}

	@Override
	public R e() {
		R ret = f0.e();
		cache.add(ret);
		return ret;
	}
}
