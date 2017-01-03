
public class OCRLogger
{
	private ViewFrame view;
	
	public OCRLogger() {
		this.view = new ViewFrame();
		setActionListeners();
		view.present();
	}
	
	private void setActionListeners() {
		
	}
	
	public static void main(String[] args) {
		new OCRLogger();
	}
}
