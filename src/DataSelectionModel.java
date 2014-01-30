import javax.swing.table.AbstractTableModel;

public class DataSelectionModel extends AbstractTableModel {
	private DataTableModel tableModel;

	DataSelectionModel(DataTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return tableModel.getRowCount();
	}

	public Object getValueAt(int arg0, int arg1) {
		return null;
	}

}
