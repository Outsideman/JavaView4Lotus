import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DataTable {
	// CONSTANTS
	public static final Color ROWCOLOR_DEFAULT = Color.decode("#FFFFFF");
	public static final Color ROWCOLORALT_DEFAULT = Color.decode("#F8F8F8");

	public static final int SELECTION_STANDART = 30;
	public static final int SELECTION_DIALOG = 18;

	public static final int COLUMN_NONE = -1;

	// VARIABLES
	private DataHeader header = new DataHeader();
	private ArrayList rows = new ArrayList();
	private HashMap viewIndex = new HashMap();
	private ArrayList visibleRows = new ArrayList();
	private boolean selection = true;
	private int selectionWidth = SELECTION_STANDART;
	private int autoResizeColumn = COLUMN_NONE;

	private Color rowColor = ROWCOLOR_DEFAULT;
	private Color rowColorAlt = ROWCOLORALT_DEFAULT;

	public void reset() {
		header = new DataHeader();
		rows = new ArrayList();
		viewIndex = new HashMap();
		selection = true;
		selectionWidth = SELECTION_STANDART;
		autoResizeColumn = COLUMN_NONE;
		rowColor = ROWCOLOR_DEFAULT;
		rowColorAlt = ROWCOLORALT_DEFAULT;
	}
	
	public DataHeader getHeader() {
		return header;
	}

	public int getRowCount() {
		return rows.size();
	}

	public DataRow getRow(int index) {
		if (!rows.isEmpty() && index >= 0 && index < rows.size()) {
			return (DataRow) rows.get(index);
		} else {
			return null;
		}
	}

	public Color getRowColor() {
		return rowColor;
	}

	public Color getRowColorAlt() {
		return rowColorAlt;
	}

	public void setRowColorAlt(Color rowColorAlt) {
		this.rowColorAlt = rowColorAlt;
	}

	public void setRowColor(Color rowColor) {
		this.rowColor = rowColor;
	}

	public DataRow addRow() {
		DataRow row = new DataRow(this);
		rows.add(row);
		return row;
	}
	
	public void addRowData(Object[] rowData) {
		DataRow row = addRow();
		for (int i = 0; i < rowData.length; i++) {
			row.appendValue(rowData[i]);
		}
	}

	public void sort() {
		Collections.sort(rows);
	}

	public void updateViewStructure() {
		if (rows.isEmpty())
			return;

		DataCategory startCategory;
		int resortedIndex = 0;
		String key;
		boolean rebuildRows = false;

		DataColumn column = getHeader().getResortedColumn();
		if (column == null) {
			key = "DEFAULT";
		} else {
			resortedIndex = column.getPosition();
			key = resortedIndex + (column.isResortedAscending() ? "-1" : "-2");
		}

		startCategory = (DataCategory) viewIndex.get(key);
		if (startCategory == null) {
			startCategory = new DataCategory("", -1);
			viewIndex.put(key, startCategory);
			rebuildRows = true;
		}

		ArrayList categoryColumns = getHeader().getCategoryColumns();

		for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
			DataRow row = (DataRow) rows.get(rowIndex);
			DataCategory category = startCategory;

			if (!categoryColumns.isEmpty()) {
				for (int catIndex = 0; catIndex < categoryColumns.size(); catIndex++) {
					int columnPosition = ((DataColumn) categoryColumns
							.get(catIndex)).getPosition();
					if (columnPosition >= resortedIndex) {
						Object value = row.getValue(columnPosition);
						if (value != null) {
							String[] categoryArray = ((String) value)
									.split("\\\\");
							for (int catItemIndex = 0; catItemIndex < categoryArray.length; catItemIndex++) {
								category = category.addCategory(
										categoryArray[catItemIndex],
										columnPosition);
							}
						}
					}
				}
			}
			if (rebuildRows) {
				category.addRow(row);
			}
		}

		visibleRows = startCategory.getVisibleRows();
	}

	public ArrayList getVisibleRows() {
		return visibleRows;
	}

	public int getVisibleRowCount() {
		return visibleRows.size();
	}

	public DataRow getVisibleRow(int index) {
		if (!visibleRows.isEmpty() && index >= 0 && index < visibleRows.size()) {
			return (DataRow) visibleRows.get(index);
		} else {
			return null;
		}
	}

	public boolean hasSelection() {
		return selection;
	}

	public void setSelection(boolean selection) {
		this.selection = selection;
	}

	public int getSelectionWidth() {
		return selectionWidth;
	}

	public void setSelectionWidth(int selectionWidth) {
		this.selectionWidth = selectionWidth;
	}

	public int getAutoResizeColumn() {
		return autoResizeColumn;
	}

	public void setAutoResizeColumn(int autoResizeColumn) {
		this.autoResizeColumn = autoResizeColumn;
	}

}
