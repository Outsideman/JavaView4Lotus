import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel {
	protected DataTable data;

	DataTableModel() {
		data = new DataTable();
	}

	public int getColumnCount() {
		return data.getHeader().getColumnCount();
	}

	public int getRowCount() {
		return data.getVisibleRowCount();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return null; //see cell renderer
	}

	public DataTable getData() {
		return data;
	}

	public DataHeader getHeader() {
		return data.getHeader();
	}

	public DataRow getRow(int index) {
		return data.getVisibleRow(index);
	}

}
