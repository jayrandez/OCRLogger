// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ViewFrame extends JFrame
{
	protected JLabel messageLabel;
	protected JButton addButton;
	
	private JTabbedPane tabPane;
	
	public ViewFrame() {
		super("OCR Logger");
		
		buildView();
	}
	
	public void present() {
		this.pack();
		this.setVisible(true);
	}
	
	public void addJobView(JobView view, String title) {
		this.tabPane.add(view, title);
		this.tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
		this.pack();
	}
	
	public void setJobViewTitle(JobView view, String title) {
		int index = tabPane.indexOfComponent(view);
		tabPane.setTitleAt(index, title);
	}
	
	private void buildView() {
		this.setLayout(new BorderLayout());
		
		// Create Toolbar
		
		JPanel toolbar = new JPanel(new BorderLayout());
		
		JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.messageLabel = new JLabel("Message text.");
		messagePanel.add(messageLabel);
		
		this.addButton = new JButton("Add Job");
		toolbar.add(messagePanel, BorderLayout.CENTER);
		toolbar.add(addButton, BorderLayout.EAST);
		
		this.add(toolbar, BorderLayout.NORTH);
		
		// Create Tab Pane
		
		this.tabPane = new JTabbedPane();
		this.add(tabPane, BorderLayout.CENTER);
	}
}
