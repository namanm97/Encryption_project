import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class cryption extends JFrame 
{
  
    private static final long serialVersionUID = 1L;
 
      JButton btn1,btn2;
      public  cryption() 
      {
      JFrame frame = new JFrame("Options");
      
      btn1 = new JButton("Encrypt");
      btn2 = new JButton("Decrypt");

      //frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
      frame.setVisible(true);

      frame.setLayout(new BorderLayout());
      frame.setContentPane(new JLabel(new ImageIcon("/Users/namanmehrotra/eclipse-workspace/Encryption/src/b.jpg")));
      frame.setLayout(new FlowLayout());
      frame.setSize(400,300);

      btn1.setBounds(50, 180, 120, 30);
      btn2.setBounds(250, 180, 120, 30);
      JLabel l1=new JLabel("Choose a Button");
      l1.setBounds(75, 17, 250, 100);
      l1.setForeground(Color.red);
      l1.setFont(new Font("Serif", Font.BOLD, 35));
      
      frame.add(l1);
      frame.add(btn2);
      frame.add(btn1);

      frame.setLayout(null);
      btn1.addActionListener(new ActionListener() 
      {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	EncryptionDone obj1=new EncryptionDone();
            	obj1.main1();
            }
       });
       btn2.addActionListener(new ActionListener() 
       {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
            	DecryptionDone obj2=new DecryptionDone();
            	obj2.main2();
            }
       });
}
     
    
     public static void main(String[ ] args) 
     {
      new cryption();
     }
     
}