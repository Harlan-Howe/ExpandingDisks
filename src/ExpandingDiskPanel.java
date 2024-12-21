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
    private final AnimatedBufferThread abThread;
    private boolean isPaused;


    public ExpandingDiskPanel()
    {
        super();
        isPaused = false;
        buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferMutex = new Object();
        diskList = new ArrayList<Disk>();
        spikeList = new ArrayList<Spike>();
        diskListMutex = new Object();
        spikeListMutex = new Object();
        addMouseListener(this);
        currentDisk = new Disk();
        abThread = new AnimatedBufferThread();
        abThread.start();
    }

    public void setPaused(boolean paused)
    {
        isPaused = paused;
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);  // wipe panel
        synchronized (bufferMutex) // lock the buffer while we draw it to the screen.
        {
            g.drawImage(buffer, 0, 0, this);
        }
        // potentially draw outline around edge of panel if no disks are growing.
        if (isPaused || currentDisk == null)
        {
            if (isPaused)
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.YELLOW);
            ((Graphics2D)g).setStroke(new BasicStroke(6));
            g.drawRect(0,0,getWidth(),getHeight());
        }
    }

    public void selectNewCurrentDisk()
    {
        for (int i = 0; i < NUM_TRIES_TO_MAKE_NEW_DISK; i++)
        {
            currentDisk = new Disk();
            boolean foundGoodDisk = true;
            synchronized (diskListMutex)
            {
                for (Disk d : diskList)
                {
                    if (areDisksTouching(currentDisk, d))
                    {
                        foundGoodDisk = false;
                        break;
                    }
                }
                if (foundGoodDisk)
                    return;
            }
        }
        // never found a good disk.
        currentDisk = null;
        repaint();
    }


    public boolean areDisksTouching(Disk d1, Disk d2)
    {
        // TODO: you write this.
        // your answer should depend on the radii of the two disks and their separation. Be sure to use method in Disk
        //  to find separation.
        return false;
    }

    public boolean isDiskTouchingWall(Disk d)
    {
        // TODO: you write this.
        // your answer should depend on the distance between the currentDisk's (x,y) center and the edges of the walls,
        //   as well as the radius of the currentDisk.
        return false;
    }

    public void doAnimationStep()
    {
        repaintAllSpikes();
        if (currentDisk == null || isPaused)
            return;

        currentDisk.expand();
        synchronized(spikeListMutex)
        {
            // if current disk is touching a spike, select a new current disk, redrawAllObjects,  and return.
            // TODO: you write this. (Be sure to use the methods you just wrote.)
        }
        // tell the currentDisk to draw itself.
        drawCurrentDisk();

        synchronized (diskListMutex)
        {
            // If current disk is touching any other disk or the edge of the window, copy this disk into the list
            //    of disks and then reset current disk to a new disk.
            // TODO: you write this. (Be sure to use the methods you just wrote.)
        }
    }

    public void drawCurrentDisk()
    {
        if (currentDisk == null)
            return;
        synchronized (bufferMutex)
        {
            Graphics g = buffer.getGraphics();
            currentDisk.drawSelf(g);
        }
    }

    public void redrawAllObjects()
    {
        synchronized (bufferMutex)
        {
            Graphics g = buffer.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            synchronized (diskListMutex)
            {
                // TODO: tell each disk to draw self.
            }
        }
        repaintAllSpikes();
    }

    public void clearAllDisks()
    {
        synchronized (diskListMutex)
        {
            diskList.clear();
        }
        selectNewCurrentDisk();
        redrawAllObjects();
    }

    public void clearAllSpikes()
    {
        synchronized (spikeListMutex)
        {
            spikeList.clear();
        }
        if (currentDisk == null)
            selectNewCurrentDisk();
        redrawAllObjects();
    }

    public void repaintAllSpikes()
    {
        if (spikeList.isEmpty())
            return;
        synchronized (bufferMutex)
        {
            Graphics g = buffer.getGraphics();
            synchronized (spikeListMutex)
            {
                // TODO: You write this. Tell each spike to draw self.
            }
        }
        repaint();
    }

    public void addSpike(int x, int y)
    {
        Spike spikeToAdd = new Spike(x, y);
        killAllDisksTouchingSpike(spikeToAdd);
        synchronized (spikeListMutex)
        {
            spikeList.add(spikeToAdd);
        }
        if (currentDisk == null)
            selectNewCurrentDisk();
        redrawAllObjects();
    }

    public void killAllDisksTouchingSpike(Spike spike)
    {
        synchronized (diskListMutex)
        {
            // TODO: you write this. Check each of the disks; if any are touching the given spike, remove them from the
            //        disk list.
        }
    }

    public void removeNearestSpike(int x, int y)
    {
        if (spikeList.isEmpty())
            return;
        double closestDistance = MAX_DISTANCE_TO_REMOVE_SPIKE;
        Spike nearestSpike = null;

        synchronized (spikeListMutex)
        {
            // TODO: find the spike that is closest to given (x,y), if any are found closer than
            //  MAX_DISTANCE_TO_REMOVE_SPIKE.

            // TODO: After that search... remove that spike from the spikeList, if there is one.
        }
        redrawAllObjects();
    }

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
        } else
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
                    doAnimationStep();
                    start = System.currentTimeMillis();
                }
                try
                {
                    //noinspection BusyWait
                    Thread.sleep(1); // wait a quarter second before you consider running again.
                } catch (InterruptedException iExp)
                {
                    System.out.println("AnimatedBufferThread was interrupted.");
                    break;
                }
            }
        }
    }

}