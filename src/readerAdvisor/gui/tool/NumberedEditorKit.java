package readerAdvisor.gui.tool;

import java.awt.*;
import javax.swing.text.*;

public class NumberedEditorKit extends StyledEditorKit {
    public ViewFactory getViewFactory() {
        return new NumberedViewFactory();
    }

    private class NumberedViewFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null)
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                }
                else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new NumberedParagraphView(elem);
                }
                else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                }
                else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                }
                else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            return new LabelView(elem);
        }
    }

    private class NumberedParagraphView extends ParagraphView {
        private static final short NUMBERS_WIDTH = 25;

        public NumberedParagraphView(Element e) {
            super(e);
            short top = 0;
            short left = 0;
            short bottom = 0;
            short right = 0;
            this.setInsets(top, left, bottom, right);
        }

        protected void setInsets(short top, short left, short bottom, short right) {
            super.setInsets(top,(short)(left+NUMBERS_WIDTH), bottom,right);
        }

        public void paintChild(Graphics g, Rectangle r, int n) {
            super.paintChild(g, r, n);
            int previousLineCount = getPreviousLineCount();
            int numberX = r.x - getLeftInset();
            int numberY = r.y + r.height - 5;
            g.drawString(Integer.toString(previousLineCount + n + 1), numberX, numberY);
        }

        public int getPreviousLineCount() {
            int lineCount = 0;
            View parent = this.getParent();
            int count = parent.getViewCount();
            for (int i = 0; i < count; i++) {
                if (parent.getView(i) == this) {
                    break;
                }
                else {
                    lineCount += parent.getView(i).getViewCount();
                }
            }
            return lineCount;
        }
    }
}


