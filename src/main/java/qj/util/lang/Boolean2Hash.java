package qj.util.lang;

import java.io.Serializable;
import java.util.Arrays;

public class Boolean2Hash implements Serializable {
	public final boolean[][] val;

	public Boolean2Hash(boolean[][] val) {
		this.val = val;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(val);
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
		Boolean2Hash other = (Boolean2Hash) obj;
		if (!Arrays.deepEquals(val, other.val))
			return false;
		return true;
	}
	
	
}
