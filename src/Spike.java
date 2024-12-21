import java.awt.*;

/**
 * A spike is a subclass of Disk that doesn't expand, and which draws itself as a flashing 3-pixel box. (In this
 * program, a spike coming into contact with a Disk will kill the disk.)
 */
public class Spike extends Disk
{
    public Spike(int inX, int inY)
    {
        x = inX;
        y = inY;
        radius = 0;
    }

    public void expand()
    {
        ; // do nothing.
    }

    public void drawSelf(Graphics g)
    {
        g.setColor(new Color((int)(Math.random()*256),(int)(Math.random()*256), (int)(Math.random()*256)));
        g.fillRect(x-1,y-1,3,3);
    }
}
