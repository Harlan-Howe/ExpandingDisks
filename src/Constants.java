import java.awt.*;

public interface Constants
{
    // the size of the ExpandingDisksPanel. The window will be taller to accommodate the title and the buttons row.
    public final int SCREEN_WIDTH = 800;
    public final int SCREEN_HEIGHT = 800;

    public final Color BACKGROUND_COLOR = Color.GRAY;

    public final int MILLISECONDS_PER_STEP = 10;  // controls the speed at which the disks appear to expand.

    public final int MAX_DISTANCE_TO_REMOVE_SPIKE = 75; // how close does the user need to shift-click to a spike to
    //                                                      "count" as selecting it?

    public final int NUM_TRIES_TO_MAKE_NEW_DISK = 20; // how many times in a row should the computer try to make a new
    //                                                      disk before giving up and deciding the screen is too full?

}
