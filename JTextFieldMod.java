// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

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
