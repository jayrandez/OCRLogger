import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class ButtonGroupMod extends ButtonGroup implements ItemListener
{
	public interface Listener {
		public void action();
	}
	
	private Listener listener;
	private ArrayList<JRadioButton> buttons;
	private int selectedIndex;
	
	public ButtonGroupMod() {
		super();
		
		this.selectedIndex = -1;
		this.buttons = new ArrayList<JRadioButton>();
	}
	
	public void setModListener(Listener l) {
		this.listener = l;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public void setSelectedIndex(int index) {
		buttons.get(index).setSelected(true);
	}
	
	public void add(JRadioButton button) {
		super.add(button);
		
		this.buttons.add(button);
		if(button.isSelected())
			this.selectedIndex = this.buttons.size() - 1;
		button.addItemListener(this);
	}
	
	public JRadioButton get(int index) {
		return buttons.get(index);
	}

	public void itemStateChanged(ItemEvent ev) {
	    if(ev.getStateChange() == ItemEvent.SELECTED) {
	    	int index = buttons.indexOf(ev.getItem());
	    	this.selectedIndex = index;
	    	if(listener != null)
	    		listener.action();
	    }
	}
}
