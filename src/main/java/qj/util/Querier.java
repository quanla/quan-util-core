package qj.util;

import qj.util.funct.P1;

public interface Querier {
    void through(P1<String> f);

    String get(int i);

    String end(P1<Integer> lineIndex);
}