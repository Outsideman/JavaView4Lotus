import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
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

public class TableSelectionUI extends JTable implements ChangeListener {
	private TableUI tableUI;
	private boolean selectValue;
	private ImageIcon selectionIcon;

	TableSelectionUI(TableUI tableUI_) {
		super(new DataSelectionModel((DataTableModel) tableUI_.getModel()));
		this.tableUI = tableUI_;
		
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, SystemColor.controlShadow));
		setFillsViewportHeight(true);
		setFocusable(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowGrid(false);
		setRowHeight(tableUI.getRowHeight());
		setBackground(tableUI.getBackground());
		
		setPreferredSize(new Dimension(DataTable.SELECTION_STANDART,
				tableUI.getPreferredSize().height));
		
		setPreferredScrollableViewportSize(getPreferredSize());
		
		getTableHeader().setResizingAllowed(false);
		getTableHeader().setReorderingAllowed(false);

		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());

		TableColumn column = getColumnModel().getColumn(0);

		column.setHeaderValue(null);
		column.setCellRenderer(new CellRenderer());

		selectionIcon = tableUI.getImage("selection.gif");
	}
	
	public void updateViewport() {
		JViewport viewport = (JViewport) getParent();
		JScrollPane scrollpane = (JScrollPane) viewport.getParent();
		
		setPreferredSize(new Dimension(DataTable.SELECTION_STANDART,
				tableUI.getPreferredSize().height));
		
		viewport.setViewPosition(
				scrollpane.getViewport().getViewPosition());
	}
	
	public void fireTableDataChanged() {
		updateViewport();
		repaint();
	}
	
	public DataRow getRowAtPoint(MouseEvent e) {
		DataTableModel tableModel = (DataTableModel) tableUI.getModel();
		return tableModel.getRow(rowAtPoint(e.getPoint()));
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
		updateViewport();
	}

	/**
	 * Cell renderer
	 */
	private class CellRenderer extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int rowIndex,
				int columnIndex) {

			DataTableModel tableModel = (DataTableModel) tableUI.getModel();
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
