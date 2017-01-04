// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.JPanel;

public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener
{
	private Job job;
	private BufferedImage image;
	private Point dragFrom;
	private Point dragTo;
	private boolean enabled;
	
	public ImagePanel(Job j) {
		this.job = j;
		this.enabled = true;
		this.setBackground(Color.RED);
		this.setPreferredSize(new Dimension(200, 200));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void setImage(BufferedImage bi) {
		this.image = bi;
		this.setMinimumSize(new Dimension(image.getWidth() + 20, image.getHeight() + 20));
		this.setPreferredSize(new Dimension(image.getWidth() + 20, image.getHeight() + 20));
		repaint();
	}
	
	public void setBoundingRect(Rectangle rect) {
		if(rect == null) {
			this.dragFrom = null;
			this.dragTo = null;
		}
		else {
			this.dragFrom = rect.getLocation();
			this.dragTo = new Point(dragFrom.x + rect.width, dragFrom.y + rect.height);
		}
		repaint();
	}
	
	public Rectangle getBoundingRect() {
		if(dragTo == null || dragFrom == null)
			return null;
		else
			return new Rectangle(dragFrom.x, dragFrom.y, dragTo.x-dragFrom.x, dragTo.y-dragFrom.y);
	}
	
	public void setEnabled(boolean e) {
		this.enabled = e;
	}
	
	public void paint(Graphics gg) {
		Graphics2D g = (Graphics2D)gg;
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		
		
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width-1, height-1);
		if(image == null) {
			g.drawLine(0, 0, width-1, height-1);
			g.drawLine(width-1, 0, 0, height-1);
		}
		
		g.drawImage(image, 10, 10, null);
		
		if(dragFrom != null && dragTo != null) {
			g.setColor(Color.GREEN);
			g.drawRect(dragFrom.x, dragFrom.y, dragTo.x-dragFrom.x, dragTo.y-dragFrom.y);
		}
	}

	public void mouseMoved(MouseEvent ev) {
		if(image == null || !enabled)
			return;
		
		Rectangle bounds = new Rectangle(10, 10, image.getWidth(), image.getHeight());
		if(bounds.contains(ev.getPoint())) {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); 
		}
		else {
			setCursor(Cursor.getDefaultCursor());
		}
	}
	public void mouseDragged(MouseEvent ev) {
		if(image == null || !enabled)
			return;
		
		this.dragTo = limitPoint(ev.getPoint());
		repaint();
	}
	
	public void mousePressed(MouseEvent ev) {
		if(image == null || !enabled)
			return;
		
		this.dragFrom = limitPoint(ev.getPoint());
		this.dragTo = null;
		repaint();
	}
	
	public void mouseReleased(MouseEvent ev) {
		if(image == null || !enabled)
			return;
		
		job.boundsUpdated(getBoundingRect());
		repaint();
	}
	
	public Point limitPoint(Point pt) {
		int x = pt.x;
		int y = pt.y;
		
		if(x < 10) x = 10;
		else if(x > image.getWidth() + 9) x = image.getWidth() + 9;
		
		if(y < 10) y = 10;
		else if(y > image.getHeight() + 9) y = image.getHeight() + 9;
		
		return new Point(x, y);
	}
	
	public void mouseClicked(MouseEvent ev) {}
	public void mouseEntered(MouseEvent ev) {}
	public void mouseExited(MouseEvent ev) {}
}
