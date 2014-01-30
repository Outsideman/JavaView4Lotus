import java.util.ArrayList;

public class DataHeader {
	private ArrayList headerValues = new ArrayList();
	private DataColumn resortedColumn;

	DataHeader() {

	}

	public int getColumnCount() {
		return headerValues.size();
	}

	public DataColumn getColumn(int pos) {
		if (!headerValues.isEmpty() && pos >= 0 && pos < headerValues.size()) {
			return (DataColumn) headerValues.get(pos);
		} else {
			return null;
		}
	}

	public void addColumns(String[] columns) {
		for (int i = 0; i < columns.length; i++) {
			appendColumn(columns[i]);
		}
	}

	public DataColumn appendColumn(String columnName) {
		DataColumn column = new DataColumn(this, columnName);
		column.setPosition(headerValues.size());
		headerValues.add(column);
		return column;
	}

	public boolean isDefaultSort() {
		return resortedColumn == null;
	}

	public DataColumn getResortedColumn() {
		return resortedColumn;
	}

	public ArrayList getCategoryColumns() {
		ArrayList result = new ArrayList();
		if (!headerValues.isEmpty()) {
			for (int i = 0; i < getColumnCount(); i++) {
				DataColumn column = getColumn(i);
				if (column.isCategory()) {
					result.add(column);
				}
			}
		}
		return result;
	}

	public void toggleColumnSort(int columnIndex) {
		DataColumn column = getColumn(columnIndex);
		if (column == null || !column.isResortable())
			return;

		if (column.isResortedDescending()) {
			column.setResorted(DataColumn.SORT_NONE);
			column = null;
		} else if (column.isResortedAscending()) {
			if (column.isResortDescending()) {
				column.setResorted(DataColumn.SORT_DESCENDING);
			} else {
				column.setResorted(DataColumn.SORT_NONE);
				column = null;
			}
		} else {
			if (column.isResortAscending()) {
				column.setResorted(DataColumn.SORT_ASCENDING);
			} else if (column.isResortDescending()) {
				column.setResorted(DataColumn.SORT_DESCENDING);
			}
		}

		if (resortedColumn != null && column != null
				&& !resortedColumn.equals(column)) {
			resortedColumn.setResorted(DataColumn.SORT_NONE);
		}

		resortedColumn = column;
	}

	public int compareColumnValues(int columnIndex, boolean sortDescending,
			Object obj1, Object obj2) {
		int result = 0;

		DataColumn column = getColumn(columnIndex);
		if (column == null)
			return result;

		if (obj1 != null && obj2 != null && !obj1.equals(obj2)) {
			switch (column.getDataType()) {
				case DataColumn.DATA_TEXT :
					result = obj1.toString().compareTo(obj2.toString());
				case DataColumn.DATA_NUMERIC :
					// TODO
					break;
				case DataColumn.DATA_DATE :
					// TODO
					break;
			}
		}

		if (sortDescending) {
			return -1 * result;
		} else {
			return result;
		}
	}

}
