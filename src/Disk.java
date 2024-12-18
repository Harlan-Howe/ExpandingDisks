import java.awt.*;

public class Disk implements Constants
{
    protected int x, y;
    private int radius;
    private final Color color; // "final" because once we initialize them, they don't change.

    public Disk()
    {
        x = (int)(Math.random()*SCREEN_WIDTH);
        y = (int)(Math.random()*SCREEN_HEIGHT);
        radius = 0;
        color = new Color((int)(Math.random()*256),(int)(Math.random()*256), (int)(Math.random()*256));
    }

    public double distanceToDisk(Disk otherDisk)
    {
        return Math.sqrt(Math.pow(this.x-otherDisk.x, 2) + Math.pow(this.y-otherDisk.y, 2));
    }

    public void expand()
    {
        radius ++;
    }

    public void drawSelf(Graphics g)
    {
        // TODO: Fix this code.
        // the following is wrong, but it has the general syntax you'll need.
        g.setColor(color);
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
        return "Disk{" +
                "x=" + x +
                ", y=" + y +
                ", radius=" + radius +
                ", color=" + color +
                '}';
    }
}
