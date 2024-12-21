import java.awt.*;

/**
 * The Disk class describes a circular area of the screen that is filled in with a given color. The circle has the
 * option to enlarge.
 */
public class Disk implements Constants
{
    protected int x, y;
    protected int radius;
    private final Color color; // "final" because once we initialize them, they don't change.

    public Disk()
    {
        x = (int)(Math.random()*SCREEN_WIDTH);
        y = (int)(Math.random()*SCREEN_HEIGHT);
        radius = 0;
        color = new Color((int)(Math.random()*256),(int)(Math.random()*256), (int)(Math.random()*256));
    }

    /**
     * finds the distance from the center of this disk to the center of the other disk.
     * @param otherDisk - another Disk object to which we wish to find the distance.
     * @return - the distance, in pixels.
     */
    public double distanceToDisk(Disk otherDisk)
    {
        return Math.sqrt(Math.pow(this.x-otherDisk.x, 2) + Math.pow(this.y-otherDisk.y, 2));
    }

    /**
     * increment the radius of this disk.
     */
    public void expand()
    {
        radius ++;
    }

    /**
     * draws in a filled circle of the "color" for this Disk, centered at (x,y) with radius "radius."
     * @param g - the graphics context in which to do the drawing.
     */
    public void drawSelf(Graphics g)
    {
        g.setColor(color);
        // TODO: Fix this code.
        // the following is wrong, but it has the general syntax you'll need.
        g.fillOval(x,y,radius,radius); // left, top, width, height
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getRadius()
    {
        return radius;
    }

    @Override
    public String toString()
    {
        return STR."Disk{x=\{x}, y=\{y}, radius=\{radius}, color=\{color}}";
    }
}
