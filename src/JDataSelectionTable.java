import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class JDataSelectionTable extends JTable implements ChangeListener {
	private JDataTable jDataTable;
	private boolean selectValue;
	private ImageIcon selectionIcon;

	JDataSelectionTable(JDataTable jDataTable) {
		super(new DataSelectionModel((DataTableModel) jDataTable.getModel()));
		this.jDataTable = jDataTable;

		setFillsViewportHeight(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowGrid(false);

		setPreferredSize(new Dimension(DataTable.SELECTION_STANDART,
				jDataTable.getPreferredSize().height));
		// TODO
		// Dimension d = getPreferredSize();
		// d.width = DataTable.SELECTION_STANDART;
		// setPreferredSize(d);

		getTableHeader().setResizingAllowed(false);
		getTableHeader().setReorderingAllowed(false);

		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());

		TableColumn column = getColumnModel().getColumn(0);

		column.setHeaderValue(null);
		column.setCellRenderer(new CellRenderer());

		selectionIcon = jDataTable.getImage("selection.gif");
	}

	public void rebuild() {
		setRowHeight(jDataTable.getRowHeight());
		setBackground(jDataTable.getBackground());

		((DataSelectionModel) getModel()).fireTableDataChanged();
	}

	public DataRow getRowAtPoint(MouseEvent e) {
		DataTableModel tableModel = (DataTableModel) jDataTable.getModel();
		return tableModel.getRow(rowAtPoint(e.getPoint()));
	}

	public void paintComponent(Graphics g) {
		// TODO repaint after moving the window?
		super.paintComponent(g);

		Rectangle r = g.getClipBounds();
		g.setColor(SystemColor.controlShadow);
		g.drawLine(r.width - 1, r.y, r.width - 1, r.y + r.height);
	}

	// Keep scrolling of the row table in sync with the main table.
	public void addNotify() {
		super.addNotify();

		Component c = getParent();
		if (c instanceof JViewport) {
			((JViewport) c).addChangeListener(this);
		}
	}

	// Keep the scrolling of the row table in sync with main table
	public void stateChanged(ChangeEvent e) {
		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane) viewport.getParent();

		// System.out.println("MAIN TABLE:");
		// System.out.println(scrollPane.getViewport().getViewSize());
		// JDataTable obj1 = (JDataTable) scrollPane.getViewport().getView();
		// obj1.setBorder(new LineBorder(Color.GREEN, 2));
		//
		// System.out.println("SELECTION TABLE:");
		// System.out.println(viewport.getViewSize());
		// JDataSelection obj2 = (JDataSelection) viewport.getView();
		// obj2.setBorder(new LineBorder(Color.RED, 2));

		// viewport.setViewPosition(new Point(viewport.getViewPosition().x,
		// scrollPane.getVerticalScrollBar().getValue()));

		scrollPane.getVerticalScrollBar()
				.setValue(viewport.getViewPosition().y);
	}

	/**
	 * cell renderer
	 */
	private class CellRenderer extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int rowIndex,
				int columnIndex) {

			DataTableModel tableModel = (DataTableModel) jDataTable.getModel();
			DataRow row = tableModel.getRow(rowIndex);

			if (row.isInMultipleSelection()) {
				setIcon(selectionIcon);
				setHorizontalAlignment(JLabel.RIGHT);
			} else {
				setIcon(null);
			}

			return this;
		}

	}

	/**
	 * Mouse Listener
	 */
	private class MouseHandler implements MouseListener, MouseMotionListener {

		public void mouseClicked(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {
			DataRow row = getRowAtPoint(e);
			if (row != null) {
				row.setInMultipleSelection(!row.isInMultipleSelection());
				selectValue = row.isInMultipleSelection();
				repaint();
			}
		}

		public void mouseReleased(MouseEvent e) {

		}

		public void mouseDragged(MouseEvent arg0) {
			DataRow row = getRowAtPoint(arg0);
			if (row != null) {
				row.setInMultipleSelection(selectValue);
				repaint();
			}
		}

		public void mouseMoved(MouseEvent arg0) {

		}

	}

}
