package qj.util.funct;

import qj.util.ReflectUtil;

import java.lang.reflect.Method;

public class FsGenerated {

    public static <A> A f(final Method method, final Object object) {
        Object ret;
        if (Void.TYPE.equals(method.getReturnType())) {
            int paraNum = method.getParameterTypes().length;
            if (paraNum == 0) {
                ret = new P0() {public void e() {
                    ReflectUtil.invoke(method, object);
                }};
            } else if (paraNum == 1) {
                ret = new P1() {public void e(Object a1) {
                    ReflectUtil.invoke(method, object, a1);
                }};
            } else if (paraNum == 2) {
                ret = new P2() {public void e(Object a1, Object a2) {
                    ReflectUtil.invoke(method, object, a1, a2);
                }};
            } else if (paraNum == 3) {
                ret = new P3() {public void e(Object a1, Object a2, Object a3) {
                    ReflectUtil.invoke(method, object, a1, a2, a3);
                }};
            } else if (paraNum == 4) {
                ret = new P4() {public void e(Object a1, Object a2, Object a3, Object a4) {
                    ReflectUtil.invoke(method, object, a1, a2, a3, a4);
                }};
            } else if (paraNum == 5) {
                ret = new P5() {public void e(Object a1, Object a2, Object a3, Object a4, Object a5) {
                    ReflectUtil.invoke(method, object, a1, a2, a3, a4, a5);
                }};
            } else { //if (paraNum == 4) {
                ret = new P6() {public void e(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {
                    ReflectUtil.invoke(method, object, a1, a2, a3, a4, a5, a6);
                }};
            }

        } else {
            int paraNum = method.getParameterTypes().length;
            if (paraNum == 0) {
                ret = new F0<A>() {public A e() {
                    return (A) ReflectUtil.invoke(method, object);
                }};
            } else if (paraNum == 1) {
                ret = new F1() {public Object e(Object a1) {
                    return ReflectUtil.invoke(method, object, a1);
                }};
            } else if (paraNum == 2) {
                ret = new F2() {public Object e(Object a1, Object a2) {
                    return ReflectUtil.invoke(method, object, a1, a2);
                }};
            } else if (paraNum == 3) {
                ret = new F3() {public Object e(Object a1, Object a2, Object a3) {
                    return ReflectUtil.invoke(method, object, a1, a2, a3);
                }};
            } else if (paraNum == 4) {
            	ret = new F4() {public Object e(Object a1, Object a2, Object a3, Object a4) {
                    return ReflectUtil.invoke(method, object, a1, a2, a3, a4);
                }};
            } else if (paraNum == 5) {
                ret = new F5() {public Object e(Object a1, Object a2, Object a3, Object a4, Object a5) {
                    return ReflectUtil.invoke(method, object, a1, a2, a3, a4, a5);
                }};
            } else {
                ret = new F6() {public Object e(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {
                    return ReflectUtil.invoke(method, object, a1, a2, a3, a4, a5, a6);
                }};
            }
        }
        return (A) ret;
    }

	// Down F
	/**
	 * Convert from a f1 to f0
	 * @return f0
	 */
	public static <A, R> F0<R> f0(final F1<A, R> f1, final A a) {
		return new F0<R>(){public R e() {
			return f1.e(a);
		}};
	}
	/**
	 * Convert from a f2 to f0
	 * @return f0
	 */
	public static <A, B, R> F0<R> f0(final F2<A, B, R> f2, final A a, final B b) {
		return new F0<R>(){public R e() {
			return f2.e(a, b);
		}};
	}
	/**
	 * Convert from a f3 to f0
	 * @return f0
	 */
	public static <A, B, C, R> F0<R> f0(final F3<A, B, C, R> f3, final A a, final B b, final C c) {
		return new F0<R>(){public R e() {
			return f3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a f2 to f1
	 * @return f1
	 */
	public static <A, B, R> F1<A, R> f1(final F2<A, B, R> f2, final B b) {
		return new F1<A, R>(){public R e(final A a) {
			return f2.e(a, b);
		}};
	}
	/**
	 * Convert from a f3 to f1
	 * @return f1
	 */
	public static <A, B, C, R> F1<A, R> f1(final F3<A, B, C, R> f3, final B b, final C c) {
		return new F1<A, R>(){public R e(final A a) {
			return f3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a f3 to f2
	 * @return f2
	 */
	public static <A, B, C, R> F2<A, B, R> f2(final F3<A, B, C, R> f3, final C c) {
		return new F2<A, B, R>(){public R e(final A a, final B b) {
			return f3.e(a, b, c);
		}};
	}

	// Up F
	/**
	 * Convert from a f0 to f1
	 * @return f1
	 */
	public static <A, R> F1<A, R> f1(final F0<R> f0) {
		return new F1<A, R>(){public R e(final A a) {
			return f0.e();
		}};
	}
	/**
	 * Convert from a f0 to f2
	 * @return f2
	 */
	public static <A, B, R> F2<A, B, R> f2(final F0<R> f0) {
		return new F2<A, B, R>(){public R e(final A a, final B b) {
			return f0.e();
		}};
	}
	/**
	 * Convert from a f1 to f2
	 * @return f2
	 */
	public static <A, B, R> F2<A, B, R> f2(final F1<A, R> f1) {
		return new F2<A, B, R>(){public R e(final A a, final B b) {
			return f1.e(a);
		}};
	}
	/**
	 * Convert from a f0 to f3
	 * @return f3
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final F0<R> f0) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			return f0.e();
		}};
	}
	/**
	 * Convert from a f1 to f3
	 * @return f3
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final F1<A, R> f1) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			return f1.e(a);
		}};
	}
	/**
	 * Convert from a f2 to f3
	 * @return f3
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final F2<A, B, R> f2) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			return f2.e(a, b);
		}};
	}
	/**
	 * Convert from a f0 to f4
	 * @return f4
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final F0<R> f0) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			return f0.e();
		}};
	}
	/**
	 * Convert from a f1 to f4
	 * @return f4
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final F1<A, R> f1) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			return f1.e(a);
		}};
	}
	/**
	 * Convert from a f2 to f4
	 * @return f4
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final F2<A, B, R> f2) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			return f2.e(a, b);
		}};
	}
	/**
	 * Convert from a f3 to f4
	 * @return f4
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final F3<A, B, C, R> f3) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			return f3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a f0 to f5
	 * @return f5
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final F0<R> f0) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			return f0.e();
		}};
	}
	/**
	 * Convert from a f1 to f5
	 * @return f5
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final F1<A, R> f1) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			return f1.e(a);
		}};
	}
	/**
	 * Convert from a f2 to f5
	 * @return f5
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final F2<A, B, R> f2) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			return f2.e(a, b);
		}};
	}
	/**
	 * Convert from a f3 to f5
	 * @return f5
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final F3<A, B, C, R> f3) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			return f3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a f4 to f5
	 * @return f5
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final F4<A, B, C, D, R> f4) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			return f4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a f0 to f6
	 * @return f6
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final F0<R> f0) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			return f0.e();
		}};
	}
	/**
	 * Convert from a f1 to f6
	 * @return f6
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final F1<A, R> f1) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			return f1.e(a);
		}};
	}
	/**
	 * Convert from a f2 to f6
	 * @return f6
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final F2<A, B, R> f2) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			return f2.e(a, b);
		}};
	}
	/**
	 * Convert from a f3 to f6
	 * @return f6
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final F3<A, B, C, R> f3) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			return f3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a f4 to f6
	 * @return f6
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final F4<A, B, C, D, R> f4) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			return f4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a f5 to f6
	 * @return f6
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final F5<A, B, C, D, E, R> f5) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			return f5.e(a, b, c, d, e);
		}};
	}

	// Down P
	/**
	 * Convert from a p1 to p0
	 * @return p0
	 */
	public static <A> P0 p0(final P1<A> p1, final A a) {
		return new P0(){public void e() {
			p1.e(a);
		}};
	}
	/**
	 * Convert from a p2 to p0
	 * @return p0
	 */
	public static <A, B> P0 p0(final P2<A, B> p2, final A a, final B b) {
		return new P0(){public void e() {
			p2.e(a, b);
		}};
	}
	/**
	 * Convert from a p3 to p0
	 * @return p0
	 */
	public static <A, B, C> P0 p0(final P3<A, B, C> p3, final A a, final B b, final C c) {
		return new P0(){public void e() {
			p3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a p4 to p0
	 * @return p0
	 */
	public static <A, B, C, D> P0 p0(final P4<A, B, C, D> p4, final A a, final B b, final C c, final D d) {
		return new P0(){public void e() {
			p4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a p5 to p0
	 * @return p0
	 */
	public static <A, B, C, D, E> P0 p0(final P5<A, B, C, D, E> p5, final A a, final B b, final C c, final D d, final E e) {
		return new P0(){public void e() {
			p5.e(a, b, c, d, e);
		}};
	}
	/**
	 * Convert from a p6 to p0
	 * @return p0
	 */
	public static <A, B, C, D, E, F> P0 p0(final P6<A, B, C, D, E, F> p6, final A a, final B b, final C c, final D d, final E e, final F f) {
		return new P0(){public void e() {
			p6.e(a, b, c, d, e, f);
		}};
	}
	/**
	 * Convert from a p2 to p1
	 * @return p1
	 */
	public static <A, B> P1<A> p1(final P2<A, B> p2, final B b) {
		return new P1<A>(){public void e(final A a) {
			p2.e(a, b);
		}};
	}
	/**
	 * Convert from a p3 to p1
	 * @return p1
	 */
	public static <A, B, C> P1<A> p1(final P3<A, B, C> p3, final B b, final C c) {
		return new P1<A>(){public void e(final A a) {
			p3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a p4 to p1
	 * @return p1
	 */
	public static <A, B, C, D> P1<A> p1(final P4<A, B, C, D> p4, final B b, final C c, final D d) {
		return new P1<A>(){public void e(final A a) {
			p4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a p5 to p1
	 * @return p1
	 */
	public static <A, B, C, D, E> P1<A> p1(final P5<A, B, C, D, E> p5, final B b, final C c, final D d, final E e) {
		return new P1<A>(){public void e(final A a) {
			p5.e(a, b, c, d, e);
		}};
	}
	/**
	 * Convert from a p6 to p1
	 * @return p1
	 */
	public static <A, B, C, D, E, F> P1<A> p1(final P6<A, B, C, D, E, F> p6, final B b, final C c, final D d, final E e, final F f) {
		return new P1<A>(){public void e(final A a) {
			p6.e(a, b, c, d, e, f);
		}};
	}
	/**
	 * Convert from a p3 to p2
	 * @return p2
	 */
	public static <A, B, C> P2<A, B> p2(final P3<A, B, C> p3, final C c) {
		return new P2<A, B>(){public void e(final A a, final B b) {
			p3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a p4 to p2
	 * @return p2
	 */
	public static <A, B, C, D> P2<A, B> p2(final P4<A, B, C, D> p4, final C c, final D d) {
		return new P2<A, B>(){public void e(final A a, final B b) {
			p4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a p5 to p2
	 * @return p2
	 */
	public static <A, B, C, D, E> P2<A, B> p2(final P5<A, B, C, D, E> p5, final C c, final D d, final E e) {
		return new P2<A, B>(){public void e(final A a, final B b) {
			p5.e(a, b, c, d, e);
		}};
	}
	/**
	 * Convert from a p6 to p2
	 * @return p2
	 */
	public static <A, B, C, D, E, F> P2<A, B> p2(final P6<A, B, C, D, E, F> p6, final C c, final D d, final E e, final F f) {
		return new P2<A, B>(){public void e(final A a, final B b) {
			p6.e(a, b, c, d, e, f);
		}};
	}
	/**
	 * Convert from a p4 to p3
	 * @return p3
	 */
	public static <A, B, C, D> P3<A, B, C> p3(final P4<A, B, C, D> p4, final D d) {
		return new P3<A, B, C>(){public void e(final A a, final B b, final C c) {
			p4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a p5 to p3
	 * @return p3
	 */
	public static <A, B, C, D, E> P3<A, B, C> p3(final P5<A, B, C, D, E> p5, final D d, final E e) {
		return new P3<A, B, C>(){public void e(final A a, final B b, final C c) {
			p5.e(a, b, c, d, e);
		}};
	}
	/**
	 * Convert from a p6 to p3
	 * @return p3
	 */
	public static <A, B, C, D, E, F> P3<A, B, C> p3(final P6<A, B, C, D, E, F> p6, final D d, final E e, final F f) {
		return new P3<A, B, C>(){public void e(final A a, final B b, final C c) {
			p6.e(a, b, c, d, e, f);
		}};
	}
	/**
	 * Convert from a p5 to p4
	 * @return p4
	 */
	public static <A, B, C, D, E> P4<A, B, C, D> p4(final P5<A, B, C, D, E> p5, final E e) {
		return new P4<A, B, C, D>(){public void e(final A a, final B b, final C c, final D d) {
			p5.e(a, b, c, d, e);
		}};
	}
	/**
	 * Convert from a p6 to p4
	 * @return p4
	 */
	public static <A, B, C, D, E, F> P4<A, B, C, D> p4(final P6<A, B, C, D, E, F> p6, final E e, final F f) {
		return new P4<A, B, C, D>(){public void e(final A a, final B b, final C c, final D d) {
			p6.e(a, b, c, d, e, f);
		}};
	}
	/**
	 * Convert from a p6 to p5
	 * @return p5
	 */
	public static <A, B, C, D, E, F> P5<A, B, C, D, E> p5(final P6<A, B, C, D, E, F> p6, final F f) {
		return new P5<A, B, C, D, E>(){public void e(final A a, final B b, final C c, final D d, final E e) {
			p6.e(a, b, c, d, e, f);
		}};
	}

	// Up P
	/**
	 * Convert from a p0 to p1
	 * @return p1
	 */
	public static <A> P1<A> p1(final P0 p0) {
		return new P1<A>(){public void e(final A a) {
			p0.e();
		}};
	}
	/**
	 * Convert from a p0 to p2
	 * @return p2
	 */
	public static <A, B> P2<A, B> p2(final P0 p0) {
		return new P2<A, B>(){public void e(final A a, final B b) {
			p0.e();
		}};
	}
	/**
	 * Convert from a p1 to p2
	 * @return p2
	 */
	public static <A, B> P2<A, B> p2(final P1<A> p1) {
		return new P2<A, B>(){public void e(final A a, final B b) {
			p1.e(a);
		}};
	}
	/**
	 * Convert from a p0 to p3
	 * @return p3
	 */
	public static <A, B, C> P3<A, B, C> p3(final P0 p0) {
		return new P3<A, B, C>(){public void e(final A a, final B b, final C c) {
			p0.e();
		}};
	}
	/**
	 * Convert from a p1 to p3
	 * @return p3
	 */
	public static <A, B, C> P3<A, B, C> p3(final P1<A> p1) {
		return new P3<A, B, C>(){public void e(final A a, final B b, final C c) {
			p1.e(a);
		}};
	}
	/**
	 * Convert from a p2 to p3
	 * @return p3
	 */
	public static <A, B, C> P3<A, B, C> p3(final P2<A, B> p2) {
		return new P3<A, B, C>(){public void e(final A a, final B b, final C c) {
			p2.e(a, b);
		}};
	}
	/**
	 * Convert from a p0 to p4
	 * @return p4
	 */
	public static <A, B, C, D> P4<A, B, C, D> p4(final P0 p0) {
		return new P4<A, B, C, D>(){public void e(final A a, final B b, final C c, final D d) {
			p0.e();
		}};
	}
	/**
	 * Convert from a p1 to p4
	 * @return p4
	 */
	public static <A, B, C, D> P4<A, B, C, D> p4(final P1<A> p1) {
		return new P4<A, B, C, D>(){public void e(final A a, final B b, final C c, final D d) {
			p1.e(a);
		}};
	}
	/**
	 * Convert from a p2 to p4
	 * @return p4
	 */
	public static <A, B, C, D> P4<A, B, C, D> p4(final P2<A, B> p2) {
		return new P4<A, B, C, D>(){public void e(final A a, final B b, final C c, final D d) {
			p2.e(a, b);
		}};
	}
	/**
	 * Convert from a p3 to p4
	 * @return p4
	 */
	public static <A, B, C, D> P4<A, B, C, D> p4(final P3<A, B, C> p3) {
		return new P4<A, B, C, D>(){public void e(final A a, final B b, final C c, final D d) {
			p3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a p0 to p5
	 * @return p5
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5(final P0 p0) {
		return new P5<A, B, C, D, E>(){public void e(final A a, final B b, final C c, final D d, final E e) {
			p0.e();
		}};
	}
	/**
	 * Convert from a p1 to p5
	 * @return p5
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5(final P1<A> p1) {
		return new P5<A, B, C, D, E>(){public void e(final A a, final B b, final C c, final D d, final E e) {
			p1.e(a);
		}};
	}
	/**
	 * Convert from a p2 to p5
	 * @return p5
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5(final P2<A, B> p2) {
		return new P5<A, B, C, D, E>(){public void e(final A a, final B b, final C c, final D d, final E e) {
			p2.e(a, b);
		}};
	}
	/**
	 * Convert from a p3 to p5
	 * @return p5
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5(final P3<A, B, C> p3) {
		return new P5<A, B, C, D, E>(){public void e(final A a, final B b, final C c, final D d, final E e) {
			p3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a p4 to p5
	 * @return p5
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5(final P4<A, B, C, D> p4) {
		return new P5<A, B, C, D, E>(){public void e(final A a, final B b, final C c, final D d, final E e) {
			p4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a p0 to p6
	 * @return p6
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final P0 p0) {
		return new P6<A, B, C, D, E, F>(){public void e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p0.e();
		}};
	}
	/**
	 * Convert from a p1 to p6
	 * @return p6
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final P1<A> p1) {
		return new P6<A, B, C, D, E, F>(){public void e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p1.e(a);
		}};
	}
	/**
	 * Convert from a p2 to p6
	 * @return p6
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final P2<A, B> p2) {
		return new P6<A, B, C, D, E, F>(){public void e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p2.e(a, b);
		}};
	}
	/**
	 * Convert from a p3 to p6
	 * @return p6
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final P3<A, B, C> p3) {
		return new P6<A, B, C, D, E, F>(){public void e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p3.e(a, b, c);
		}};
	}
	/**
	 * Convert from a p4 to p6
	 * @return p6
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final P4<A, B, C, D> p4) {
		return new P6<A, B, C, D, E, F>(){public void e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p4.e(a, b, c, d);
		}};
	}
	/**
	 * Convert from a p5 to p6
	 * @return p6
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final P5<A, B, C, D, E> p5) {
		return new P6<A, B, C, D, E, F>(){public void e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p5.e(a, b, c, d, e);
		}};
	}

	// Nothing P
	/**
	 * Do nothing
	 * @return f that do nothing upon invocation
	 */
	public static P0 p0() {
		return new P0(){public void e() {
		}};
	}
	/**
	 * Do nothing
	 * @return f that do nothing upon invocation
	 */
	public static <A> P1<A> p1() {
		return new P1<A>(){public void e(final A a) {
		}};
	}
	/**
	 * Do nothing
	 * @return f that do nothing upon invocation
	 */
	public static <A, B> P2<A, B> p2() {
		return new P2<A, B>(){public void e(final A a, final B b) {
		}};
	}
	/**
	 * Do nothing
	 * @return f that do nothing upon invocation
	 */
	public static <A, B, C> P3<A, B, C> p3() {
		return new P3<A, B, C>(){public void e(final A a, final B b, final C c) {
		}};
	}
	/**
	 * Do nothing
	 * @return f that do nothing upon invocation
	 */
	public static <A, B, C, D> P4<A, B, C, D> p4() {
		return new P4<A, B, C, D>(){public void e(final A a, final B b, final C c, final D d) {
		}};
	}
	/**
	 * Do nothing
	 * @return f that do nothing upon invocation
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5() {
		return new P5<A, B, C, D, E>(){public void e(final A a, final B b, final C c, final D d, final E e) {
		}};
	}
	/**
	 * Do nothing
	 * @return f that do nothing upon invocation
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6() {
		return new P6<A, B, C, D, E, F>(){public void e(final A a, final B b, final C c, final D d, final E e, final F f) {
		}};
	}

	// Reflect
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static P0 p0(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A> P1<A> p1(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B> P2<A, B> p2(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C> P3<A, B, C> p3(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C, D> P4<A, B, C, D> p4(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <R> F0<R> f0(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, R> F1<A, R> f1(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, R> F2<A, B, R> f2(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method of class
	 * @param method Name of the class's method
	 * @param clazz Class that contains the method
	 * @return f use reflection to call to method of class
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final String method, final Class<?> clazz) {
		Method m = ReflectUtil.deepFindMethod(method, clazz );
		return f(m, null);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static P0 p0(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A> P1<A> p1(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B> P2<A, B> p2(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C> P3<A, B, C> p3(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C, D> P4<A, B, C, D> p4(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p5(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> p6(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <R> F0<R> f0(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, R> F1<A, R> f1(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, R> F2<A, B, R> f2(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}
	/**
	 * Reflect call to method
	 * @param method Name of the method
	 * @param object object to invoke the method on
	 * @return f use reflection to call to method
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final String method, final Object object) {
		Method m = ReflectUtil.deepFindMethod(method, null,object.getClass());
		return f(m, object);
	}

	// F with fixed return
	/**
	 * Return fixed value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <R> F0<R> f0(final R ret) {
		return new F0<R>(){public R e() {
			return ret;
		}};
	}
	/**
	 * Return fixed value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, R> F1<A, R> f1(final R ret) {
		return new F1<A, R>(){public R e(final A a) {
			return ret;
		}};
	}
	/**
	 * Return fixed value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, R> F2<A, B, R> f2(final R ret) {
		return new F2<A, B, R>(){public R e(final A a, final B b) {
			return ret;
		}};
	}
	/**
	 * Return fixed value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final R ret) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			return ret;
		}};
	}
	/**
	 * Return fixed value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final R ret) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			return ret;
		}};
	}
	/**
	 * Return fixed value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final R ret) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			return ret;
		}};
	}
	/**
	 * Return fixed value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			return ret;
		}};
	}

	// F with P and fixed return
	/**
	 * Call to p and return fixed value
	 * @param p0 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <R> F0<R> f0(final P0 p0, final R ret) {
		return new F0<R>(){public R e() {
			p0.e();
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p0 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, R> F1<A, R> f1(final P0 p0, final R ret) {
		return new F1<A, R>(){public R e(final A a) {
			p0.e();
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p1 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, R> F1<A, R> f1(final P1<A> p1, final R ret) {
		return new F1<A, R>(){public R e(final A a) {
			p1.e(a);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p0 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, R> F2<A, B, R> f2(final P0 p0, final R ret) {
		return new F2<A, B, R>(){public R e(final A a, final B b) {
			p0.e();
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p1 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, R> F2<A, B, R> f2(final P1<A> p1, final R ret) {
		return new F2<A, B, R>(){public R e(final A a, final B b) {
			p1.e(a);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p2 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, R> F2<A, B, R> f2(final P2<A, B> p2, final R ret) {
		return new F2<A, B, R>(){public R e(final A a, final B b) {
			p2.e(a, b);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p0 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final P0 p0, final R ret) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			p0.e();
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p1 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final P1<A> p1, final R ret) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			p1.e(a);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p2 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final P2<A, B> p2, final R ret) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			p2.e(a, b);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p3 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, R> F3<A, B, C, R> f3(final P3<A, B, C> p3, final R ret) {
		return new F3<A, B, C, R>(){public R e(final A a, final B b, final C c) {
			p3.e(a, b, c);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p0 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final P0 p0, final R ret) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			p0.e();
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p1 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final P1<A> p1, final R ret) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			p1.e(a);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p2 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final P2<A, B> p2, final R ret) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			p2.e(a, b);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p3 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final P3<A, B, C> p3, final R ret) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			p3.e(a, b, c);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p4 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, R> F4<A, B, C, D, R> f4(final P4<A, B, C, D> p4, final R ret) {
		return new F4<A, B, C, D, R>(){public R e(final A a, final B b, final C c, final D d) {
			p4.e(a, b, c, d);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p0 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final P0 p0, final R ret) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			p0.e();
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p1 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final P1<A> p1, final R ret) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			p1.e(a);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p2 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final P2<A, B> p2, final R ret) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			p2.e(a, b);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p3 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final P3<A, B, C> p3, final R ret) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			p3.e(a, b, c);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p4 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final P4<A, B, C, D> p4, final R ret) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			p4.e(a, b, c, d);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p5 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, R> F5<A, B, C, D, E, R> f5(final P5<A, B, C, D, E> p5, final R ret) {
		return new F5<A, B, C, D, E, R>(){public R e(final A a, final B b, final C c, final D d, final E e) {
			p5.e(a, b, c, d, e);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p0 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final P0 p0, final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p0.e();
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p1 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final P1<A> p1, final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p1.e(a);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p2 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final P2<A, B> p2, final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p2.e(a, b);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p3 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final P3<A, B, C> p3, final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p3.e(a, b, c);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p4 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final P4<A, B, C, D> p4, final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p4.e(a, b, c, d);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p5 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final P5<A, B, C, D, E> p5, final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p5.e(a, b, c, d, e);
			return ret;
		}};
	}
	/**
	 * Call to p and return fixed value
	 * @param p6 the function to call before return value
	 * @param ret the fixed value to return
	 * @return ret
	 */
	public static <A, B, C, D, E, F, R> F6<A, B, C, D, E, F, R> f6(final P6<A, B, C, D, E, F> p6, final R ret) {
		return new F6<A, B, C, D, E, F, R>(){public R e(final A a, final B b, final C c, final D d, final E e, final F f) {
			p6.e(a, b, c, d, e, f);
			return ret;
		}};
	}
}