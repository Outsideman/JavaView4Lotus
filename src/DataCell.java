import java.util.HashMap;

public class DataCell {
	DataRow row;
	HashMap attr;
	Object value;

	DataCell(DataRow row) {
		this.row = row;
	}
	
	DataCell(DataRow row, Object value) {
		this.row = row;
		this.value = value;
	}
	
	public String toString() {
		return value.toString();
	}
	
	public DataRow getRow() {
		return row;
	}

	public Object getValue() {
		return value;
	}
	
	public void setAttr(String attrName, Object attrValue) {
		attr.put(attrName, attrValue);
	}
	
}
