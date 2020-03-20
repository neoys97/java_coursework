import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;

/**
 * 
 * Public class DrawPanel that inherits JPanel and implements MouseListener and MouseMotion Listener
 * Act as the draw canvas for the program
 * 
 * @author Neo Yi Siang
 *
 */
public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener{
	
	private ArrayList <Shape> shapeList = new ArrayList<Shape>();
	private ArrayList <Point2D> pointList = new ArrayList<Point2D>();
	private int mode = -1;
	private int count = 0;
	private int selected = -1;
	private JButton button1 = null;
	private JButton button2 = null;
	private JButton button3 = null;
	private JButton button4 = null;
	private Point2D start = null;
	private Point2D end = null;
	
	
	/**
	 * Constructor of the DrawPanel class
	 */
	public DrawPanel() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * Return the dimension of the required size
	 */
	public Dimension getPreferredSize() {
		return (new Dimension(400, 450));
	}
	
	/**
	 * Set the mode of DrawPanel and some buttons from the main program to be manipulated here
	 * @param m 0 : draw line
	 * 			1 : draw circle
	 * 			2 : draw triangle
	 * 			3 : select shape
	 * 			4 : move shape
	 * @param btn1 JButton component to be manipulated
	 * @param btn2 JButton component to be manipulated
	 * @param btn3 JButton component to be manipulated
	 * @param btn4 JButton component to be manipulated
	 */
	public void setMode (int m, JButton btn1, JButton btn2, JButton btn3, JButton btn4) {
		mode = m;
		button1 = btn1;
		button2 = btn2;
		button3 = btn3;
		button4 = btn4;
	}
	
	/**
	 * Set the mode of DrawPanel and a button from the main program to be manipulated here
	 * @param m 0 : draw line
	 * 			1 : draw circle
	 * 			2 : draw triangle
	 * 			3 : select shape
	 * 			4 : move shape
	 * @param JButton component to be manipulated
	 */
	public void setMode (int m, JButton btn) {
		mode = m;
		button1 = btn;
	}
	
	/**
	 * Remove the selected shape from the panel
	 */
	public void deleteSelected () {
		if (selected > -1)
			shapeList.remove(selected);
		selected = -1;
		repaint();
	}
	
	/**
	 * Add shape to the panel
	 * @param m 0 : line
	 * 			1 : circle
	 * 			2 : triangle
	 */
	public void addShape (int m) {
		mode = -1;
		if (m == 0) {
			Line2D.Float line = new Line2D.Float();
			line.setLine(pointList.get(0), pointList.get(1));
			shapeList.add(line);
			pointList.clear();
		}
		if (m == 1) {
			Ellipse2D.Float ellipse = new Ellipse2D.Float();
			double distance = (pointList.get(0).distance(pointList.get(1)));
			System.out.println(distance);
			ellipse.setFrame(pointList.get(0).getX()-distance, pointList.get(0).getY()-distance, distance*2, distance*2);
			shapeList.add(ellipse);
			pointList.clear();
		}
		if (m == 2) {
			int x[] = {(int)pointList.get(0).getX(), (int)pointList.get(1).getX(), (int)pointList.get(2).getX()};
			int y[] = {(int)pointList.get(0).getY(), (int)pointList.get(1).getY(), (int)pointList.get(2).getY()};
			shapeList.add(new Polygon(x, y, 3));
			pointList.clear();
		}
		repaint();
		enableButton(button1);
		button1 = null;
	}
	
	/**
	 * Disable a JButton component and change the background to gray colour
	 * @param btn JButton to be disabled
	 */
	public void disableButton (JButton btn) {
		btn.setEnabled(false);
		btn.setBackground(Color.gray);
	}
	
	/**
	 * Enable a JButton component and change the background to default colour
	 * @param btn JButton to be enabled
	 */
	public void enableButton (JButton btn) {
		btn.setEnabled(true);
		btn.setBackground(null);
	}
	
	/**
	 * Move the selected shape by a certain distance
	 * @param x_dist x distance
	 * @param y_dist y distance
	 */
	public void moveShape (int x_dist, int y_dist) {
		if (selected > -1) {
			Shape currentShape = shapeList.get(selected);
			if (currentShape instanceof Line2D.Float) {
				Point2D point1 = ((Line2D.Float) currentShape).getP1();
				Point2D point2 = ((Line2D.Float) currentShape).getP2();
				point1.setLocation(point1.getX()-x_dist, point1.getY()-y_dist);
				point2.setLocation(point2.getX()-x_dist, point2.getY()-y_dist);
				((Line2D.Float) currentShape).setLine(point1, point2);
				selected = -1;
			}
			if (currentShape instanceof Ellipse2D.Float) {
				double x = ((Ellipse2D.Float) currentShape).getX();
				double y = ((Ellipse2D.Float) currentShape).getY();
				double w = ((Ellipse2D.Float) currentShape).getWidth();
				double h = ((Ellipse2D.Float) currentShape).getHeight();
				((Ellipse2D.Float) currentShape).setFrame(x-x_dist, y-y_dist, w, h);
				selected = -1;
			}
			if (currentShape instanceof Polygon) {
				int x[] = ((Polygon) currentShape).xpoints;
				int y[] = ((Polygon) currentShape).ypoints;
				for (int i = 0; i < 3; i ++) {
					x[i] = x[i] - x_dist;
					y[i] = y[i] - y_dist;
				}
				shapeList.remove(currentShape);
				shapeList.add(new Polygon(x, y, 3));
				selected = -1;
			}
		}
		repaint();
		enableButton(button1);
		button1 = null;
	}
	
	/**
	 * Make a copy of the selected shape and show it on the panel
	 */
	public void copy() {
		if (selected > -1) {
			Shape currentShape = shapeList.get(selected);
			if (currentShape instanceof Line2D.Float) {
				Line2D.Float newLine = (Line2D.Float)((Line2D.Float) currentShape).clone();
				Point2D point1 = (newLine).getP1();
				Point2D point2 = (newLine).getP2();
				point1.setLocation(point1.getX()-20, point1.getY()-20);
				point2.setLocation(point2.getX()-20, point2.getY()-20);
				newLine.setLine(point1, point2);
				shapeList.add(newLine);
				selected = -1;
			}
			if (currentShape instanceof Ellipse2D.Float) {
				Ellipse2D.Float ellipse = new Ellipse2D.Float();
				double x = ((Ellipse2D.Float) currentShape).getX();
				double y = ((Ellipse2D.Float) currentShape).getY();
				double w = ((Ellipse2D.Float) currentShape).getWidth();
				double h = ((Ellipse2D.Float) currentShape).getHeight();
				ellipse.setFrame(x-20, y-20, w, h);
				shapeList.add(ellipse);
				selected = -1;
			}
			if (currentShape instanceof Polygon) {
				int x[] = ((Polygon) currentShape).xpoints;
				int y[] = ((Polygon) currentShape).ypoints;
				int newx[] = {0,0,0};
				int newy[] = {0,0,0};
				for (int i = 0; i < 3; i ++) {
					newx[i] = x[i] - 20;
					newy[i] = y[i] - 20;
				}
				shapeList.add(new Polygon(newx, newy, 3));
				selected = -1;
			}
			repaint();
		}
	}
	
	/**
	 * Paint all the shape object onto the panel
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		for (Point2D point : pointList) {
			g.setColor(Color.black);
			int x = (int)point.getX();
			int y = (int)point.getY();		
			g.drawLine(x,y,x,y);
		}
		for (Shape e : shapeList) {
			if (shapeList.indexOf(e) == selected) {
				g.setColor(Color.green);
			}
			else {
				g.setColor(Color.black);
			}
			if (e instanceof Line2D.Float)
				g.drawLine((int)((Line2D.Float) e).getX1(), (int)((Line2D.Float) e).getY1(), (int)((Line2D.Float) e).getX2(), (int)((Line2D.Float) e).getY2());
			if (e instanceof Ellipse2D.Float)
				g.drawOval((int)((Ellipse2D.Float) e).getX(), (int)((Ellipse2D.Float) e).getY(), (int)((Ellipse2D.Float) e).getWidth(), (int)((Ellipse2D.Float) e).getHeight());
			if (e instanceof Polygon) {
				int x [] = ((Polygon) e).xpoints;
				int y [] = ((Polygon) e).ypoints;
				for (int i = 0; i < 3; i ++) {
					g.drawLine(x[i%3], y[i%3], x[(i+1)%3], y[(i+1)%3]);
				}
			}
		}
	}
	
	/**
	 * Save all the shape object to the selected file path
	 * @param filePath path to the file
	 */
	public void save(String filePath) {
		try {
			FileOutputStream f = new FileOutputStream(new File(filePath));
			ObjectOutputStream o = new ObjectOutputStream(f);

			for (Shape ele: shapeList)
				o.writeObject(ele);

			o.close();
			f.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		}
	}
	
	/**
	 * Load all the shape object from the selected file path
	 * @param filePath path to the file
	 */
	public void load(String filePath) {
		FileInputStream fi = null;
		ObjectInputStream oi = null;
		shapeList.clear();
		
		try {
			fi = new FileInputStream(new File(filePath));
			oi = new ObjectInputStream(fi);

			while (true) {
				shapeList.add((Shape) oi.readObject());
			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			// System.out.println("Error initializing stream");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		
		try {
			oi.close();
			fi.close();
		} catch (IOException e) {
			// System.out.println("Error initializing stream");
		}
		repaint();
	}
	
	/**
	 * Mouse Drag listener
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {}

	/**
	 * Mouse Move listener
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {}

	/**
	 * Mouse click listener
	 * Carry out appropriate action base on the current mode
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (mode == 0 || mode == 1) {
			int x = e.getX();
			int y = e.getY();
			Point2D.Float point = new Point2D.Float(x, y);
			pointList.add(point);
			count += 1;
			repaint();
			if (count >= 2) {
				count = 0;
				addShape (mode);
			}
		}
		if (mode == 2) {
			int x = e.getX();
			int y = e.getY();
			Point2D.Float point = new Point2D.Float(x, y);
			pointList.add(point);
			count += 1;
			repaint();
			if (count >= 3) {
				count = 0;
				addShape (mode);
			}
		}
		if (mode == 3) {
			int x = e.getX();
			int y = e.getY();
			Point2D.Float point = new Point2D.Float(x, y);
			for (Shape ele : shapeList) {
				if (ele.getBounds2D().contains(point)) {
					selected = shapeList.indexOf(ele);
					repaint();
					break;
				}
			}
			if (selected > -1) {
				if (button1 != null)
					enableButton(button1);
				if (button2 != null)
					enableButton(button2);
				if (button3 != null)
					enableButton(button3);
			}
			else
				enableButton(button4);
			button1 = null;
			button2 = null;
			button3 = null;
			button4 = null;
		}
	}

	/**
	 * Mouse Enter listener
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {}

	/**
	 * Mouse Exit listener
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {}

	/**
	 * Mouse Press listener
	 * record the mouse position for moving purpose
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (mode == 4) {
			int x = e.getX();
			int y = e.getY();
			Point2D.Float point = new Point2D.Float(x, y);
			start = point;
		}	
	}

	/**
	 * Mouse Release listener
	 * record the mouse position for moving purpose
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (mode == 4) {
			int x = e.getX();
			int y = e.getY();
			Point2D.Float point = new Point2D.Float(x, y);
			end = point;
			mode = -1;
			int x_dist = (int)(start.getX()-end.getX());
			int y_dist = (int)(start.getY()-end.getY());
			moveShape(x_dist, y_dist);
		}
	}
	
}
