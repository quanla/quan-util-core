package qj.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P1;
import qj.util.funct.P2;
import qj.util.math.Range;
import qj.util.structure.Structure;
import qj.util.structure.StructureBuilder;

public class RegexUtil extends RegexUtil4 {
    public static F1<Object,Pattern> compileF = new F1<Object, Pattern>() {
        public Pattern e(Object obj) {
            return Pattern.compile(obj.toString());
        }
    };

    public static F1<Object,Pattern> patternCache = Fs.<Object,Pattern>cache(compileF);

    @SuppressWarnings("unchecked")
	public static String replaceAll(final String str, F1<String, String> forNonMatch, Object... replaces) {
        List<Object> patterns = Cols.filter(replaces, Fs.<Object, Boolean>f1(MathUtil.onoff()));
        List<Object> reps = Cols.filter(replaces, Fs.<Object, Boolean>f1(Fs.not(MathUtil.onoff())));
        List<Matcher> matchers = Cols.yield(patterns, Fs.chain(compileF, new F1<Pattern, Matcher>() {public Matcher e(Pattern obj) {
            return obj.matcher(str);  //To change body of implemented methods use File | Settings | File Templates.
        }}));

        StringBuffer sb = new StringBuffer();
        int index = 0;
        while (true) {
            int minIndex = Integer.MAX_VALUE;
            Matcher minMatcher = null;
            int matcherIndex = -1;

            for (int i = 0, matchersSize = matchers.size(); i < matchersSize; i++) {
                Matcher matcher = matchers.get(i);
                if (matcher.find(index)) {
                    int pos = matcher.start();
                    if (pos < minIndex) {
                        minIndex = pos;
                        minMatcher = matcher;
                        matcherIndex = i;

                        if (minIndex == 0) {
                            break;
                        }
                    }
                }
            }

            if (minMatcher == null) {
                break;
            }

            Object rep = reps.get(matcherIndex);
            sb.append(forNonMatch.e(str.substring(index, minMatcher.start())));
            if (rep instanceof String) {
                sb.append(rep.toString());
            } else { // if (rep instanceof F1)
                sb.append(((F1<String, String>) rep).e(minMatcher.group()));
            }
            index = minMatcher.end();
        }

        sb.append(forNonMatch.e(str.substring(index)));
        return sb.toString();
    }
    @SuppressWarnings("unchecked")
	public static String replaceAll2(String str, Object... replaces) {
        List<Object> patterns = Cols.filter(replaces, Fs.<Object, Boolean>f1(MathUtil.onoff()));
        List<Object> reps = Cols.filter(replaces, Fs.<Object, Boolean>f1(Fs.not(MathUtil.onoff())));
        Pattern mainPtn = Pattern.compile(Cols.join(Cols.yield(patterns, new F1<Object, String>() {
            public String e(Object obj) {
                return "(" + obj + ")";
            }
        }), "|"));
        StringBuffer sb = new StringBuffer();
        Matcher m = mainPtn.matcher(str);
        while (m.find()) {
            Object rep = null;
            for (int i = 0; i < reps.size(); i++) {
                if (m.group(i+1) != null) {
                    rep = reps.get(i);
                    break;
                }
            }
            if (rep instanceof String) {
                m.appendReplacement(sb, rep.toString());
            } else { // if (rep instanceof F1)
                m.appendReplacement(sb, ((F1<String, String>) rep).e(m.group()));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

//    public static void main(String[] args) {
//        System.out.println(replaceAll("a bb esr", "a", "@@", "bb", "^^", "awr", ""));
//    }

    public static String[] groups(Matcher matcher) {
        String[] ret = new String[matcher.groupCount()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = matcher.group(i + 1);
        }
        return ret;
    }

    public static String patternize(Collection<String> col) {
        if (matchAll("\\d*", col)) {
            return "\\d" + greedy(Cols.range(col, StringUtil.length));
        } else if (matchAll("\\w*", col)) {
            return "\\w" + greedy(Cols.range(col, StringUtil.length));
        } else {
            return null;
        }
    }
                
    private static boolean matchAll(String ptn, Collection<String> col) {
        Pattern pattern = Pattern.compile(ptn);
        for (String s : col) {
            if (!pattern.matcher(s).matches()) {
                return false;
            }
        }
        return true;
    }

    private static String greedy(Range range) {
        if (range.isEmpty()) {
            if (range.getFrom() < 2) {
                return "";
            } else {
                return "{" + range.getFrom() + "}";
            }
        } else {
            if (range.getFrom() == 0) {
                return range.getTo() == 1 ? "?" : "*";
            } else {
                return "+";
            }
        }
    }


    static Fs.F1Cache<String, Pattern> cachedCompileF = Fs.cache(new F1<String, Pattern>() {public Pattern e(String obj) {
        return Pattern.compile(obj);
    }});
    public static Pattern pattern(String ptn) {
        return cachedCompileF.e(ptn);
    }
	public static String toString(Matcher matcher) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= matcher.groupCount(); i++) {
			sb.append("Group ").append(i).append(": ").append(matcher.group(i)).append("\n");
		}
		
		return sb.toString();
	}
	public static boolean endsWith(String ptn, String str) {
		return Pattern.compile(ptn + "$").matcher(str).find();
	}
	
	public static boolean startWith(String ptn, String str) {
		return Pattern.compile("^" + ptn).matcher(str).find();
	}

	
	public static Matcher lastFound(Pattern ptn, int pos, CharSequence text) {
		Matcher matcher = ptn.matcher(text);
		int last = -1;
		while (matcher.find()) {
			int start = matcher.start();
//			if (start==pos) {
//				return matcher;
//			} else 
			if (start >= pos) {
				if (last > -1) {
					matcher.find(last - 1);
					return matcher;
				} else {
					return null;
				}
			}
			last = start;
		}
		if (last > -1) {
			matcher.find(last);
			return matcher;
		} else {
			return null;
		}
	}
	public static List<StringChange> replaceAll(
			F1<Matcher, String> f1, 
			Pattern ptn,
			String text) {
		return replaceAll(f1, ptn, text, 0, -1);
	}

	public static String replaceAll(String text,String ptn, F1<Matcher, String> f1) {
		return StringChange.apply(replaceAll(f1, compileF.e(ptn), text), text);
	}
	
	public static List<StringChange> replaceAll(
			F1<Matcher, String> f1, 
			Pattern ptn,
			String text,
			int from,
			int to) {
		Matcher matcher = ptn.matcher(text);
		
		ArrayList<StringChange> changes = new ArrayList<StringChange>();
		if (matcher.find(from)) {
			do {
				Matcher m = ptn.matcher(matcher.group(0));
				m.matches();
				StringBuffer sb = new StringBuffer();
				m.appendReplacement(sb, f1.e(m));
				changes.add(StringChange.replace(matcher.start(), matcher.end(), sb.toString()));
			} while (matcher.find() && (to==-1 || matcher.start() < to));
		}
		return changes;
	}
	
	public static void main(String[] args) {
//		System.out.println(structure("syslog:vormetric:SOURCE\\[([^\\]]++)\\]:(\\w+(\\w)):(.*+)"));
		System.out.println(structure("([^\\]]++)\\]:(\\w+(\\w)):(.*+)"));
	}
	
	public static Structure structure(String value) {
		StructureBuilder structureBuilder = new StructureBuilder(value);
		structureBuilder.layered(Group.class);
		char[] chars = value.toCharArray();
		int groupIndex = 1;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			
			if (structureBuilder.in("escape")) {
				structureBuilder.endSingle("escape", i+1);
				continue;
			}

			if (c == '\\') {
				structureBuilder.into("escape", i);
				continue;
			}

			if (c == '[') {
				structureBuilder.into("squareBracket", i);
				continue;
			}

			if (c == ']' && structureBuilder.in("squareBracket")) {
				structureBuilder.endSingle("squareBracket", i + 1);
				continue;
			}

			if (c == '(' && !structureBuilder.in("squareBracket")) {
				if (structureBuilder.peek(1, i + 1).equals("?")) {
					structureBuilder.into(new NonCapturingGroup(), i + 1);
				} else {
					structureBuilder.into(new Group(groupIndex++), i + 1);
				}
				
				continue;
			}
			if (c == ')' && !structureBuilder.in("squareBracket")) {
				structureBuilder.endSingle(AnyGroup.class, i);
				continue;
			}
		}
		return structureBuilder.build();
	}

	public static class AnyGroup {
	}
	public static class NonCapturingGroup extends AnyGroup {

		public NonCapturingGroup() {
		}
		
	}
	
	public static class Group extends AnyGroup implements Comparable<Group> {
		public final int index;
		public Group(int index) {
			this.index = index;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
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
			Group other = (Group) obj;
			if (index != other.index)
				return false;
			return true;
		}
		public int compareTo(Group o) {
			return index - o.index;
		}
		@Override
		public String toString() {
			return "Group[" + index + "]";
		}
		
	}
	public static Integer getInt(String key, String ptn) {
		Pattern PTN = patternCache.e(ptn);
		
		return getInt(key, PTN);
	}
	public static Integer getInt(String key, Pattern ptn) {
		Matcher matcher = ptn.matcher(key);
		if (!matcher.find()) {
			return null;
		}
		return Integer.valueOf(matcher.group(1));
	}
	
	public static boolean contains(String ptn, String text) {
		if (text != null) {
			Pattern ptnO = cachedCompileF.e(ptn);
			return ptnO.matcher(text).find();
		} else {
			return false;
		}
	}
	public static boolean matches(String key, Pattern ptn) {
		return ptn.matcher(key).matches();		
	}
	public static boolean matchesAny(String[] keys, Pattern ptn) {
		for (String key : keys) {
			if (key==null) {
				continue;
			}
			if (ptn.matcher(key).matches()) {
				return true;
			}
		}
		return false;
	}
	public static boolean matches(String key, String ptn) {
		return compileF.e(ptn).matcher(key).matches();		
	}
	public static F1<String,String> getF(String ptn) {
		final Pattern oPtn = Pattern.compile(ptn);
		return new F1<String, String>() {
			
			public String e(String str) {
				Matcher matcher = oPtn.matcher(str);
				if (matcher.find()) {
					return matcher.group(1);
				} else {
					return null;
				}
			}
		};
	}
	public static void each0(Pattern ptn, String content, P2<String, Integer> p2) {
		Matcher matcher = ptn.matcher(content);
		while (matcher.find()) {
			p2.e(matcher.group(), matcher.start());
		}
	}
	public static int indexOf(String ptn, int groupId, String text) {
		int from = 0;
		return indexOf(ptn, groupId, text, from);
	}
	public static int indexOf(String ptn, int groupId, String text, int from) {
		Matcher matcher = Pattern.compile(ptn).matcher(text);
		if (matcher.find(from)) {
			return matcher.start(groupId);
		} else {
			return -1;
		}
	}
	
	/**
	 * Get the string group out of given text
	 * @param ptnStr
	 * @param groupId
	 * @param text
	 * @return
	 */
	public static String getString(String ptnStr, int groupId, String text) {
		if (text==null) {
			return null;
		}
		Pattern ptn = cachedCompileF.e(ptnStr); //Pattern.compile(ptnStr)
		return getString(ptn, groupId, text);
	}
	/**
	 * Get the string group out of given text
	 * @param ptn
	 * @param groupId
	 * @param text
	 * @return
	 */
	public static String getString(Pattern ptn, int groupId, String text) {
		Matcher matcher = ptn.matcher(text);
		if (matcher.find()) {
			return matcher.group(groupId);
		} else {
			return null;
		}
	}
	public static Integer getGroup(int pos, Matcher matcher) {
		for (int i = matcher.groupCount();i>-1;i--) {
			if (pos >= matcher.start(i) && 
					pos <= matcher.end(i)) {
				return i;
			}
		}
		return null;
	}
	
	public static boolean find(Pattern ptn, String string) {
		return ptn.matcher(string).find();
	}
	public static boolean find(String ptn, String string) {
		return compileF.e(ptn).matcher(string).find();
	}
	public static String find(Pattern ptn, Collection<String> excepts, String string) {
		Matcher matcher = ptn.matcher(string);
		while (matcher.find()) {
			String val = matcher.group();
			if (!excepts.contains(val)) {
				return val;
			}
		}
		return null;
	}
	public static int countFinds(Pattern ptn, String string) {
		Matcher matcher = ptn.matcher(string);
		int count = 0;
//		System.out.println(string);
		while (matcher.find()) {
//			System.out.println(matcher.group());
			count++;
		}
		return count;
	}
	public static int countFinds(Pattern ptn, Collection<String> excepts, String string) {
		Matcher matcher = ptn.matcher(string);
		int count = 0;
//		System.out.println(string);
		while (matcher.find()) {
			if (!excepts.contains(matcher.group())) {
				count++;
			}
		}
		return count;
	}
	public static String lastFind(Pattern ptn, Collection<String> excepts, String string) {
		Matcher matcher = ptn.matcher(string);
		String last = null;
		while (matcher.find()) {
			String val = matcher.group();
			if (!excepts.contains(val)) {
				last = val;
			}
		}
		return last;
	}
	public static String nextFind(Pattern ptn, Collection<String> excepts, String string) {
		Matcher matcher = ptn.matcher(string);
		while (matcher.find()) {
			String val = matcher.group();
			if (!excepts.contains(val)) {
				return val;
			}
		}
		return null;
	}
	public static String lastFind(Pattern ptn, int group, String string) {
		Matcher matcher = ptn.matcher(string);
		String last = null;
		while (matcher.find()) {
			last = matcher.group(group);
		}
		return last;
	}
	public static void each(String ptn, String content, P1<Matcher> p1) {
		each(compileF.e(ptn), content, p1);
	}
	public static void each(Pattern ptn, String content, P1<Matcher> p1) {
		Matcher matcher = ptn.matcher(content);
		while (matcher.find()) {
			p1.e(matcher);
		}
	}
	public static void each(String ptnStr, String content, P1<Matcher> matchP,
			P1<String> unmatchP) {
		each(ptnStr, content, matchP, Fs.<String,Integer>p2(unmatchP));
	}
	public static void each(String ptnStr, String content, P1<Matcher> matchP,
			P2<String,Integer> unmatchP) {
		int index = 0;
		Matcher m = compileF.e(ptnStr).matcher(content);
		while (m.find()) {
			if (m.start() > index) {
				unmatchP.e(content.substring(index, m.start()), index);
			}
			matchP.e(m);
			index = m.end();
		}
		if (index < content.length()) {
			unmatchP.e(content.substring(index), index);
		}
	}

	public static Matcher matcher(String regex, String str) {
		return compileF.e(regex).matcher(str);
	}
	public static F1<String, String> replaceAllF(final String from, final String to) {
		return new F1<String, String>() {public String e(String obj) {
			return obj.replaceAll(from, to);
		}};
	}
	public static F1<String,Boolean> matchF(String regex) {
		final Pattern ptn = compileF.e(regex);
		return new F1<String, Boolean>() {public Boolean e(String str) {
			return ptn.matcher(str).matches();
		}};
	}
}
