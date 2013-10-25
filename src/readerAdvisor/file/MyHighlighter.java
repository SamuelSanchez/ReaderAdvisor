package readerAdvisor.file;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 7/16/13
 * Time: 2:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyHighlighter extends DefaultHighlighter.DefaultHighlightPainter {

    public MyHighlighter(Color color) {
        super(color);
    }
}
