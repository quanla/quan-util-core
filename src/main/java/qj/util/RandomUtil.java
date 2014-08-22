package qj.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {
	public static Random random = new Random();

    /**
     * Randomly select objects out of colection
     * @param num Max number of returned objects
     * @param objects Collection to get objects from
     * @param <A> Elem class
     * @return randomly chosen objects
     */
    public static <A> Collection<A> select(int num, Collection<A> objects) {
        num = Math.min(num, objects.size());
        List<A> list = Cols.toList(objects);
        Set<A> selected = new HashSet<A>();
        while (selected.size() < num) {
            selected.add(list.get(random.nextInt(list.size())));
        }
        return selected;
    }
    
    public static <A> A select(Collection<A> objects, Collection<A> except) {
        List<A> list = Cols.toList(objects);

        do {
            A a = list.get(random.nextInt(list.size()));
            if (!except.contains(a)) {
                return a;
            }
        } while (true);
    }
	public static int range(int from, int to) {
		return random.nextInt(to - from) + from;
	}

    public static <A> A choose(A... col) {
        if (col != null) {
            return col[random.nextInt(col.length)];
        } else {
            return null;
        }
    }

    public static <A> A choose(List<A> col) {
        if (Cols.isNotEmpty(col)) {
            return col.get(random.nextInt(col.size()));
        } else {
            return null;
        }
    }
    
	public static long long_() {
		return random.nextLong();
	}

	public static Integer nextInt(int n) {
		return random.nextInt(n);
	}
	
	public static String birthday(String df) {
		long from = System.currentTimeMillis() - 50*DateUtil.YEAR_LENGTH;
		return DateUtil.format(new Date((long) (random.nextDouble()*30*DateUtil.YEAR_LENGTH+from)), df);
	}

	public static boolean chooseBoolean(double chance) {
		return random.nextDouble() <= chance;
	}

}
