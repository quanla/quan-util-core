package qj.util;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArrayUtil extends ArrayUtil4 {

	/**
	 * Create a new Array of same type but has length = oldArray.length + 1.<br>
	 * Put appendObject to the first element of new array and shift all old
	 * array's element next to it.
	 *
	 * @param appendObject -
	 *            The object to be put to the begin of new array.
	 * @param oldArray -
	 *            The old array object.
	 * @return New array like old one but has extra appendObject at the
	 *         beginning.
	 */
	@SuppressWarnings("unchecked")
	public static <A> A[] appendToArrayBegin(A appendObject,
			A[] oldArray) {
		A[] newArray = (A[]) Array.newInstance(oldArray.getClass()
				.getComponentType(), oldArray.length + 1);

		newArray[0] = appendObject;
        System.arraycopy(oldArray, 0, newArray, 1, oldArray.length);
		return newArray;
	}
	
	@SuppressWarnings("unchecked")
	public static <A> A[] pop(
			A[] oldArray) {
		A[] newArray = (A[]) Array.newInstance(oldArray.getClass()
				.getComponentType(), oldArray.length - 1);

//		A oldObj = oldArray[0];
        System.arraycopy(oldArray, 1, newArray, 0, oldArray.length - 1);
		return newArray;
	}

	public static void reverse(byte[] byteArray) {
		for (int i = 0; i < byteArray.length / 2; i++) {
			byte temp = byteArray[i];
			byteArray[i] = byteArray[byteArray.length -1 -i];
			byteArray[byteArray.length -1 -i] = temp;
		}
	}

	public static Character[] chars(char[] charArray) {
		Character[] chars = new Character[charArray.length];
		
		for (int i = 0; i < chars.length; i++) {
			chars[i] = charArray[i];
		}
		
		return chars;
	}

	public static String join(char[] objs, String delimiter) {
        if (objs == null) {
            return "";
        }
		StringBuffer sb = new StringBuffer();
		for (char a : objs) {
			sb.append(a).append(delimiter);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - delimiter.length());
		}
		return sb.toString();
	}
	

	/**
	 * Duplicated elements stay together
	 * @param list
	 * @return
	 */
	public static int[] removeDuplicates2(
			int[] list) {
		int last = Integer.MIN_VALUE;
		int count = 0;
		for (int e : list) {
			if (last == Integer.MIN_VALUE || last != (e)) {
				count ++;
			}
			
			last = e;
		}
		int[] ret = new int[count];

		last = Integer.MIN_VALUE;
		int i = 0;
		for (int e : list) {
			if (last == Integer.MIN_VALUE || last != (e)) {
				ret[i] = e;
				i++;
			}
			last = e;
		}
		return ret;
	}

}
