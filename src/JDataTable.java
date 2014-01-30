import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class JDataTable extends JTable {
	public static final int INDENT_SIZE = 20;

	private JDataSelectionTable selectionTable;
	private boolean isColumnWidthChanged;
	private Application applet;
	
	private static final CellRendererPane CELL_RENDER_PANE = new CellRendererPane();

	JDataTable() {
		super(new DataTableModel());
		setUI(new MultiSpanCellTableUI());
		addMouseListener(new TableMouseListener());
		getTableHeader().addMouseListener(new TableHeaderMouseListener());
	}

	public Application getApplet() {
		if (applet == null) {
			Component obj = getParent();
			while (obj != null) {
				if (obj instanceof Application) {
					applet = (Application) obj;
					break;
				}
				obj = obj.getParent();
			}
		}
		return applet;
	}

	public ImageIcon getImage(String name) {
		return new ImageIcon(getApplet().getImage(getApplet().getCodeBase(),
				"images/" + name));
	}

	public DataTable getData() {
		return ((DataTableModel) getModel()).getData();
	}

	public boolean getColumnWidthChanged() {
		return isColumnWidthChanged;
	}

	public void setColumnWidthChanged(boolean widthChanged) {
		isColumnWidthChanged = widthChanged;
	}

	public int getRowHeight() {
		return super.getRowHeight() + 2;
	}
	
	public void rebuild() {
		// Rebuild data
		getData().updateViewStructure();

		// fire table
		DataTableModel model = ((DataTableModel) getModel());
		model.fireTableStructureChanged();
		
		// Common table properties
		setFillsViewportHeight(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // AUTO_RESIZE_OFF
		//setTableHeader(createTableHeader());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setIntercellSpacing(new Dimension(0, 0));
		setShowGrid(false);

		// Customizable properties
		setBackground(getData().getRowColor());
		//setRowHeight(getRowHeight());

		// Header properties
		JTableHeader header = getTableHeader();
		header.setReorderingAllowed(false);

		// Header handlers
		header.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Point pt = new Point(e.getPoint());
				int columnIndex = getTableHeader().columnAtPoint(pt);
				if (columnIndex < 0)
					return;
				pt.x -= 4;
				if (columnIndex != getTableHeader().columnAtPoint(pt))
					return;
				pt.x += 8;
				if (columnIndex != getTableHeader().columnAtPoint(pt))
					return;
				columnIndex = convertColumnIndexToModel(columnIndex);
				if (columnIndex < 0)
					return;
				toggleColumnSort(columnIndex);
			}
		});

		// Column properties
		DataHeader dataHeader = model.getHeader();
		int columnCount = dataHeader.getColumnCount();

		for (int i = columnCount - 1; i >= 0; i--) {
			TableColumn column = header.getColumnModel().getColumn(i);
			DataColumn dataColumn = dataHeader.getColumn(i);

			column.setPreferredWidth(dataColumn.getWidth());
			column.setHeaderRenderer(new HeaderRenderer(dataColumn));
			column.setCellRenderer(new CellRenderer(dataColumn));
		}

		// Resize column by resizing the applet
		JScrollPane scrollPane = (JScrollPane) getParent().getParent();
		scrollPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				autoresize();
			}
		});

		// Create selection table
		if (model.getData().hasSelection()) {
			selectionTable = new JDataSelectionTable(this);
			selectionTable.setFocusable(false);
			selectionTable.setPreferredScrollableViewportSize(selectionTable
					.getPreferredSize());
			scrollPane.setRowHeaderView(selectionTable);
			scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
					selectionTable.getTableHeader());
			selectionTable.rebuild();
		} else {
			scrollPane.setRowHeaderView(null);
		}

	}

	public void columnMarginChanged(ChangeEvent e) {
		super.columnMarginChanged(e);
		setColumnWidthChanged(true);
	}

	public void autoresize() {		
		int columnIndex = getData().getAutoResizeColumn();
		if (columnIndex == DataTable.COLUMN_NONE)
			return;

		TableColumnModel model = getTableHeader().getColumnModel();
		if (columnIndex > model.getColumnCount() - 1)
			return;

		int width = 0;

		for (int i = 0; i < model.getColumnCount(); i++) {
			if (i != columnIndex) {
				width += model.getColumn(i).getWidth();
			}
		}

		JViewport viewport = (JViewport) getParent();
		int allColumnWidth = width
				+ getData().getHeader().getColumn(columnIndex).getWidth();
		if (viewport.getWidth() > allColumnWidth) {
			model.getColumn(columnIndex).setPreferredWidth(
					viewport.getWidth() - width);
		}

	}

	public void toggleColumnSort(int columnIndex) {
		DataTableModel model = ((DataTableModel) getModel());

		model.getData().getHeader().toggleColumnSort(columnIndex);
		model.getData().updateViewStructure();
		model.fireTableDataChanged();

		getTableHeader().repaint();
		selectionTable.rebuild();
	}

	/**
	 * Handle action on active row
	 */
	public void fireRow() {
		int rowIndex = getSelectedRow();
		if (rowIndex < 0)
			return;
		DataTableModel model = ((DataTableModel) getModel());
		DataRow row = model.getRow(rowIndex);
		if (row instanceof DataCategory) {
			DataCategory category = (DataCategory) row;
			category.setExpanded(!category.isExpanded());
			model.getData().updateViewStructure();
			model.fireTableDataChanged();
			setRowSelectionInterval(rowIndex, rowIndex);
			selectionTable.rebuild();
		} else {
			// TODO Open document/row
		}
	}

	/////////////////////////////// BETTER JTABLE ////////////////////////////////////////
	
    /**
     * Creates a JTableHeader that paints the table header background to the right
     * of the right-most column if neccesasry.
     */
    private JTableHeader createTableHeader() {
        return new JTableHeader(getColumnModel()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // if this JTableHEader is parented in a JViewport, then paint the
                // table header background to the right of the last column if
                // neccessary.
                JViewport viewport = (JViewport) table.getParent();
                if (viewport != null && table.getWidth() < viewport.getWidth()) {
                    int x = table.getWidth();
                    int width = viewport.getWidth() - table.getWidth();
                    paintHeader(g, getTable(), x, width);
                }
            }
        };
    }
    
    /**
     * Paints the given JTable's table default header background at given
     * x for the given width.
     */
    private static void paintHeader(Graphics g, JTable table, int x, int width) {
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component component = renderer.getTableCellRendererComponent(
                table, "", false, false, -1, 2);

        component.setBounds(0,0,width, table.getTableHeader().getHeight());

        ((JComponent)component).setOpaque(false);
        CELL_RENDER_PANE.paintComponent(g, component, null, x, 0,
                width+1, table.getTableHeader().getHeight(), true);
    }
	
    public static JScrollPane createStripedJScrollPane(JTable table) {
        JScrollPane scrollPane =  new JScrollPane(table);
        scrollPane.setViewport(new StripedViewport(table));
        scrollPane.getViewport().setView(table);
        //scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                createCornerComponent(table));
        return scrollPane;
    }
    
    /**
     * Creates a component that paints the header background for use in a
     * JScrollPane corner.
     */
    private static JComponent createCornerComponent(final JTable table) {
        return new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                paintHeader(g, table, 0, getWidth());
            }
        };
    }
    
    /////////////////////////////// BETTER JTABLE ////////////////////////////////////////
    
	/**
	 * Paints the backgrounds of the implied empty rows when the table model is
	 * insufficient to fill all the visible area available to us. We don't
	 * involve cell renderers, because we have no data.
	 */
	protected void paintEmptyRows(Graphics g) {
		final int rowCount = getRowCount();
		final Rectangle clip = g.getClipBounds();
		if (rowCount * rowHeight < clip.height) {
			for (int i = rowCount; i <= clip.height / rowHeight; ++i) {
				g.setColor(colorForRow(i));
				g.fillRect(clip.x, i * rowHeight, clip.width, rowHeight);
			}
		}
	}

	/**
	 * Returns the appropriate background color for the given row.
	 */
	protected Color colorForRow(int rowIndex) {
		if (rowIndex % 2 == 0) {
			return ((DataTableModel) getModel()).getData().getRowColor();
		} else {
			return ((DataTableModel) getModel()).getData().getRowColorAlt();
		}
	}

	/**
	 * Paints empty rows too, after letting the UI delegate do its painting.
	 */
	public void paint(Graphics g) {
		super.paint(g);
		//paintEmptyRows(g);
	}

	/**
	 * The header renderer
	 */
	private static class HeaderRenderer extends JLabel
			implements
				TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private DataColumn dataColumn;

		HeaderRenderer(DataColumn dataColumn) {
			this.dataColumn = dataColumn;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int row,
				int col) {

			setBorder(new BevelBorder(BevelBorder.RAISED,
					SystemColor.controlLtHighlight, SystemColor.control,
					SystemColor.controlShadow, SystemColor.control));

			setText(dataColumn.getTitle());
			setHorizontalAlignment(dataColumn.getHeaderAlignment());
			setFont(dataColumn.getHeaderFont());
			setForeground(dataColumn.getHeaderFontColor());

			return this;
		}

		public void paint(Graphics g) {
			super.paint(g);

			if (!dataColumn.isResortable())
				return;

			FontMetrics fm = getFontMetrics(getFont());
			int x = fm.stringWidth(getText()) + 2;
			if (x > getWidth() - 16)
				x = getWidth() - 16;
			int y = getHeight() / 2 - 8;

			g.setColor(SystemColor.control);
			g.fillRect(x + 2, y, 13, 15);
			x += 8;
			g.setColor(SystemColor.controlText);

			int[] xp = {x - 3, x, x + 3};
			int[] yp = {y + 7, y + 4, y + 7};

			if (dataColumn.isResortAscending()) {
				if (!dataColumn.isResortDescending()) {
					yp[0] += 3;
					yp[1] += 3;
					yp[2] += 3;
				}
				if (!dataColumn.isResortedAscending()) {
					g.drawPolyline(xp, yp, 3);
				} else {
					g.fillPolygon(xp, yp, 3);
					g.drawPolygon(xp, yp, 3);
				}
			}

			if (dataColumn.isResortDescending()) {
				if (!dataColumn.isResortAscending()) {
					y += 13;
				} else {
					y += 16;
				}
				yp[0] = y - 6;
				yp[1] = y - 3;
				yp[2] = y - 6;
				if (!dataColumn.isResortedDescending()) {
					g.drawPolyline(xp, yp, 3);
				} else {
					g.fillPolygon(xp, yp, 3);
					g.drawPolygon(xp, yp, 3);
				}
			}
		}
	}

	/**
	 * Cells renderer
	 */
	private class CellRenderer extends DefaultTableCellRenderer {
		private Border emptyBorder;
		private DataColumn dataColumn;
		private int translateX = 0;

		CellRenderer(DataColumn dataColumn) {
			this.dataColumn = dataColumn;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int rowIndex,
				int columnIndex) {

			DataTableModel tableModel = (DataTableModel) getModel();
			DataRow row = tableModel.getRow(rowIndex);
			DataColumn column = tableModel.getHeader().getColumn(columnIndex);

			// Set default table preferences
			setBackground(colorForRow(rowIndex));

			// Set text preferences
			setForeground(column.getFontColor());
			setFont(new Font(column.getFontFace(), column.getFontStyle(),
					column.getFontPointSize()));

			translateX = 0;
			emptyBorder = new EmptyBorder(0, 1, 0, 0);

			if (row instanceof DataCategory) {
				DataCategory category = (DataCategory) row;
				if (category.getColumnPosition() == columnIndex) {
					if (category.isExpanded()) {
						setIcon(getImage("minus.gif"));
					} else {
						setIcon(getImage("plus.gif"));
					}
					setText((String) row.getValue(columnIndex));
					translateX = JDataTable.INDENT_SIZE
							* category.getIndentLevel();
					emptyBorder = new EmptyBorder(0, translateX, 0, 0);
				} else {
					setText("");
					setIcon(null);
				}
			} else {
				if (column.isCategory()) {
					setText("");
					setIcon(null);
				} else {
					setText((String) row.getValue(columnIndex));
					setIcon(null);
				}
			}

			// Set selection preferences
			if (selected) {
				setBorder(new CompoundBorder(new CursorBorder(columnIndex == 0,
						false), emptyBorder));
			} else {
				setBorder(emptyBorder);
			}

			return this;
		}

		public void paint(Graphics g) {
			super.paint(g);
		}
	}

	/**
	 * Selection border as used in Lotus Notes
	 */
	private static class CursorBorder extends AbstractBorder {
		private boolean drawLeft;
		private boolean drawRight;

		CursorBorder(boolean drawLeft, boolean drawRight) {
			this.drawLeft = drawLeft;
			this.drawRight = drawRight;
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			Color color = g.getColor();
			g.setColor(new Color(0, 0, 0));
			g.drawRect(x, y, width - 1, 1);
			g.drawRect(x, y + height - 2, width - 1, 1);

			if (drawLeft)
				g.drawRect(x, y, 1, height - 1);

			if (drawRight)
				g.drawRect(x + width - 2, y, 1, height - 1);

			g.setColor(color);
		}
	}

	/**
	 * Handles mouse clicks
	 */
	private class TableMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent event) {
			switch (event.getClickCount()) {
				case 1 :
					DataRow row = ((DataTableModel) getModel())
							.getRow(rowAtPoint(event.getPoint()));
					if (row instanceof DataCategory) {
						DataCategory category = (DataCategory) row;
						int iconPosition = category.getIndentLevel()
								* JDataTable.INDENT_SIZE;
						if (event.getPoint().x > iconPosition - 4
								&& event.getPoint().x < iconPosition + 16) {
							fireRow();
						}
					}
					break;
				case 2 :
					fireRow();
					break;
			}
		}

	}

	private class TableHeaderMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent event) {
			//JScrollPane scrollPane = (JScrollPane) getParent().getParent();
			//scrollPane
			//	.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}

		public void mouseReleased(MouseEvent e) {
			/* On mouse release, check if column width has changed */
			if (getColumnWidthChanged()) {
				// Do whatever you need to do here
				//autoresize();
				// Reset the flag on the table.
				setColumnWidthChanged(false);
			}

			JScrollPane scrollPane = (JScrollPane) getParent().getParent();
			scrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}

	}

}
