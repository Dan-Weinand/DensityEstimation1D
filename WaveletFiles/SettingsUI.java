import java.awt.FlowLayout;
import javax.swing.JFrame;

/**
 * Allows the user to set/select settings.
 * @author Gedeon Nyengele & Daniel Weinand
 *
 */
public class SettingsUI extends JFrame{

	public SettingsUI(int width, int height){
		
		this.setSize(width, height);
		init();
		
		
	}
	public SettingsUI()
	{
		init();
	}
	
	public void init()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout( new FlowLayout() );
	}
}
