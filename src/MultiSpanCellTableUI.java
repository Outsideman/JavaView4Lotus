import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

class MultiSpanCellTableUI extends BasicTableUI {

	public void paint(Graphics g, JComponent c) {
		Rectangle oldClipBounds = g.getClipBounds();
		Rectangle clipBounds = new Rectangle(oldClipBounds);
		int tableWidth = table.getColumnModel().getTotalColumnWidth();
		clipBounds.width = Math.min(clipBounds.width, tableWidth);
		g.setClip(clipBounds);

		int firstIndex = table.rowAtPoint(new Point(0, clipBounds.y));
		int lastIndex = table.getRowCount() - 1;

		Rectangle rowRect = new Rectangle(0, 0, tableWidth,
				table.getRowHeight() + table.getRowMargin());
		rowRect.y = firstIndex * rowRect.height;

		for (int index = firstIndex; index <= lastIndex; index++) {
			if (rowRect.intersects(clipBounds)) {
				// System.out.println(); // debug
				// System.out.print("" + index +": "); // row
				paintRow(g, index);
			}
			rowRect.y += rowRect.height;
		}
		g.setClip(oldClipBounds);
	}

	private void paintRow(Graphics g, int rowIndex) {
		DataTableModel model = (DataTableModel) table.getModel();
		DataRow row = model.getRow(rowIndex);
		DataCategory category = null;

		if (row instanceof DataCategory) {
			category = (DataCategory) row;
		}

		int numColumns = table.getColumnCount();
		for (int column = 0; column < numColumns; column++) {
			Rectangle cellRect = table.getCellRect(rowIndex, column, true);
			if (category == null) {
				paintCell(g, cellRect, rowIndex, column);
			} else {
				if (column < category.getColumnPosition()) {
					paintCell(g, cellRect, rowIndex, column);
				} else if (column == category.getColumnPosition()) {
					Rectangle lastColumnRect = table.getCellRect(rowIndex,
							numColumns, true);
					Rectangle extendedRect = new Rectangle(cellRect.x,
							cellRect.y, lastColumnRect.x + lastColumnRect.width
									- cellRect.x, cellRect.height);
					paintCell(g, extendedRect, rowIndex, column);
				}
			}
		}
	}

	private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
		int spacingHeight = table.getRowMargin();
		int spacingWidth = table.getColumnModel().getColumnMargin();

		// Color c = g.getColor();
		// g.setColor(table.getGridColor());
		// g.drawRect(cellRect.x, cellRect.y, cellRect.width - 1,
		// cellRect.height - 1);
		// g.setColor(c);

		cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y
				+ spacingHeight / 2, cellRect.width - spacingWidth,
				cellRect.height - spacingHeight);

		if (table.isEditing() && table.getEditingRow() == row
				&& table.getEditingColumn() == column) {
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		} else {
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = table.prepareRenderer(renderer, row, column);

			if (component.getParent() == null) {
				rendererPane.add(component);
			}
			rendererPane.paintComponent(g, component, table, cellRect.x,
					cellRect.y, cellRect.width, cellRect.height, true);
		}
	}
}