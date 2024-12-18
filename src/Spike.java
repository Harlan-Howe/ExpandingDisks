import java.awt.*;

public class Spike extends Disk
{
    public Spike(int inX, int inY)
    {
        x = inX;
        y = inY;
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
