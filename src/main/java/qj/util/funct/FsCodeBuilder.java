package qj.util.funct;

import qj.util.Cols;
import qj.util.ReflectUtil;
import qj.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FsCodeBuilder {
    
    public static void main(String[] args) {
        int maxParam = 6 + 1;

        System.out.println("\n\t// Down F");
        for (int i = 0; i < maxParam; i++) {
            for (int j = i + 1; j < 4; j++) {
                System.out.println("\t" + tab(convert(i, j, ReflectUtil.newInstanceF1(F.class))));
            }
        }
        System.out.println("\n\t// Up F");
        for (int i = 0; i < maxParam; i++) {
            for (int j = 0; j < i; j++) {
                System.out.println("\t" + tab(convert(i, j, ReflectUtil.newInstanceF1(F.class))));
            }
        }

        System.out.println("\n\t// Down P");
        for (int i = 0; i < maxParam; i++) {
            for (int j = i + 1; j < maxParam; j++) {
                System.out.println("\t" + tab(convert(i, j, ReflectUtil.newInstanceF1(P.class))));
            }
        }
        System.out.println("\n\t// Up P");
        for (int i = 0; i < maxParam; i++) {
            for (int j = 0; j < i; j++) {
                System.out.println("\t" + tab(convert(i, j, ReflectUtil.newInstanceF1(P.class))));
            }
        }

        System.out.println("\n\t// Nothing P");
        for (int i = 0; i < maxParam; i++) {
            System.out.println("\t" + tab(doNothing(i, ReflectUtil.newInstanceF1(P.class))));
        }

        System.out.println("\n\t// Reflect");
        for (int i = 0; i < maxParam; i++) {
            System.out.println("\t" + tab(reflect(i, ReflectUtil.newInstanceF1(P.class))));
        }
        for (int i = 0; i < maxParam; i++) {
            System.out.println("\t" + tab(reflect(i, ReflectUtil.newInstanceF1(F.class))));
        }
        for (int i = 0; i < maxParam; i++) {
            System.out.println("\t" + tab(reflectObj(i, ReflectUtil.newInstanceF1(P.class))));
        }
        for (int i = 0; i < maxParam; i++) {
            System.out.println("\t" + tab(reflectObj(i, ReflectUtil.newInstanceF1(F.class))));
        }

        System.out.println("\n\t// F with fixed return");
        for (int i = 0; i < maxParam; i++) {
            System.out.println("\t" + tab(fixedReturn(i)));
        }

        System.out.println("\n\t// F with P and fixed return");
        for (int i = 0; i < maxParam; i++) {
            for (int j = 0; j <= i; j++) {
                System.out.println("\t" + tab(pToF(j, i)));
            }
        }
    }

    private static String convert(int to, int from, F1<Object, ? extends GenF> clazzF) {
        GenF fTo = clazzF.e(to);
        GenF fFrom = clazzF.e(from);
        GenF bigger = clazzF.e(Math.max(to, from));
        return
                "/**\n" +
                " * Convert from a " + fFrom.valName() + " to " + fTo.valName() + "\n" +
                " * @return " + fTo.valName() + "\n" +
                " */\n" +
                "public static " + Cols.join(" ", bigger.genericParam(), fTo.classDeclare()) + " " + fTo.valName() +
                "(" +
                Cols.join(Cols.merge("final " + fFrom.classDeclare() + " " + fFrom.valName(), methodParams(to, from)), ", ") + ") {\n" +
                "\treturn " + tab(fTo.newInstance(fTo.lastCall(fFrom.call() + ";"))) + "\n}";
    }


    private static String doNothing(int paramNum, F1<Object, ? extends GenF> clazzF) {
        GenF f = clazzF.e(paramNum);
        return
                "/**\n" +
                " * Do nothing\n" +
                " * @return f that do nothing upon invocation\n" +
                " */\n" +
                "public static " + Cols.join(" ", f.genericParam(), f.classDeclare()) + " " + f.valName() +
                "() {\n" +
                "\treturn " + tab(f.newInstance("")) + "\n}";
    }


    private static String fixedReturn(int paramNum) {
        F f = new F(paramNum);
        return
                "/**\n" +
                " * Return fixed value\n" +
                " * @param ret the fixed value to return\n" +
                " * @return ret\n" +
                " */\n" +
                "public static " + Cols.join(" ", f.genericParam(), f.classDeclare()) + " " + f.valName() +
                "(final R ret) {\n" +
                "\treturn " + tab(f.newInstance("return ret;")) + "\n}";
    }


    private static String pToF(int pp, int fp) {
        F f = new F(fp);
        P p = new P(pp);
        return
                "/**\n" +
                " * Call to p and return fixed value\n" +
                " * @param " + p.valName() + " the function to call before return value\n" +
                " * @param ret the fixed value to return\n" +
                " * @return ret\n" +
                " */\n" +
                "public static " + Cols.join(" ", f.genericParam(), f.classDeclare()) + " " + f.valName() +
                "(final " + p.classDeclare() + " " + p.valName() + ", final R ret) {\n" +
                "\treturn " + tab(f.newInstance(
                        p.valName() + ".e(" + params(pp) + ");\n" +
                        "return ret;")) + "\n}";
    }

    private static String reflect(int paramNum, F1<Object, ? extends GenF> clazzF) {
        GenF f = clazzF.e(paramNum);
        return
                "/**\n" +
                " * Reflect call to method of class\n" +
                " * @param method Name of the class's method\n" +
                " * @param clazz Class that contains the method\n" +
                " * @return f use reflection to call to method of class\n" +
                " */\n" +
                "public static " + Cols.join(" ", f.genericParam(), f.classDeclare()) + " " + f.valName() +
                "(final String method, final Class<?> clazz) {\n" +
                "\tMethod m = ReflectUtil.deepFindMethod(method, clazz );\n" +
                "\treturn f(m, null);" +
                "\n}";
    }
    private static String reflectObj(int paramNum, F1<Object, ? extends GenF> clazzF) {
        GenF f = clazzF.e(paramNum);
        return
                "/**\n" +
                " * Reflect call to method\n" +
                " * @param method Name of the method\n" +
                " * @param object object to invoke the method on\n" +
                " * @return f use reflection to call to method\n" +
                " */\n" +
                "public static " + Cols.join(" ", f.genericParam(), f.classDeclare()) + " " + f.valName() +
                "(final String method, final Object object) {\n" +
                "\tMethod m = ReflectUtil.deepFindMethod(method, null,object.getClass());\n" +
                "\treturn f(m, object);" +
                "\n}";
    }

    private static List<String> methodParams(int from, int to) {
        ArrayList<String> params = new ArrayList<String>();
        for (int i = from; i < to; i++) {
            char c = toChar(i);
            params.add("final " + Character.toUpperCase(c) + " " + c);
        }
        return params;
    }
    static String params(int paramNum) {
        ArrayList<Character> params = new ArrayList<Character>();
        for (int i = 0; i < paramNum; i++) {
            params.add(toChar(i));
        }
        return Cols.join(params, ", ");
    }           
    private static char toChar(int i) {
        return ((char)((int)'a' + i));
    }

    static String tab(String code) {
        return code.replaceAll("(\r?\n)", "$1\t");
    }

    public static abstract class GenF {
        int paramNum = 0;
        abstract String genericParam();
        /**
         *
         * @return <A, B>
         */
        abstract String newInstance(String code);
        abstract String classDeclare();
        abstract String call();
        abstract String valName();

        public abstract String lastCall(String callCode);
    }
    public static class F extends GenF {
        public F(int paramNum) {
            this.paramNum = paramNum;
        }

        public String genericParam() {
            return "<" + Cols.join(", ", params(paramNum).toUpperCase(), "R") + ">";
        }
        public String newInstance(String code) {
            return "new " + classDeclare() + "(){public R e(" + Cols.join(methodParams(0, paramNum), ", ") + ") {\n" +
                    (StringUtil.isNotEmpty(code) ? "\t" + tab(code) + "\n" : "") +
                    "}};";
        }

        public String classDeclare() {
            return "F" + paramNum + genericParam();
        }

        public String call() {
            return "f" + paramNum + ".e(" + params(paramNum) + ")";
        }

        public String valName() {
            return "f" + paramNum;
        }

        public String lastCall(String callCode) {
            return "return " + callCode;
        }
    }

    public static class P extends GenF {
        public P(int paramNum) {
            this.paramNum = paramNum;
        }
        public String genericParam() {
            String params = params(paramNum);
            return StringUtil.isEmpty(params) ? "" : "<" + params.toUpperCase() + ">";
        }
        public String newInstance(String code) {
            return "new " + classDeclare() + "(){public void e(" + Cols.join(methodParams(0, paramNum), ", ") + ") {\n" +
                    (StringUtil.isNotEmpty(code) ? "\t" + tab(code) + "\n" : "") +
                    "}};";
        }

        public String classDeclare() {
            return "P" + paramNum + genericParam();
        }

        public String call() {
            return "p" + paramNum + ".e(" + params(paramNum) + ")";
        }
        public String valName() {
            return "p" + paramNum;
        }
        public String lastCall(String callCode) {
            return callCode;
        }
    }
}
