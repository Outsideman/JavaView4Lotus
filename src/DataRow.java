import java.util.ArrayList;

public class DataRow implements Comparable {
	private ArrayList cells = new ArrayList();
	private DataTable table;
	private boolean selected = false;
	private boolean inMultipleSelection = false;

	DataRow() {

	}

	DataRow(DataTable table) {
		this.table = table;
	}

	public String toString() {
		return cells.toString();
	}

	public DataCell getCell(int pos) {
		if (!cells.isEmpty() && pos >= 0 && pos < cells.size()) {
			return (DataCell) cells.get(pos);
		} else {
			return null;
		}
	}
	
	public Object getValue(int pos) {
		DataCell cell = getCell(pos);
		if(cell != null) {
			return cell.getValue();
		} else {
			return null;
		}
	}

	public DataCell appendCell(DataCell cell) {
		cells.add(cell);
		return cell;
	}
	
	public DataCell appendValue(Object obj) {
		return appendCell(new DataCell(this, obj));
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isInMultipleSelection() {
		return inMultipleSelection;
	}

	public void setInMultipleSelection(boolean inMultipleSelection) {
		this.inMultipleSelection = inMultipleSelection;
	}

	public DataTable getTable() {
		return table;
	}

	public int compareTo(Object arg0) {
		DataRow row = (DataRow) arg0;
		DataHeader header = getTable().getHeader();
		DataColumn column;
		int result = 0;

		if (header.isDefaultSort()) {
			for (int i = 0; i < header.getColumnCount(); i++) {
				column = header.getColumn(i);
				if (column.isSorted()) {
					result = header.compareColumnValues(i, column.isSortDescending(), getValue(i), row.getValue(i));
					if (result != 0)
						break;
				}
			}
		} else {
			column = header.getResortedColumn();
			int i = column.getPosition();
			result = header.compareColumnValues(i, column.isResortedDescending(), getValue(i), row.getValue(i));
			if (result == 0 && column.isSecondaryResort()) {
				boolean secondaryResortDescending = column.isSecondaryResortDescending();
				i = column.getSecondaryResortColumnIndex();
				column = header.getColumn(i);
				if (column != null) {
					result = header.compareColumnValues(i, secondaryResortDescending, getValue(i), row.getValue(i));
				}
			}
		}
		return result;
	}

}
