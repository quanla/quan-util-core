package qj.util.template;

import java.io.Writer;
import java.util.Map;

import qj.util.IOUtil;
import qj.util.funct.P2;

public class Layout {
	P2<Map, Writer> p;
	Map<String, Object> temp;
	

	public static Layout compile(Class<?> clazz, String resourceName) {
		String content = IOUtil.toString(clazz.getResourceAsStream(resourceName), "UTF-8");
		Layout ret = new Layout();
		ret.p = Template.compilePart(content);
		return ret;
	}

	public void write(Map<String, Object> vars, Writer writer) {
		p.e(vars, writer);
	}

	public Layout temp(Map<String, Object> map) {
		Layout ret = new Layout();
		ret.p = p;
		ret.temp = map;
		return ret;
	}
}
