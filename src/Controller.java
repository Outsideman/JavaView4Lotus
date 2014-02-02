import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Controller {
	private Application app;
	private JScrollPane scrollpane;
	private TableUI tableUI;
	private TableSelectionUI tableSelectionUI;
	private org.w3c.dom.Document document = null;

	Controller(Application app) {
		this.app = app;
		initTable();
	}

	public void initTable() {
		
		//initialize main table
		tableUI = new TableUI();
		
		//load preferences
		String xml = getParam("xml");
		if (!"".equals(xml)) {
			loadXML(xml);
		} else {
			xml = getParam("demo");
			if (!"".equals(xml)) {
				loadDemo(xml);
			} else {
				loadDemo("default");
			}
		}
		
		//adding table to applet
		scrollpane = new JScrollPane(tableUI);
		app.getContentPane().add(scrollpane, BorderLayout.CENTER);
		
		//adding selection table to applet
		if (tableUI.getData().hasSelection()) {
			tableSelectionUI = new TableSelectionUI(tableUI);
			tableUI.setSelectionTable(tableSelectionUI);
			scrollpane.setRowHeaderView(tableSelectionUI);
			scrollpane.setCorner(JScrollPane.UPPER_LEFT_CORNER, tableSelectionUI.getTableHeader());
		} else {
			scrollpane.setRowHeaderView(null);
		}
		
	}

	public String getParam(String name) {
		return getParam(name, "");
	}

	public String getParam(String name, String defaultValue) {
		String value = app.getParameter(name);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	public void loadDemo(String demo) {
		URL url = null;
		try {
			url = new URL(app.getCodeBase(), "conf/" + demo + ".xml");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			setXML(url.openStream());
		} catch (IOException e) {
			if (!"default".equals(demo)) {
				System.out.println("WARNING! Demo '" + demo + "' is not found");
				loadDemo("default");
			} else {
				e.printStackTrace();
				return;
			}
		}
		rebuildDataTable();
	}

	public void loadXML(String xml) {
		setXML(xml);
		rebuildDataTable();
	}

	public void setXML(InputStream xml) {
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setXML(String xml) {
		try {
			document = DocumentBuilderFactory
				    .newInstance()
				    .newDocumentBuilder()
				    .parse(new InputSource(new StringReader(xml)));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void rebuildDataTable() {
		if (document == null)
			return;

		// set current processing table
		DataTable table = tableUI.getData();
		table.reset();

		int index;
		Color color;

		// get root element 'table'
		Element root = document.getDocumentElement();

		// set selection bar width
		String selectionType = root.getAttribute("selectionBar");
		if ("true".equals(selectionType)) {
			table.setSelection(true);
			table.setSelectionWidth(DataTable.SELECTION_STANDART);
		} else {
			table.setSelection(false);
		}

		// set auto resizable column
		try {
			index = Integer.parseInt(root.getAttribute("resizeColumnIndex"));
		} catch (NumberFormatException e) {
			index = DataTable.COLUMN_NONE;
		}
		table.setAutoResizeColumn(index);

		// set row color
		try {
			color = Color.decode(root.getAttribute("rowColor"));
		} catch (NumberFormatException e) {
			color = DataTable.ROWCOLOR_DEFAULT;
		}
		table.setRowColor(color);

		// set alternative row color
		try {
			color = Color.decode(root.getAttribute("rowColorAlt"));
		} catch (NumberFormatException e) {
			color = DataTable.ROWCOLORALT_DEFAULT;
		}
		table.setRowColorAlt(color);

		/**
		 * Customize columns and set data
		 */

		NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {

			Node node = nodeList.item(i);
			if ("header".equals(node.getNodeName())) {
				processHeader(node);
				continue;
			}
			if ("data".equals(node.getNodeName())) {
				processData(node);
				continue;
			}

		}

		tableUI.rebuild();
	}

	private void processHeader(Node node) {
		NodeList columnNodeList = node.getChildNodes();
		for (int k = 0; k < columnNodeList.getLength(); k++) {

			Node columnNode = columnNodeList.item(k);
			if ("column".equals(columnNode.getNodeName())) {
				processColumn(columnNode);
			}

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processColumn(Node node) {
		DataColumn column = tableUI.getData().getHeader().appendColumn("");
		HashMap attr = new HashMap();

		NodeList nodeList = node.getChildNodes();
		for (int k = 0; k < nodeList.getLength(); k++) {
			Node attrNode = nodeList.item(k);
			if (attrNode.getNodeType() == Node.ELEMENT_NODE) {
				attr.put(attrNode.getNodeName(), attrNode.getTextContent());
			}
		}

		setColumnAlignment(column, (String) attr.get("alignment"));
		setColumnFontColor(column, (String) attr.get("fontColor"));
		setColumnFontFace(column, (String) attr.get("fontFace"));
		setColumnFontPointSize(column, (String) attr.get("fontPointSize"));
		setColumnFontStyle(column, (String) attr.get("fontStyle"));
		setColumnHeaderAlignment(column, (String) attr.get("headerAlignment"));
		setColumnHeaderFontColor(column, (String) attr.get("headerFontColor"));
		setColumnHeaderFontFace(column, (String) attr.get("headerFontFace"));
		setColumnHeaderFontPointSize(column, (String) attr.get("headerFontPointSize"));
		setColumnHeaderFontStyle(column, (String) attr.get("headerFontStyle"));
		setColumnListSep(column, (String) attr.get("listSep"));
		setColumnCategory(column, (String) attr.get("category"));
		setColumnIcon(column, (String) attr.get("icon"));
		setColumnResize(column, (String) attr.get("resize"));
		setColumnResortAscending(column, (String) attr.get("resortAscending"));
		setColumnResortDescending(column, (String) attr.get("resortDescending"));
		setColumnResponse(column, (String) attr.get("response"));
		setColumnSecondaryResortColumnIndex(column, (String) attr.get("secondaryResortColumnIndex"));
		setColumnSecondaryResortDescending(column, (String) attr.get("secondaryResortDescending"));
		setColumnSorted(column, (String) attr.get("sorted"));
		setColumnCaseSensitiveSort(column, (String) attr.get("caseSensitiveSort"));
		setColumnAccentSensitiveSort(column, (String) attr.get("accentSensitiveSort"));
		setColumnSortDescending(column, (String) attr.get("sortDescending"));
		setColumnShowTwistie(column, (String) attr.get("showTwistie"));
		setColumnTitle(column, (String) attr.get("title"));
		setColumnNumberAttrib(column, (String) attr.get("numberAttrib"));
		setColumnNumberDigits(column, (String) attr.get("numberDigits"));
		setColumnTimeDateFmt(column, (String) attr.get("timeDateFmt"));
		setColumnDateFmt(column, (String) attr.get("dateFmt"));
		setColumnTimeFmt(column, (String) attr.get("timeFmt"));
		setColumnWidth(column, (String) attr.get("width"));
		setColumnDataType(column, (String) attr.get("dataType"));
		setColumnSortDescending(column, (String) attr.get("sortDescending"));

	}

	/**
	 * Set parameters for DataTable column
	 * ==================================================
	 * 
	 * @param column
	 * @param value
	 */

	private void setColumnDataType(DataColumn column, String value) {
		if ("text".equals(value)) {
			column.setDataType(DataColumn.DATA_TEXT);
			return;
		}
		if ("numeric".equals(value)) {
			column.setDataType(DataColumn.DATA_NUMERIC);
			return;
		}
		if ("date".equals(value)) {
			column.setDataType(DataColumn.DATA_DATE);
			return;
		}
		if ("color".equals(value)) {
			column.setDataType(DataColumn.DATA_COLOR);
			return;
		}
		if ("icon".equals(value)) {
			column.setDataType(DataColumn.DATA_ICON);
			return;
		}
	}

	private void setColumnWidth(DataColumn column, String value) {
		if (value == null)
			return;

		try {
			column.setWidth(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			// nothing
		}
	}

	private void setColumnTimeFmt(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnDateFmt(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnTimeDateFmt(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnNumberDigits(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnNumberAttrib(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnTitle(DataColumn column, String value) {
		column.setTitle(value);
	}

	private void setColumnShowTwistie(DataColumn column, String value) {
		column.setShowTwistie("true".equals(value));
	}

	private void setColumnSortDescending(DataColumn column, String value) {
		column.setSortDescending("true".equals(value));
	}

	private void setColumnAccentSensitiveSort(DataColumn column, String value) {
		column.setAccentSensitiveSort("true".equals(value));
	}

	private void setColumnCaseSensitiveSort(DataColumn column, String value) {
		column.setCaseSensitiveSort("true".equals(value));
	}

	private void setColumnSorted(DataColumn column, String value) {
		column.setSorted("true".equals(value));
	}

	private void setColumnSecondaryResortDescending(DataColumn column, String value) {
		column.setSecondaryResortDescending("true".equals(value));
	}

	private void setColumnSecondaryResortColumnIndex(DataColumn column, String value) {
		if (value == null)
			return;

		try {
			column.setSecondaryResortColumnIndex(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			// nothing
		}
	}

	private void setColumnResponse(DataColumn column, String value) {
		column.setResponse("true".equals(value));
	}

	private void setColumnResortDescending(DataColumn column, String value) {
		column.setResortDescending("true".equals(value));
	}

	private void setColumnResortAscending(DataColumn column, String value) {
		column.setResortAscending("true".equals(value));
	}

	private void setColumnResize(DataColumn column, String value) {
		column.setResize("true".equals(value));
	}

	private void setColumnIcon(DataColumn column, String value) {
		column.setIcon("true".equals(value));
	}

	private void setColumnCategory(DataColumn column, String value) {
		column.setCategory("true".equals(value));
	}

	private void setColumnListSep(DataColumn column, String value) {
		column.setListSeparator(value);
	}

	private void setColumnHeaderFontStyle(DataColumn column, String value) {
		if (value == null)
			return;

		int fontStyle = 0;
		String arr[] = value.split(" ");

		for (int i = 0; i < arr.length; i++) {
			if ("bold".equals(arr[i])) {
				fontStyle += Font.BOLD;
			} else if ("italic".equals(arr[i])) {
				fontStyle += Font.ITALIC;
			}
		}

		column.setHeaderFontStyle(fontStyle);
	}

	private void setColumnHeaderFontPointSize(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnHeaderFontFace(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnHeaderFontColor(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnHeaderAlignment(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnFontStyle(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnFontPointSize(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnFontFace(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	private void setColumnFontColor(DataColumn column, String value) {
		if (value == null)
			return;

		try {
			column.setFontColor(Color.decode(value));
		} catch (NumberFormatException e) {
			// nothing
		}
	}

	private void setColumnAlignment(DataColumn column, String value) {
		// TODO Auto-generated method stub

	}

	/**
	 * Set data for DataTable
	 * 
	 * @param node
	 */

	private void processData(Node root) {
		NodeList nodeList = root.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {

			Node node = nodeList.item(i);
			if ("notesView".equals(node.getNodeName())) {
				processNotesView(node);
				continue;
			}
			if ("notesSearch".equals(node.getNodeName())) {
				processNotesSearch(node);
				continue;
			}
			if ("row".equals(node.getNodeName())) {
				processRow(node);
				continue;
			}

		}
	}

	private void processRow(Node root) {
		NodeList nodeList = root.getChildNodes();
		DataRow row = tableUI.getData().addRow();

		for (int k = 0; k < nodeList.getLength(); k++) {
			Node node = nodeList.item(k);
			if ("columnValue".equals(node.getNodeName())) {
				row.appendValue(node.getTextContent());
			}
		}
	}

	private void processNotesSearch(Node node) {
		// TODO Auto-generated method stub

	}

	private void processNotesView(Node node) {
		// TODO Auto-generated method stub

	}

}
