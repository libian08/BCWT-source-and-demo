package y0.gui;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;


public class JDraggableLabel extends JLabel {	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6664062307820070604L;


	private int mousePointOffsetX;	
	private int mousePointOffsetY;	
	private int resizeDetectRange = 10 ; // resize mode detected area width/height
	private int resizeMinimumSize = 10; //minimum size of the label
	
	private String handleMode = "";
	public static String HANDLE_MODE_RESIZE = "resize";
	public static String HANDLE_MODE_DRAG = "drag";
	
	public Container jParent = null;	
	
	private boolean dragEnabled = true; //can set to false if you're busy doing something else.
	private boolean resizeEnabled = true; //can set to false if you're busy doing something else.
	
	public JDraggableLabel(){
		super();
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent evt) {
				jLabelMouseMoved(evt);
			}
			public void mouseDragged(MouseEvent evt) {
				jLabelMouseDragged( evt);
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				jLabelMouseReleased( evt);
			}
			public void mousePressed(MouseEvent evt) {
				jLabelMousePressed(evt);
			}
		});
	}
	
	private void jLabelMouseMoved(MouseEvent evt) {
		int mouseX = evt.getX();
		int mouseY = evt.getY();
		
		if ((mouseX> (getWidth()- getResizeDetectRange())) 
				&& (mouseY > (getHeight()-getResizeDetectRange()))
				&& this.isResizeEnabled())
			setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		else if (this.isDragEnabled())
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}
	
	public void jLabelMouseDragged(MouseEvent evt) {
		if (getHandleMode().equals(JDraggableLabel.HANDLE_MODE_RESIZE) && this.isResizeEnabled()){
			//resize the label
			int newX = evt.getX();
			int newY = evt.getY();
			
			if (newX>(this.getJParent().getWidth()-this.getLocation().x)) newX = this.getJParent().getWidth()-this.getLocation().x;
			if (newY>(this.getJParent().getHeight()-this.getLocation().y)) newY = this.getJParent().getHeight()-this.getLocation().y;
			
			if (newX<resizeMinimumSize) newX = resizeMinimumSize;
			if (newY<resizeMinimumSize) newY = resizeMinimumSize;
			setSize(newX, newY);
			this.getJParent().setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
			
		}else if (getHandleMode().equals(JDraggableLabel.HANDLE_MODE_DRAG) && this.isDragEnabled()){ // drag the whole label and move
			int origX = getLocation().x;
			int origY = getLocation().y;
			
			int mouseOffsetX = getMousePointOffsetX();
			int mouseOffsetY = getMousePointOffsetY();
			
			int newX = origX+(evt.getX()-mouseOffsetX);
			int newY = origY+(evt.getY()-mouseOffsetY);
			
			if (newX<0) newX=0;
			if (newY<0) newY=0;
			
			if (newX>this.getJParent().getWidth()-getWidth()) newX =this.getJParent().getWidth()-getWidth();
			if (newY>this.getJParent().getHeight()-getHeight()) newY =this.getJParent().getHeight()-getHeight();
			
			setLocation(newX, newY);
		}

	}	
	
								

	public void jLabelMouseReleased(MouseEvent evt) {
		this.getJParent().setCursor(Cursor.getDefaultCursor());//new Cursor());
		setHandleMode("");
	}
	public void jLabelMousePressed(MouseEvent evt) {

		setMousePointOffsetX(evt.getX());
		setMousePointOffsetY(evt.getY());
		
		int mouseX = evt.getX();
		int mouseY = evt.getY();
		if ((mouseX> (getWidth()-getResizeDetectRange())) 
				&& (mouseY > (getHeight()-getResizeDetectRange()))
				&& this.isResizeEnabled()){
			//resize the label											
			setHandleMode(JDraggableLabel.HANDLE_MODE_RESIZE);
			
		}else if ( this.isDragEnabled()){ 
			setHandleMode(JDraggableLabel.HANDLE_MODE_DRAG);
		}
	}
	

	public int getMousePointOffsetY() {
		return mousePointOffsetY;
	}

	public void setMousePointOffsetY(int mousePointOffsetY) {
		this.mousePointOffsetY = mousePointOffsetY;
	}

	public int getMousePointOffsetX() {
		return mousePointOffsetX;
	}

	public void setMousePointOffsetX(int mousePointOffsetX) {
		this.mousePointOffsetX = mousePointOffsetX;
	}
	public String getHandleMode() {
		return handleMode;
	}
	public void setHandleMode(String handleMode) {
		this.handleMode = handleMode;
	}

	public int getResizeMinimumSize() {
		return resizeMinimumSize;
	}

	public void setResizeMinimumSize(int resizeMinimumSize) {
		this.resizeMinimumSize = resizeMinimumSize;
	}

	public int getResizeDetectRange() {
		return resizeDetectRange;
	}

	public void setResizeDetectRange(int resizeDetectRange) {
		this.resizeDetectRange = resizeDetectRange;
	}

	public Container getJParent() {
		if (jParent == null){
			jParent = this.getParent();
		}
		return jParent;
	}

	public void setJParent(Container parent) {
		jParent = parent;
	}

	public boolean isDragEnabled() {
		return dragEnabled;
	}

	public void setDragEnabled(boolean dragEnabled) {
		this.dragEnabled = dragEnabled;
	}

	public boolean isResizeEnabled() {
		return resizeEnabled;
	}

	public void setResizeEnabled(boolean resizeEnabled) {
		this.resizeEnabled = resizeEnabled;
	}


}
