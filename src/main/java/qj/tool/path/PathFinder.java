package qj.tool.path;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import qj.util.Cols;
import qj.util.ObjectUtil;
import qj.util.funct.F1;
import qj.util.funct.P1;

@SuppressWarnings("unchecked")
public class PathFinder {
	public static <A> List<A> findPath(final Collection<A> targets, Collection<A> froms, F1<A,Iterable<A>> surroundF) {
		// Validation
		if (Cols.isEmpty(targets)) {
			throw new IllegalArgumentException("Targets is empty");
		} else if (Cols.isEmpty(froms)) {
			throw new IllegalArgumentException("Froms is empty");
		}
		
		// Check distance 0
		
		if (Cols.containAny(targets, froms) != null) {
			return Cols.toList(targets);
		}
		
		// Check further
		final SmartLines<A> fromLines = new SmartLines<A>(froms, surroundF);
		
		SmartLines<A> targetLines = new SmartLines<A>(targets, surroundF);
		
		final AtomicReference<List<A>> ref = new AtomicReference<List<A>>();
		
		while (true) {
			// TODO Other direction (fromLines to targetLines)
			targetLines.nextAndCheck(fromLines,new P1<List<A>>() {public void e(List<A> linkLine) {
				ref.set(linkLine);
			}});
			
			if (ref.get() != null) {
				return ref.get();
			}
			if (targetLines.deadEnd()) {
				return null;
			}
		}
	}
	
	/**
	 * Not step on it's own foot
	 * @author quanle
	 *
	 * @param <A>
	 */
	public static class SmartLines<A> {
		HashSet<A> visited;
		LinkedList<LinkedList<A>> lines;
		private F1<A, Iterable<A>> surroundF;
		
		public SmartLines(Collection<A> froms, F1<A,Iterable<A>> surroundF) {
			this.surroundF = surroundF;
			visited = new HashSet<A>(froms);
			lines = new LinkedList<LinkedList<A>>();
			for (A a : froms) {
				lines.add((LinkedList<A>) Cols.addList(a, null));
			}
		}
		
		public boolean deadEnd() {
			return Cols.isEmpty(lines);
		}

		public void next(P1<LinkedList<A>> p) {
			LinkedList<LinkedList<A>> newLines = new LinkedList<LinkedList<A>>();
			for (LinkedList<A> line : lines) {
				Iterable<A> lastSurround = surroundF.e(line.getLast());
				if (lastSurround == null) {
					continue;
				}
				for (A a : lastSurround) {
					if (visited.contains(a)) {
						continue;
					}
					visited.add(a);
					
					LinkedList<A> newLine = new LinkedList<A>(line);
					newLine.add(a);
					newLines.add(newLine);
					if (p != null) {
						p.e(newLine);
					}
				}
			}
			lines = newLines;
		}
		
		public void nextAndCheck(final SmartLines<A> otherLines, final P1<List<A>> p) {
			this.next(new P1<LinkedList<A>>() {public void e(final LinkedList<A> thisLine) {
				otherLines.eachLinkLine(thisLine, p);
			}});
		}
		
		public void eachLinkLine(final LinkedList<A> targetLine, final P1<List<A>> p) {

			A a = targetLine.getLast();
			
			meet(a, new P1<List<A>>() {public void e(List<A> fromLine) {
				LinkedList<A> linkingLine = new LinkedList<A>(fromLine);
				Iterator<A> iter = targetLine.descendingIterator();
				iter.next();
				
				while (iter.hasNext()) {
					linkingLine.add(iter.next());
				}
				p.e(linkingLine);
			}});
		}
		
		public void meet(A a, P1<List<A>> p) {
			for (LinkedList<A> line : lines) {
				if (ObjectUtil.equals(line.getLast(), a)) {
					p.e(line);
				}
			}
		}
	}


	/**
	 * Not step on it's own foot
	 * @author quanle
	 *
	 * @param <A>
	 */
	public static class Lines<A> {
		LinkedList<LinkedList<A>> lines;
		private F1<A, Collection<A>> surroundF;
		
		public Lines(Collection<A> froms, F1<A,Collection<A>> surroundF) {
			this.surroundF = surroundF;
			lines = new LinkedList<LinkedList<A>>();
			for (A a : froms) {
				lines.add((LinkedList<A>) Cols.addList(a, null));
			}
		}
		
		/**
		 * 
		 * @param onLineEnded
		 * @return ended
		 */
		public boolean next(F1<List<A>, Boolean> onLineEnded) {
			if (Cols.isEmpty(lines)) {
				throw new RuntimeException("No more line can be found");
			}
			LinkedList<LinkedList<A>> newLines = new LinkedList<LinkedList<A>>();
			for (LinkedList<A> line : lines) {
				Collection<A> lastSurround = surroundF.e(line.getLast());
				if (Cols.isEmpty(lastSurround)) {
					// Ended line
					if (onLineEnded != null && onLineEnded.e(line)) {
						return true;
					}
					continue;
				}
				for (A a : lastSurround) {
					LinkedList<A> newLine = new LinkedList<A>(line);
					newLine.add(a);
					newLines.add(newLine);
				}
			}
			lines = newLines;
			return lines.isEmpty();
		}
	}
	
	public static <N> boolean allLines(int length, N nodeStart, F1<N,Collection<N>> surroundF, F1<List<N>, Boolean> lineF) {
		if (length<=0) {
			return false;
		}
		Lines<N> lines = new Lines<N>(Arrays.asList(nodeStart), surroundF);
		for (int i = 0; i < length - 1; i++) {
			if (lines.next(null)) {
				break;
			}
		}
		for (LinkedList<N> line : lines.lines) {
			if (lineF.e(line)) {
				return true;
			}
		}
		return false;
	}
	public static <N> void allLines(List<N> nodeStarts, F1<N,Collection<N>> surroundF, F1<List<N>, Boolean> lineF) {
		Lines<N> lines = new Lines<N>(nodeStarts, surroundF);
		while (true) {
			if (lines.next(lineF)) {
				return;
			}
		}
	}
	
	public static <N> LinkedList<Set<N>> getSteps(Collection<N> starts, N to, F1<N,Iterable<N>> surroundF) {
		LinkedList<Set<N>> steps = new LinkedList<>();
		Set<N> thisStep = Cols.toSet(starts);
		steps.add(thisStep);
		
		Set<N> lastStep = Collections.emptySet();
		for (boolean cont = true;cont;) {
			HashSet<N> nextStep = new HashSet<>();
			for (N thisStepNode : thisStep) {
				for (N next : surroundF.e(thisStepNode)) {
					if (to != null && to.equals(next)) {
						cont = false;
					}
					if (!lastStep.contains(next) && !thisStep.contains(next)) {
						nextStep.add(next);
					}
				}
			}
			steps.add(nextStep);
			lastStep = thisStep;
			thisStep = nextStep;
		}
		return steps;
	}

	/**
	 * Lines that do not need to start with nodeStart
	 * @param length
	 * @param nodeStart
	 * @param surroundF
	 * @param lineF
	 * @return 
	 */
	public static <N> boolean allFreeLines(int length, N nodeStart, F1<N,Collection<N>> surroundF, F1<List<N>, Boolean> lineF) {
		if (length<=0) {
			return false;
		}
		LinkedList<N> usedToStart = new LinkedList<N>();
		
		LinkedList<N> stack = new LinkedList<N>();
		stack.add(nodeStart);
		
		while (!stack.isEmpty()) {
			N node = stack.removeFirst();
			if (Cols.containsPointer(node, usedToStart)) {
				continue;
			}
			if (allLines(length, node, surroundF, lineF)) {
				return true;
			}
			
			usedToStart.add(node);
			
			// Next
			Iterable<N> surround = surroundF.e(node);
			if (surround != null) {
				stack.addAll(Cols.toList(surround));
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		Node1<String> a1 = new Node1<String>("a1");
		Node1<String> a2 = new Node1<String>("a2");
		Node1<String> b  = new Node1<String>("b");
		Node1<String> c1 = new Node1<String>("c1");
		Node1<String> c2 = new Node1<String>("c2");
		
		a1.addNext(b);
		a2.addNext(b);
		b.addNext(c1);
		b.addNext(c2);
		
		allLines(Arrays.asList(a1,a2), Node1.<String>nextF(), new F1<List<Node1<String>>,Boolean>() {public Boolean e(List<Node1<String>> obj) {
			System.out.println(obj);
			return false;
		}});
	}
}
