import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExpandingDisksFrame extends JFrame implements Constants, ActionListener
{
    private JButton clearDisksButton, clearSpikesButton, clearBothButton;
    private JToggleButton pauseToggle;
    private final ExpandingDiskPanel mainPanel;
    private final ImageIcon playIcon, pauseIcon;

    public ExpandingDisksFrame()
    {
        super("Expanding Disks");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT+28+39); // 28 is for Mac title bar. 39 is for button bar.
        playIcon = new ImageIcon("play.png");
        pauseIcon = new ImageIcon("pause.png");
        getContentPane().setLayout(new BorderLayout());
        mainPanel = new ExpandingDiskPanel();
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(createButtonPane(), BorderLayout.SOUTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public JPanel createButtonPane()
    {
        JPanel thePanel = new JPanel(new FlowLayout());
        clearDisksButton = new JButton("Clear Disks");
        clearDisksButton.addActionListener(this);
        thePanel.add(clearDisksButton);

        clearSpikesButton = new JButton("Clear Spikes");
        clearSpikesButton.addActionListener(this);
        thePanel.add(clearSpikesButton);

        clearBothButton = new JButton("Clear Both");
        clearBothButton.addActionListener(this);
        thePanel.add(clearBothButton);

        pauseToggle = new JToggleButton(pauseIcon, false);
        pauseToggle.setSelectedIcon(playIcon);
        pauseToggle.addActionListener(this);
        pauseToggle.setSelected(false);

        thePanel.add(pauseToggle)
        ;
        return thePanel;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == clearDisksButton)
            mainPanel.clearAllDisks();
        if (e.getSource() == clearSpikesButton)
            mainPanel.clearAllSpikes();
        if (e.getSource() == clearBothButton)
        {
            mainPanel.clearAllDisks();
            mainPanel.clearAllSpikes();
        }
        if (e.getSource() == pauseToggle)
        {
            mainPanel.setPaused(pauseToggle.isSelected());
        }
    }
}
