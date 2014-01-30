import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DataCategory extends DataRow implements Comparable {
	private String title;
	private ArrayList rows = new ArrayList();
	private ArrayList categories = new ArrayList();
	private HashMap categoryNames = new HashMap();
	private boolean expanded = false;
	private int indentLevel;
	private int columnPosition = 0;

	DataCategory(String title, int columnPosition) {
		this.title = title;
		this.columnPosition = columnPosition;
	}

	public String toString() {
		return title;
	}
	
	public Object getValue(int columnIndex) {
		return title;
	}

	public DataCategory addCategory(String categoryName, int columnPosition) {
		DataCategory category = (DataCategory) categoryNames.get(categoryName);
		if (category == null) {
			category = new DataCategory(categoryName, columnPosition);
			if (this.columnPosition == columnPosition) {
				category.setIndentLevel(getIndentLevel() + 1);
			} else {
				category.setIndentLevel(0);
			}
			categories.add(category);
			categoryNames.put(categoryName, category);
		}
		return category;
	}

	public ArrayList getRows() {
		return rows;
	}

	public void addRow(DataRow row) {
		rows.add(row);
	}

	public int compareTo(Object obj) {
		return toString().compareTo(((DataCategory) obj).toString());
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public ArrayList getVisibleRows() {
		Collections.sort(rows);
		Collections.sort(categories);

		ArrayList result = new ArrayList();
		result.addAll(rows);

		if (!categoryNames.isEmpty()) {
			for (int i = 0; i < categories.size(); i++) {
				DataCategory category = (DataCategory) categories.get(i);
				result.add(category);
				if (category.isExpanded()) {
					result.addAll(category.getVisibleRows());
				}
			}
		}
		return result;
	}

	public int getIndentLevel() {
		return indentLevel;
	}

	public void setIndentLevel(int indentLevel) {
		this.indentLevel = indentLevel;
	}

	public int getColumnPosition() {
		return columnPosition;
	}

}
