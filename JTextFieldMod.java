import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JTextFieldMod extends JTextField
{
	public interface Listener {
		public void modified();
	}
	
	private Listener listener;
	private boolean dirty;
	
	public JTextFieldMod(String initial) {
		super(initial);
		
		listener = null;
		dirty = false;
	}
	
	public void setModListener(Listener l) {
		this.listener = l;
		
		addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ev) { listener.modified(); }});
		
		addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent ev) {  }
		public void focusLost(FocusEvent ev) { if(dirty) { listener.modified(); dirty = false; } }});
		
		getDocument().addDocumentListener(new DocumentListener() {
		public void changedUpdate(DocumentEvent ev) { dirty = true; }
		public void insertUpdate(DocumentEvent ev) { dirty = true; }
		public void removeUpdate(DocumentEvent ev) { dirty = true; }});
	}
}
