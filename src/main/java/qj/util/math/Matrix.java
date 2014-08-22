package qj.util.math;

import java.util.ArrayList;

import qj.util.funct.F0;

public class Matrix<E> {

	ArrayList<ArrayList<E>> list2;
	Point base;
	public F0<E> createElem;
	
	public Matrix(ArrayList<ArrayList<E>> list2) {
		this.list2 = list2;
		base = new Point(0,0);
	}
	
	public Matrix(int width, int height, Point base) {
		list2 = new ArrayList<>(height);
		for (int y = 0; y < height; y++) {
			ArrayList<E> line = new ArrayList<>();
			for (int x = 0; x < width; x++) {
				line.add(null);
			}
			list2.add(line);
		}
		this.base = base;
	}
	
	public Matrix<E> trim() {
		Rectangle limit = getLimit();
		
		ArrayList<ArrayList<E>> list2 = new ArrayList<>();
		for (int y = 0; y < limit.width; y++) {
			ArrayList<E> line = new ArrayList<E>();
			for (int x = 0; x < limit.height; x++) {
				line.add(get(x, y));
			}
			list2.add(line);
		}
		
		return new Matrix<E>(list2);
	}

	private Rectangle getLimit() {
		int left;
		for (int x = 0;; x--) {
			if (get(x, 0) == null) {
				left = x + 1;
				break;
			}
		}
		int top;
		for (int y = 0;; y--) {
			if (get(left, y) == null) {
				top = y + 1;
				break;
			}
		}
		int width;
		int height;
		for ( width = 0; ; width++) {
			if (get(left + width, top) == null) {
				break;
			}
		}
		for ( height = 0; ; height++) {
			if (get(left + width, top + height) == null) {
				break;
			}
		}
		return new Rectangle(left, top, width, height);
	}

	public ArrayList<ArrayList<E>> getList2() {
		return list2;
	}

	public E getf(Point loc) {
		E e = get(loc);
		if (e == null) {
			e = createElem.e();
			set(loc, e);
		}
		return e;
	}

	private void set(Point loc, E e) {
		list2.get(loc.y + base.y).set(loc.x + base.x, e);
	}

	private E get(Point loc) {
		int x = loc.x;
		int y = loc.y;
		return get(x, y);
	}

	private E get(int x, int y) {
		return list2.get(y + base.y).get(x + base.x);
	}
	
}
