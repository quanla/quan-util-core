package qj.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayUtil4 {

	public static int indexOf(int val, int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (val == array[i]) {
				return i;
			}
		}
		return -1;
	}
	
	public static int indexOf(String str, String[] strs) {
		for (int i = 0; i < strs.length; i++) {
			if (str.equals(strs[i])) {
				return i;
			}
		}
		return -1;
	}
	public static int indexOf(char c, char[] cs) {
		for (int i = 0; i < cs.length; i++) {
			if (c == cs[i]) {
				return i;
			}
		}
		return -1;
	}
	public static boolean contains(String expression, String[] fields) {
		return indexOf(expression, fields) > -1;
	}
	public static boolean contains(int i, int[] array) {
		return indexOf(i, array) > -1;
	}
	public static boolean contains(char c, char[] cs) {
		return indexOf(c, cs) > -1;
	}



	public static int[][] getIntArray2(String arr) {
		String[] lines = arr.split("\r?\n");
		String[][] vals = new String[lines.length][];
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			vals[i] = line.split("\\s+");
		}
		return parseInt(vals);
	}

	private static int[][] parseInt(String[][] vals) {
		int[][] ret = new int[vals.length][];
		for (int i = 0; i < vals.length; i++) {
			String[] chars = vals[i];
			ret[i] = new int[chars.length];
			for (int j = 0; j < chars.length; j++) {
				String c = chars[j];
				ret[i][j] = Integer.parseInt(c);
			}
		}
		return ret;
	}
	
    /**
     * Parse a list to an array
     * @param list
     * @return Array of data
     */
	public static Object listToArray(List list, Class componentClass) {
        Object[] array = (Object[]) Array.newInstance(componentClass, list.size());
        list.toArray(array);
        return array;
    }

	public static Object[] appendListToArray(List list, Object[] array) {

		Object[] a1 = (Object[]) Array.newInstance(array.getClass()
				.getComponentType(), list.size() + array.length);
		for (int i = 0; i < list.size(); i++) {
			Object o = (Object) list.get(i);
			a1[i + array.length] = o;
		}
		return a1;
	}

	public static void copy(Object[] from, Object[] to) {
		for (int i = 0; i < from.length; i++) {
			to[i] = from[i];
		}
	}

	public static int sum(int[] nums) {
		int result = 0;
		for (int i = 0; i < nums.length; i++) {
			result += nums[i];
		}
		return result;
	}

    /**
     * Subtitution for Arrays.asList. <br>
     * This implementation return an editable list (sizable) an non thread-safe
     * @param array - Array of elements converted into list
     */
	public static List asList(Object[] array) {
    	ArrayList list = new ArrayList();
    	
    	appendArrayToList(array, list);
    	
		return list;
    }

    /**
     * Append the array's elements into the existing list.
     * @param array - Array of elements to be appended into the list
     * @param list - The target list
     */
    public static void appendArrayToList(Object[] array, List list) {
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
    }
    
	public static Object[] flatten(Object[] array) {
		ArrayList result = new ArrayList();
		for (int i = 0; i < array.length; i++) {
			if (Object[].class.isAssignableFrom(array[i].getClass())) {
				appendArrayToList((Object[]) array[i], result);
			} else {
				result.add(array[i]);
			}
		}
		return result.toArray();
	}

	public static boolean isNotEmpty(String[] arrays) {
		return arrays !=null && arrays.length > 0;
	}
}
