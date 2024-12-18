import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ExpandingDiskPanel extends JPanel implements MouseListener, Constants
{
    private final BufferedImage buffer; // the area we will be drawing into.
    private Disk currentDisk;           // the disk that is currently expanding.
    private ArrayList<Disk> diskList;
    private ArrayList<Spike> spikeList;
    private final Object bufferMutex;   // this is an object that allows us to "lock" the associated variable so only one
    //                                     thread can access it at a time.
    private final Object diskListMutex, spikeListMutex;


    public ExpandingDiskPanel()
    {
        super();
        buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferMutex = new Object();
        diskList = new ArrayList<Disk>();
        spikeList = new ArrayList<Spike>();
        diskListMutex = new Object();
        spikeListMutex = new Object();
        addMouseListener(this);
        currentDisk = new Disk();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);  // wipe panel
        synchronized (bufferMutex) // lock the buffer while we draw it to the screen.
        {
            g.drawImage(buffer, 0, 0 , this);
        }
    }

    public void expandCurrentDisk()
    {

    }

    public void redrawAllObjects()
    {
        synchronized(bufferMutex)
        {
            Graphics g = buffer.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            synchronized (diskListMutex)
            {
                // tell each disk to draw self.
            }
        }
        repaintAllSpikes();
    }

    public void repaintAllSpikes()
    {
        if (spikeList.isEmpty())
            return;
        synchronized(bufferMutex)
        {
            Graphics g = buffer.getGraphics();
            synchronized (spikeListMutex)
            {
                // tell each disk to draw self.
            }
        }
    }

    public void addSpike(int x, int y)
    {
        Spike spikeToAdd = new Spike(x,y);
        killAllDisksTouchingSpike(spikeToAdd);
        synchronized (spikeListMutex)
        {
            spikeList.add(spikeToAdd);
        }
        redrawAllObjects();
    }

    public void killAllDisksTouchingSpike(Spike spike)
    {
        synchronized (diskListMutex)
        {

        }
    }

    public void removeNearestSpike(int x, int y)
    {
        if (spikeList.isEmpty())
            return;
        synchronized (spikeListMutex)
        {
            // find the spike that is closest to given (x,y)
        }
    }

//    public void removeAllDisksTouchingSpike(Spike spike)
//    {
//
//    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        System.out.print(STR."Mouse released at (\{e.getX()}, \{e.getY()}).");
        if (e.isShiftDown())
        {
            System.out.println(" Shift is pressed.");
            removeNearestSpike(e.getX(), e.getY());
        }
        else
        {
            System.out.println();
            addSpike(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }

    class AnimatedBufferThread extends Thread
    {
        private boolean shouldInterrupt;
        private boolean doingExpansionProcess;

        public AnimatedBufferThread()
        {
            shouldInterrupt = false;
            doingExpansionProcess = false;
        }

        public void interrupt() { shouldInterrupt = true; doingExpansionProcess = false;}
        public void startExpansionProcess() {doingExpansionProcess = true;}
        public boolean isDoingExpansionProcess() {return doingExpansionProcess;}

        public void run() // this is what gets called when we tell this thread to start(). You should _NEVER_ call this
        // method directly.
        {
            long start = System.currentTimeMillis();
            long difference;
            System.out.println("Starting Expansion Thread.");
            while (true)
            {
                difference = System.currentTimeMillis() - start;
                if (difference >= MILLISECONDS_PER_STEP)
                {
                    expandCurrentDisk();
                    start = System.currentTimeMillis();
                }
                try
                {
                    //noinspection BusyWait
                    Thread.sleep(1); // wait a quarter second before you consider running again.
                }catch (InterruptedException iExp)
                {
                    iExp.printStackTrace();
                }
                shouldInterrupt = false; // reset this to try again, if needed.
            }
        }
    }
}
