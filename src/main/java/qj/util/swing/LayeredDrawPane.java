package qj.util.swing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;

import qj.util.Cols;
import qj.util.MathUtil;
import qj.util.SwingUtil;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;
import qj.util.funct.P2;
import qj.util.math.PointD;

public class LayeredDrawPane extends JComponent {
	private static P1<Draw> drawRectF(final Rectangle rect, final Color color) {
		return new P1<Draw>() {
			@Override
			public void e(Draw draw) {
				draw.brush.drawRect(rect.x, rect.y, rect.width, rect.height, color);
			}
		};
	}


	private static P1<Draw> drawImgF(final BufferedImage img,
			final qj.util.math.Rectangle rectangle) {
		return new P1<Draw>() {
			@Override
			public void e(Draw draw) {
				if (rectangle!=null) {
					draw.brush.drawImage(img, rectangle.x, rectangle.y, 1);
				} {
					draw.brush.drawImage(img, 0, 0, 1);
				}
			}
		};
	}


	public LinkedList<P1<Draw>> layers = new LinkedList<P1<Draw>>();

	@Override
	protected void paintComponent(final Graphics g) {

		BufferedImage i2 = new BufferedImage(
				getWidth(), getHeight(), 
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = i2.createGraphics();
		g2.setBackground(Color.lightGray);
		g2.clearRect(0, 0, i2.getWidth(), i2.getHeight());
		
		Draw draw = new Draw(g2, new Rectangle(0, 0, getWidth(), getHeight()), this);
		
		Fs.invokeAll(layers, draw);

		g.drawImage(i2, 0, 0, this);
	}
	

	public static class DrawLayer {
		public AtomicReference<P1<Draw>> layer = new AtomicReference<P1<Draw>>();
		public P1<Draw> drawer = Fs.atomicP1(layer);

		public void set(final Rectangle rect) {
			if (rect == null) {
				clear();
				return;
			}
			
			final Color color = Color.red;
			layer.set(drawRectF(rect, color));
		}

		public void clear() {
			layer.set(null);
		}

		public void set(final BufferedImage img) {
			set(img, null);
		}
		public void set(final BufferedImage img, final qj.util.math.Rectangle rectangle) {
			if (img == null) {
				clear();
				return;
			}
			
			layer.set(drawImgF(img, rectangle));
		}
	}
	
	public static class DrawGroupLayer {

		public java.util.List<P1<Draw>> drawers = new LinkedList<P1<Draw>>();
		
		public P1<Draw> drawer = Fs.invokeAllP1(drawers);
		
		public void clear() {
			drawers.clear();
		}
		
		public void addFilledCircle(final PointD point, final double radius, final Color color) {
			if (point==null) {
				return;
			}
			final Rectangle circleRect = circleRect(point, radius);
			addFilledCircle(color, circleRect);
		}

		private void addFilledCircle(final Color color,
				final Rectangle circleRect) {
			drawers.add(new P1<Draw>() {public void e(Draw draw) {
				draw.brush.graphics.setColor(color);
				draw.brush.graphics.fillOval(
						circleRect.x, 
						circleRect.y, 
						circleRect.width, 
						circleRect.height 
						);
			}});
		}

		private void addCircle(final Color color,
				final Rectangle circleRect) {
			drawers.add(new P1<Draw>() {public void e(Draw draw) {
				draw.brush.graphics.setColor(color);
				draw.brush.graphics.drawOval(
						circleRect.x, 
						circleRect.y, 
						circleRect.width, 
						circleRect.height 
						);
			}});
		}

		protected Rectangle circleRect(PointD point, double radius) {
			if (point==null) {
				return null;
			}
			return new Rectangle(
					(int) Math.round(point.x - radius), 
					(int) Math.round(point.y - radius),
					(int)(radius*2),
					(int)(radius*2)
					);
		}

		public void addCircle(final PointD point, final double radius, final Color color) {
			if (point==null) {
				return;
			}

			final Rectangle circleRect = circleRect(point, radius);
			addCircle(color, circleRect);
		}

		public void addLine(final List<PointD> line, final Color color) {
			if (Cols.isEmpty(line)) {
				return;
			}
			drawers.add(new P1<Draw>() {public void e(Draw draw) {
				Graphics g = draw.brush.graphics;
				g.setColor(color);

				PointD lastPoint = null;
				for (PointD p : line) {
					if (lastPoint!=null && !lastPoint.equals(p)) {
						g.fillOval(
								(int) Math.round(lastPoint.x -3), 
								(int) Math.round(lastPoint.y -3), 
								6, 6);
//						g.drawLine(
//								(int) Math.round(lastPoint.x), 
//								(int) Math.round(lastPoint.y), 
//								(int) Math.round(p.x), 
//								(int) Math.round(p.y));
					}
					lastPoint = p;
				}
			}});
		}

		public void addLineTri(final List<PointD> line, final Color color) {
			if (Cols.isEmpty(line)) {
				return;
			}
			drawers.add(new P1<Draw>() {public void e(Draw draw) {
				Graphics g = draw.brush.graphics;
				g.setColor(color);

				LinkedList<PointD> lastPoints = new LinkedList<PointD>();
				for (PointD p : line) {
					lastPoints.add(p);
					if (lastPoints.size() > 3) {
						lastPoints.removeFirst();
					}
					if (lastPoints.size() == 3) {
						g.fillPolygon(MathUtil.polygon(lastPoints));
					}
				}
			}});
		}
	}


	public DrawLayer addLayer() {
		DrawLayer imageLayer = new DrawLayer();
		layers.add(imageLayer.drawer);
		return imageLayer;
	}


	public void addLayer(final P1<Graphics> p1) {
		layers.add(new P1<Draw>() {public void e(Draw draw) {
			p1.e(draw.brush.graphics);
		}});
	}


	public SelectionLayer addSelectionLayer() {
		SelectionLayer selectionLayer = new SelectionLayer(this);
		layers.add(selectionLayer.drawer);
		return selectionLayer;
	}
	
	public DrawGroupLayer addDrawGroupLayer() {
		DrawGroupLayer drawGroupLayer = new DrawGroupLayer();
		layers.add(drawGroupLayer.drawer);
		return drawGroupLayer;
	}

	
	public static class SelectionLayer {
		Point from;
		Point to;

		public SelectionLayer(final LayeredDrawPane panel) {
			P1<Point> onStart = new P1<Point>() {public void e(Point point) {
				if (from!=null && to != null) {
					final Rectangle bound = SwingUtil.bound(from, to);
					SwingUtil.asyncRun(new P0() {public void e() {
						panel.repaint(SwingUtil.expand( bound, 1));
					}});
				}
				from = null;
				to = null;
			}};
			P2<Point, Point> onDrag = new P2<Point, Point>() {public void e(Point from1, Point to1) {
				final Rectangle bound = SwingUtil.bound(from, to, from1, to1);
				from = from1;
				to = to1;
				SwingUtil.asyncRun(new P0() {public void e() {
					panel.repaint(SwingUtil.expand( bound, 1) );
				}});
			}};
			SwingUtil.onDrag(panel, onStart, onDrag);
		}

		public P1<Draw> drawer = new P1<Draw>() {public void e(Draw draw) {
			if (from!=null && to != null) {
				final Rectangle bound = SwingUtil.bound(from, to);
				draw.brush.drawDashRect(bound, Color.GRAY);
			}
		}};

		public boolean isSelecting() {
			return from != null && to != null && from.x != to.x && from.y != to.y;
		}
		
		public qj.util.math.Rectangle getSelection() {
			if (from == null || to == null) {
				return null;
			} else {
				return qj.util.math.Rectangle.fromAwt(SwingUtil.bound(from, to));
			}
		}
	}


}
