import javax.swing.*;
import java.awt.*;

public class ExpandingDisksFrame extends JFrame implements Constants
{
    public ExpandingDisksFrame()
    {
        super("Expanding Disks");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT+28); // 28 is for Mac title bar.
        getContentPane().setLayout(new GridLayout(1,1));
        getContentPane().add(new ExpandingDiskPanel());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
