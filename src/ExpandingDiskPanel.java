import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ExpandingDiskPanel extends JPanel implements MouseListener
{
    private final BufferedImage buffer; // the area we will be drawing into.
    private Disk currentDisk;           // the disk that is currently expanding.
    private ArrayList<Disk> diskList;   // the list of all the no-longer-expanding disks
    private ArrayList<Spike> spikeList; // the list of spikes, if any.

    // mutex objects - used for multithreading.
    private final Object bufferMutex;   // this is an object that allows us to "lock" the associated variable so only one
    //                                     thread can access it at a time.
    private final Object diskListMutex, spikeListMutex;

    // the separate process that does the work of the animation so we can see it in action.
    private final AnimatedBufferThread abThread;

    private boolean isPaused;


    public ExpandingDiskPanel()
    {
        super();
        isPaused = false;
        buffer = new BufferedImage(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferMutex = new Object();
        diskList = new ArrayList<Disk>();
        spikeList = new ArrayList<Spike>();
        diskListMutex = new Object();
        spikeListMutex = new Object();
        addMouseListener(this);
        currentDisk = new Disk();
        redrawAllObjects();
        abThread = new AnimatedBufferThread();
        abThread.start();
    }

    public void setPaused(boolean paused)
    {
        isPaused = paused;
        repaint();
    }

    /**
     * draws the "buffer" - a rectangular area the same size as this panel that you've been drawing into with other
     * commands - onto the screen. If we are not actively animating, it will draw a border around the perimeter, as
     * well to indicate that.
     * @param g the <code>Graphics</code> object used to draw to the panel.
     */
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

    /**
     * picks a new disk at random, and checks whether it is already inside one of the other disks. If so, it will pick
     * another one. Keeps trying until it finds one, or until it has tried Constants.NUM_TRIES_TO_MAKE_NEW_DISK times.
     * If it can't find one, sets currentDisk to null.
     */
    public void selectNewCurrentDisk()
    {
        boolean foundGoodDisk;
        for (int i = 0; i < Constants.NUM_TRIES_TO_MAKE_NEW_DISK; i++)
        {
            currentDisk = new Disk();
            foundGoodDisk = true;
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


    /**
     * determines whether the two disks are touching, overlapping or one contains another. Returns true UNLESS the
     * disks are completely independent.
     * @param d1 - the first disk (or spike) to check
     * @param d2 - the second disk (or spike) to check
     * @return - whether these two disks are in contact.
     */
    public boolean areDisksTouching(Disk d1, Disk d2)
    {
        // TODO #2: you write this.
        // your answer should depend on the radii of the two disks and their separation. Be sure to use method in Disk
        //  to find separation.
        return false;
    }

    /**
     * determines whether the given disk is located such that it's outer circle touches or intersects the bounds of this
     * panel.
     * @param d - the disk in question
     * @return - whether the disk is touching or overlapping the edges of this panel.
     */
    public boolean isDiskTouchingWall(Disk d)
    {
        // TODO #3: you write this.
        // your answer should depend on the distance between the currentDisk's (x,y) center and the edges of the walls,
        //   as well as the radius of the currentDisk.
        // hint: you may find getWidth() and getHeight() handy to find the size of the window.
        return false;
    }

    /**
     * the main "loop" of the program. Does the following:
     * 1) flashes any spikes on screen
     * 2) exits if the animation is paused or there is no currentDisk
     * 3) makes the currentDisk grow a little.
     * 4) Checks for collisions between the current disk and any spikes
     * 5) Draws the current disk
     * 6) checks for collisions between the current disk and other disks or the walls of the window.
     */
    public void doAnimationStep()
    {
        repaintAllSpikes();
        if (currentDisk == null || isPaused)
            return;

        currentDisk.expand();
        synchronized(spikeListMutex)
        {
            // if current disk is touching a spike: a) select a new current disk, b) redrawAllObjects,  and c) return.
            // TODO #6: you write this. (Be sure to use the methods you just wrote.)
        }
        // tell the currentDisk to draw itself.
        drawCurrentDisk();

        synchronized (diskListMutex)
        {
            // If current disk is touching any other disk or the edge of the window: a) copy this disk into the list
            //    of disks and then b) select a new current disk.
            // TODO #4: you write this. (Be sure to use the methods you just wrote.)
        }

        repaint();
    }

    /**
     * draws the current disk (if any) to the buffer, where it will show on screen very soon.
     */
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

    /**
     * draws all the disks in diskList and all the spikes in spikeList to the buffer.
     */
    public void redrawAllObjects()
    {
        synchronized (bufferMutex)
        {
            Graphics g = buffer.getGraphics();
            g.setColor(Constants.BACKGROUND_COLOR);
            g.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            synchronized (diskListMutex)
            {
                // TODO #1: tell each disk to draw self.
            }
        }
        repaintAllSpikes();
    }

    /**
     * draws all the spikes in the spikeList to the buffer.
     */
    public void repaintAllSpikes()
    {
        if (spikeList.isEmpty())
            return;
        synchronized (bufferMutex)
        {
            Graphics g = buffer.getGraphics();
            synchronized (spikeListMutex)
            {
                // TODO #5: You write this. Tell each spike to draw self.
            }
        }
        repaint();
    }

    /**
     * empties the diskList of all disks and refreshes the screen. Selects a new current disk.
     */
    public void clearAllDisks()
    {
        synchronized (diskListMutex)
        {
            diskList.clear();
        }
        selectNewCurrentDisk();
        redrawAllObjects();
    }

    /**
     * empties the spikeList of all disks and refreshes the screen. If there was no current disk before, selects one now.
     */
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

    /**
     * adds a new spike to the spike list, located at (x, y); updates the screen. If there was no current disk, selects
     * one.
     * @param x - the x-location of this spike (probably the mouse x-location)
     * @param y - the y-location of this spike (probably the mouse y-location)
     */
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

    /**
     * removes any disks that are overlapping with the given spike from the diskList.
     * @param spike - the spike under consideration
     */
    public void killAllDisksTouchingSpike(Spike spike)
    {
        synchronized (diskListMutex)
        {
            // TODO #7: you write this. Check each of the disks; if any are touching the given spike, remove them from the
            //        disk list.
            // Note: remember that Spikes are also disks; there is a method you wrote earlier that will be handy.
        }
    }

    /**
     * searches for the closest spike to (x, y) that is within Constants.MAX_DISTANCE_TO_REMOVE_SPIKE, and removes that
     * spike (if any) from the spike list.
     * @param x
     * @param y
     */
    public void removeNearestSpike(int x, int y)
    {
        if (spikeList.isEmpty())
            return;
        double closestDistance = Constants.MAX_DISTANCE_TO_REMOVE_SPIKE;
        Spike nearestSpike = null;

        synchronized (spikeListMutex)
        {
            // TODO #8: find the spike that is closest to given (x,y), if any are found closer than
            //  MAX_DISTANCE_TO_REMOVE_SPIKE.

            // TODO #9: After that search... remove that spike from the spikeList, if there is one.
        }
        redrawAllObjects();
    }

    @Override
    /**
     * because we are implementing MouseListener, we must have this method... but it does nothing.
     * (It is called when the user presses and releases a mouse at the same location.)
     */
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    /**
     * because we are implementing MouseListener, we must have this method... but it does nothing.
     * (It is called when the user first presses a mouse button.)
     */
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    /**
     * responds to the user letting go of a mouse button. If the shift key is also held down, attempts to remove a spike,
     * otherwise adds a spike at the current mouse location.
     * (Part of the MouseListener interface methods.)
     */
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
    /**
     * because we are implementing MouseListener, we must have this method... but it does nothing.
     * (It is called when the user moves the mouse into this panel.)
     */
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    /**
     * because we are implementing MouseListener, we must have this method... but it does nothing.
     * (It is called when the user moves the mouse out of this panel.)
     */
    public void mouseExited(MouseEvent e)
    {

    }

    /**
     * An internal thread that allows us to edit an offscreen image buffer while periodically drawing it to the screen,
     * which lets the user see it while it is being drawn.
     * (Perhaps overkill for this program, but a handy general pattern for when we wish to animate things.)
     * This class is an "internal" class - it is defined _inside_ the ExpandingDiskPanel class, which means that ONLY
     * ExpandingDiskPanel has access to it, AND it has access to all of this ExpandingDiskPanel instance's methods and
     * variables.
     */
    class AnimatedBufferThread extends Thread
    {

        /**
         * this is what gets called when we tell this thread to start(). You should _NEVER_ call this
         *         // method directly.
         */
        public void run()
        {
            long start = System.currentTimeMillis();
            long difference;
            System.out.println("Starting Expansion Thread.");
            while (true)
            {
                difference = System.currentTimeMillis() - start;
                if (difference >= Constants.MILLISECONDS_PER_STEP)
                {
                    doAnimationStep();
                    start = System.currentTimeMillis();
                }
                try
                {
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