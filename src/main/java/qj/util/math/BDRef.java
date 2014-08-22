package qj.util.math;

import java.math.BigDecimal;

/**
 * BigDecimal Reference
 * @author quanle
 *
 */
public class BDRef {
	public BigDecimal val;
	public BDRef() {
		val = BigDecimal.ZERO;
	}
	public BDRef(double val) {
		this.val = BigDecimal.valueOf(val);
	}
	public void add(BigDecimal v) {
		val = val.add(v);
	}
	public void multiply(BigDecimal v) {
		val = val.multiply(v);
	}
	public String toString() {
		return val.toPlainString();
	}
	@Override
	public int hashCode() {
		return val.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BDRef other = (BDRef) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	
}
