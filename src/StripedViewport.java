
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

/**
 * Creates a JViewport that draws a striped backgroud corresponding to the
 * row positions of the given JTable.
 */
class StripedViewport extends JViewport {

    private final JTable fTable;

    public StripedViewport(JTable table) {
        fTable = table;
        setOpaque(false);
        initListeners();
    }

    private void initListeners() {
        // install a listener to cause the whole table to repaint when
        // a column is resized. we do this because the extended grid
        // lines may need to be repainted. this could be cleaned up,
        // but for now, it works fine.
        PropertyChangeListener listener = createTableColumnWidthListener();
        for (int i=0; i<fTable.getColumnModel().getColumnCount(); i++) {
            fTable.getColumnModel().getColumn(i).addPropertyChangeListener(listener);
        }
        
        //AdjustmentListener adjListener = createAdjustmentListener();
        //((JScrollPane) fTable.getParent().getParent()).getVerticalScrollBar().addAdjustmentListener(adjListener);
        
    }

    private AdjustmentListener createAdjustmentListener() {
        return new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				repaint();
			}
        };
    }
    
    private PropertyChangeListener createTableColumnWidthListener() {
        return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				repaint();
			}
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
        paintStripedBackground(g);
        //paintVerticalGridLines(g);
        
    }

    private void paintStripedBackground(Graphics g) {
        // get the row index at the top of the clip bounds (the first row
        // to paint).
        int rowAtPoint = fTable.rowAtPoint(g.getClipBounds().getLocation());
        // get the y coordinate of the first row to paint. if there are no
        // rows in the table, start painting at the top of the supplied
        // clipping bounds.
        int topY = rowAtPoint < 0
                ? g.getClipBounds().y : fTable.getCellRect(rowAtPoint,0,true).y;

        // create a counter variable to hold the current row. if there are no
        // rows in the table, start the counter at 0.
        int currentRow = rowAtPoint < 0 ? 0 : rowAtPoint;
        while (topY < g.getClipBounds().y + g.getClipBounds().height) {
            int bottomY = topY + fTable.getRowHeight();
            g.setColor(getRowColor(currentRow));
            g.fillRect(g.getClipBounds().x, topY, g.getClipBounds().width, bottomY);
            topY = bottomY;
            currentRow ++;
        }
    }

    private Color getRowColor(int row) {
        return row % 2 == 0 ? Color.decode("#FFFFFF") : Color.decode("#F8F8F8");
    }

    private void paintVerticalGridLines(Graphics g) {
        // paint the column grid dividers for the non-existent rows.
        int x = 0;
        for (int i = 0; i < fTable.getColumnCount(); i++) {
            TableColumn column = fTable.getColumnModel().getColumn(i);
            // increase the x position by the width of the current column.
            x += column.getWidth();
            g.setColor(Color.decode("#F8F8F8"));
            // draw the grid line (not sure what the -1 is for, but BasicTableUI
            // also does it.
            g.drawLine(x - 1, g.getClipBounds().y, x - 1, getHeight());
        }
    }
    
}