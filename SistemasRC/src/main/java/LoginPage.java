import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;

public class LoginPage implements ActionListener {
	
	
	JFrame frame = new JFrame();
	JButton loginButton = new JButton("Login");
	JButton cancelButton = new JButton("Sair");
	JButton cadastroButton = new JButton("Cadastro");
	JTextField userIdField = new JTextField();
	JPasswordField userPasswordField = new JPasswordField();
	HashMap<String,String> loginInfo = new HashMap<String,String>(); 
	JLabel userIdLabel = new JLabel("ID do Usuário: ");
	JLabel userPasswordLabel = new JLabel("Senha: ");
	JLabel messageScreen = new JLabel();
	
	
	LoginPage(HashMap<String, String> loginInfoOriginal){	
		
		loginInfo = loginInfoOriginal;
		
		userIdLabel.setBounds(50,100,75,25);
		userPasswordLabel.setBounds(50,150,75,25);
		
		//frame.add(cadastroButton);
		//frame.add(cancelButton);
		frame.add(userIdLabel);
		//frame.add(userIdField);
		frame.add(userPasswordLabel);
		//frame.add(userPasswordField);
		frame.setTitle("Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300,250);
		frame.setIconImage(null);
		frame.setVisible(true);
		
		
	}

	//private static Object getContentPane() {
		// TODO Auto-generated method stub
	//	return null;
	//}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
