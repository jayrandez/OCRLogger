// MIT License 2017
// Jay Randez, https://github.com/jayrandez

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JobView extends JPanel
{
	protected JButton setImageButton;
	protected JTextField imageDescription;
	protected ImagePanel imagePanel;
	
	protected JButton setFileButton;
	protected JTextField fileDescription;
	
	protected JLabel jobNotc;
	protected JButton startButton;
	
	public JobView() {
		buildView();
	}
	
	private void buildView() {
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());

		JPanel imageSurround = new JPanel(new BorderLayout());
		imageSurround.setBorder(new EmptyBorder(10, 10, 0, 10));
		JPanel imageView = buildImageView();
		imageView.setBorder(BorderFactory.createTitledBorder("Source Image"));
		imageSurround.add(imageView, BorderLayout.CENTER);
		this.add(imageSurround, BorderLayout.WEST);
		
		JPanel rightSide = new JPanel();
		rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.PAGE_AXIS));
		
		JPanel zoneSurround = new JPanel(new BorderLayout());
		zoneSurround.setBorder(new EmptyBorder(10, 0, 0, 10));
		JPanel zoneView = buildZoneView();
		zoneView.setBorder(BorderFactory.createTitledBorder("Log Output"));
		zoneSurround.add(zoneView, BorderLayout.CENTER);
		rightSide.add(zoneSurround);
		
		JPanel scheduleSurround = new JPanel(new BorderLayout());
		scheduleSurround.setBorder(new EmptyBorder(10, 0, 0, 10));
		JPanel scheduleView = buildScheduleView();
		scheduleView.setBorder(BorderFactory.createTitledBorder("Scheduling"));
		scheduleSurround.add(scheduleView, BorderLayout.CENTER);
		rightSide.add(scheduleSurround);
		
		this.add(rightSide, BorderLayout.CENTER);
		
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		this.startButton = new JButton("Start Job");
		this.jobNotc = new JLabel("Log file last written X at X.");
		jobNotc.setVisible(false);
		actionPanel.add(startButton);
		actionPanel.add(jobNotc);
		this.add(actionPanel, BorderLayout.SOUTH);
	}
	
	private JPanel buildImageView() {
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.imageDescription = new JTextField("Image URL");
		//imageDescription.setEnabled(false);
		imageDescription.setPreferredSize(new Dimension(200, 27));
		this.setImageButton = new JButton("Choose");
		topLine.add(imageDescription);
		topLine.add(setImageButton);
		panel.add(topLine, BorderLayout.NORTH);
		
		this.imagePanel = new ImagePanel();
		panel.add(imagePanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel buildZoneView() {
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.fileDescription = new JTextField("Output File");
		fileDescription.setEnabled(false);
		fileDescription.setPreferredSize(new Dimension(200, 27));
		this.setFileButton = new JButton("Choose");
		topLine.add(fileDescription);
		topLine.add(setFileButton);
		panel.add(topLine, BorderLayout.NORTH);
		
		return panel;
	}
	
	private JPanel buildScheduleView() {
		return new JPanel();
	}
}
