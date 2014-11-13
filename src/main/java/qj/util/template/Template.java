package qj.util.template;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;

import qj.util.Cols;
import qj.util.IOUtil;
import qj.util.RegexUtil;
import qj.util.funct.Fs;
import qj.util.funct.P1;
import qj.util.funct.P2;
import qj.util.math.Range;
import qj.util.structure.StructureBuilder;
import qj.util.structure.TagRange;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Template {
	HashMap<String, P2<Map, Writer>> allParts = new HashMap<String, P2<Map,Writer>>();

	public static Template compile(Class<?> clazz, String resourceName) {
		
		InputStream resIn = clazz.getResourceAsStream(resourceName);
		if (resIn == null) {
			return null;
		}
		
		Template ret = new Template();
		String allPartContent = IOUtil.toString(resIn, "UTF-8");
		
		Matcher m = RegexUtil.matcher("<t:part name=\"(.+?)\">", allPartContent);
		int index = 0;
		while (m.find(index)) {
			String partName = m.group(1);
			index = m.end();
			int endTagPos = allPartContent.indexOf("</t:part>", index);
			String partContent = allPartContent.substring(index, endTagPos);
			ret.allParts.put(partName, compilePart(partContent));
			index = endTagPos + "</t:part>".length();
		}
		return ret;
	}
	public static Template compileSimple(Class<?> clazz, String resourceName) {
		String allContent = IOUtil.toString(clazz.getResourceAsStream(resourceName), "UTF-8");

		return compileSimple(allContent);
	}

	public static Template compileSimple(String allContent) {
		Template ret = new Template();
		ret.allParts.put(null, compilePart(allContent));
		return ret;
	}


	static P2<Map,Writer> compilePart(final String content) {
		final StructureBuilder structure = parse(content);
		final LinkedList<P2<Map, Writer>> retList = new LinkedList<P2<Map, Writer>>();
		structure.eachTagRangeSorted(new Class[] {TPart.class, Plain.class,Variable.class}, new P1<TagRange>() {public void e(TagRange tr) {
			Object tag = tr.tag;
			
			if (tag instanceof TPart) {
				TPart tPart = (TPart) tag;
				final String name = tPart.name;
				retList.add(new P2<Map, Writer>() {public void e(Map vars, Writer writer) {
					HashMap<String, P2<Map, Writer>> parts = (HashMap<String, P2<Map, Writer>>) vars.get("t:parts");
					P2<Map, Writer> partWrite = parts.get(name);
					if (partWrite != null) {
						partWrite.e(vars, writer);
					}
				}});
			} else if (tag instanceof Variable) {
				Variable var = (Variable) tag;
				final String name = var.name;
				retList.add(new P2<Map, Writer>() {public void e(Map vars, Writer writer) {
					Object value = vars.get(name);
					try {
						writer.write(String.valueOf(value));
					} catch (IOException e1) {
						throw new RuntimeException(e1.getMessage(), e1);
					}
				}});
			} else {
			// if (tag.equals(Plain.class)) {
				final String plain = structure.getValue(tr.range);
				retList.add(new P2<Map, Writer>() {public void e(Map vars, Writer writer) {
					try {
						writer.write(plain);
					} catch (IOException e1) {
						throw new RuntimeException(e1.getMessage(), e1);
					}
				}});
			}
				
		}});
		
		return Fs.sequel2(retList);
	}

	private static StructureBuilder parse(String content) {
		final StructureBuilder structure = new StructureBuilder(content);
		
		structure.cacheSeparate(TPart.class);
		RegexUtil.each("<t:part name=\"(.+?)\"/>", content, new P1<Matcher>() {public void e(Matcher m) {
			structure.fromto(m.start(), m.end(), new TPart(m.group(1)));
		}});
		
		structure.cacheSeparate(Variable.class);
		
		structure.eachRangeNot(new Class[] {TPart.class}, new P1<Range>() {public void e(final Range range) {
			RegexUtil.each("\\$(\\w+)\\|?", structure.getValue(range), new P1<Matcher>() {public void e(Matcher m) {
				structure.fromto(m.start() + range.getFrom(), m.end() + range.getFrom(), new Variable(m.group(1)));
			}});
		}});
		
		structure.cacheSeparate(Plain.class);
		structure.eachRangeNot(new Class[] {TPart.class, Variable.class}, new P1<Range>() {public void e(Range plainRange) {
			structure.range(plainRange, Plain.class);
		}});
		
		return structure;
	}
	
	public static class Plain {
		
	}
	public static class TPart {
		String name;

		public TPart(String name) {
			this.name = name;
		}
	}
	public static class Variable {
		String name;
		
		public Variable(String name) {
			this.name = name;
		}
	}

	public void write(Map<String, Object> vars, Layout layout,
			Writer writer) {
		vars.put("t:parts", allParts);
//		System.out.println(layout.temp);
		layout.write(layout.temp == null ? vars : Cols.merge(vars, layout.temp), writer);
	}
	
	public void write(Map<String, Object> vars,
			Writer writer) {
		P2<Map, Writer> p2 = allParts.get(null);
		p2.e(vars, writer);
	}

	public String toString(Map<String, Object> vars) {
		StringWriter writer = new StringWriter();
		write(vars, writer);
		return writer.toString();
	}

}
