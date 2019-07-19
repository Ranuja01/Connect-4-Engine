import javax.swing.*;
import java.awt.event.*;
import java.util.*;
public class ConnectFourListener implements MouseListener
{
    private ConnectFourGUI gui;
    private ConnectFour game;
    public ConnectFourListener (ConnectFour game, ConnectFourGUI gui) {
        this.game = game;
        this.gui = gui;
        Scanner sc = new Scanner (System.in);
        System.out.println("Press 1 to play first or any key for the computer to go first:");
        String input = sc.nextLine();
        if (!input.equals("1")) {
            game.ai();
        } 
        gui.addListener (this);
        
    }


    public void mouseClicked (MouseEvent event) {
        JLabel label = (JLabel) event.getComponent ();
        int column = gui.getColumn (label);
        game.play(column);   
    }

    public void mousePressed (MouseEvent event) {
    }

    public void mouseReleased (MouseEvent event) {
    }


    public void mouseEntered (MouseEvent event) {
    }

    public void mouseExited (MouseEvent event) {
    }
}
