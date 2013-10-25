package readerAdvisor.gui;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/17/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DisplayWindow {
    public void displayWindow();

    public void hideWindow();

    public void toggle();

    public void addTextToPanel(final String string);

    public void addTextLineToPanel(final String string);
}
