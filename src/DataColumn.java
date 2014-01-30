import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;

public class DataColumn {
	// The alignment (justification) of data in a column
	public static final int ALIGMENT_DEFAULT = SwingConstants.LEFT;

	// The font color of data in a column
	public static final Color FONTCOLOR_DEFAULT = Color.decode("#000000");

	// The font face of data in a column
	public static final String FONTFACE_DEFAULT = "SansSerif";

	// The font point size of data in a column
	public static final int FONTPOINTSIZE_DEFAULT = 13;

	// List (multi-value) separator for values in a column
	public static final String SEPARATOR_DEFAULT = ", ";

	// The width of a column
	public static final int WIDTH_DEFAULT = 120;

	// User sorting types
	public static final int SORT_NONE = 0;
	public static final int SORT_ASCENDING = 1;
	public static final int SORT_DESCENDING = 2;

	// Column data type
	public static final int DATA_TEXT = 0;
	public static final int DATA_NUMERIC = 1;
	public static final int DATA_DATE = 2;
	public static final int DATA_COLOR = 3;
	public static final int DATA_ICON = 4;

	// The format of time-date data in a column
	public static final int TIMEDATE_DATE = 0;
	public static final int TIMEDATE_DATETIME = 1;
	public static final int TIMEDATE_TIME = 2;

	// The specific format of date data in a column
	public static final int DATE_YMD = 0; // for year, month, and day
	public static final int DATE_MD = 1; // for month and day
	public static final int DATE_YM = 2; // for year and month
	public static final int DATE_Y4M = 3; // for year (4 digit) and month

	// The format of time data in a column
	public static final int TIME_HMS = 1;
	public static final int TIME_HM = 0;
	public static final int TIME_H = 2;

	// Attributes for numeric values in a column
	public static final int NUMERIC_DEFAULT = 0;
	public static final int NUMERIC_PARENS = 2;
	public static final int NUMERIC_PUNCTUATED = 1;
	public static final int NUMERIC_PERCENT = 4;

	/**
	 * Private column variables
	 * -----------------------------------------------------------------
	 */
	private DataHeader header;

	// The alignment (justification) of data in a column
	private int alignment = ALIGMENT_DEFAULT;

	// The font color of data in a column
	private Color fontColor = FONTCOLOR_DEFAULT;

	// The font face of data in a column
	private String fontFace = FONTFACE_DEFAULT;

	// The font point size of data in a column
	private int fontPointSize = FONTPOINTSIZE_DEFAULT;

	// The font style of data in a column
	private int fontStyle = Font.PLAIN;

	// The alignment (justification) of the header in a column
	private int headerAlignment = ALIGMENT_DEFAULT;

	// The font color of the header in a column
	private Color headerFontColor = FONTCOLOR_DEFAULT;

	// The font face of the header in a column
	private String headerFontFace = FONTFACE_DEFAULT;

	// The font point size of the header in a column
	private int headerFontPointSize = FONTPOINTSIZE_DEFAULT;

	// The font style of the header in a column
	private int headerFontStyle = Font.BOLD;

	// Indicates whether a column is an auto-sorted column
	private boolean sorted = false;

	// Indicates whether an auto-sorted column is descending in sort order
	private boolean sortDescending = false;

	// Indicates whether an auto-sorted column is sorted with regard to accent
	private boolean accentSensitiveSort = false;

	// Indicates whether an auto-sorted column is sorted with regard to case
	private boolean caseSensitiveSort = false;

	// Indicates whether a column is categorized
	private boolean category = false;

	// Indicates whether column values are displayed as icons
	private boolean icon = false;

	// Indicates whether a column is resizable
	private boolean resize = true;

	// Indicates whether a column is a user-sorted column that can be sorted in
	// ascending order
	private boolean resortAscending = false;

	// Indicates whether a column is a user-sorted column that can be sorted in
	// descending order
	private boolean resortDescending = false;

	// The index of the secondary sorting column of a user-sorted column that
	// allows a secondary sorting column (negative value means no secondary
	// sorting)
	private int secondaryResortColumnIndex = -1;

	// Indicates whether a column contains only response documents
	private boolean response = false;

	// Indicates whether a secondary sorting column for a user-sorted column is
	// descending in sort order
	private boolean secondaryResortDescending = false;

	// Indicates whether an expandable column displays a twistie
	private boolean showTwistie = false;

	// List (multi-value) separator for values in a column
	private String listSep = SEPARATOR_DEFAULT;

	// Attributes for numeric values in a column
	private int numberAttrib = NUMERIC_DEFAULT;

	// Number of decimal places for numeric values in a column
	private int numberDigits = 0;

	// The format of time-date data in a column
	private int timeDateFmt = TIMEDATE_DATE;

	// The specific format of date data in a column
	private int dateFmt = DATE_YMD;

	// The format of time data in a column
	private int timeFmt = TIME_HM;

	// The title of a column, if any
	private String title = "";

	// The width of a column
	private int width = WIDTH_DEFAULT;

	// Data type
	private int dataType = DATA_TEXT;

	// Indicates whether a column is sorted by user
	private boolean resorted;
	private boolean resortedDescending = false;

	// Column position
	private int position = 0;

	/*******************************************************
	 * IMPLEMENTATION
	 *******************************************************/
	DataColumn(DataHeader header, String title) {
		this.header = header;
		this.title = title;
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public int getDateFmt() {
		return dateFmt;
	}

	public void setDateFmt(int dateFmt) {
		this.dateFmt = dateFmt;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public String getFontFace() {
		return fontFace;
	}

	public void setFontFace(String fontFace) {
		this.fontFace = fontFace;
	}

	public int getFontPointSize() {
		return fontPointSize;
	}

	public void setFontPointSize(int fontPointSize) {
		this.fontPointSize = fontPointSize;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	public int getHeaderAlignment() {
		return headerAlignment;
	}

	public void setHeaderAlignment(int headerAlignment) {
		this.headerAlignment = headerAlignment;
	}

	public Color getHeaderFontColor() {
		return headerFontColor;
	}

	public void setHeaderFontColor(Color headerFontColor) {
		this.headerFontColor = headerFontColor;
	}

	public String getHeaderFontFace() {
		return headerFontFace;
	}

	public void setHeaderFontFace(String headerFontFace) {
		this.headerFontFace = headerFontFace;
	}

	public int getHeaderFontPointSize() {
		return headerFontPointSize;
	}

	public void setHeaderFontPointSize(int headerFontPointSize) {
		this.headerFontPointSize = headerFontPointSize;
	}

	public int getHeaderFontStyle() {
		return headerFontStyle;
	}

	public void setHeaderFontStyle(int headerFontStyle) {
		this.headerFontStyle = headerFontStyle;
	}

	public boolean isSorted() {
		if (header.isDefaultSort()) {
			return sorted | category;
		} else {
			return resorted;
		}
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}

	public boolean isSortDescending() {
		return sortDescending;
	}

	public void setSortDescending(boolean sortDescending) {
		this.sortDescending = sortDescending;
	}

	public boolean isAccentSensitiveSort() {
		return accentSensitiveSort;
	}

	public void setAccentSensitiveSort(boolean accentSensitiveSort) {
		this.accentSensitiveSort = accentSensitiveSort;
	}

	public boolean isCaseSensitiveSort() {
		return caseSensitiveSort;
	}

	public void setCaseSensitiveSort(boolean caseSensitiveSort) {
		this.caseSensitiveSort = caseSensitiveSort;
	}

	public boolean isCategory() {
		return category;
	}

	public void setCategory(boolean category) {
		this.category = category;
	}

	public boolean isIcon() {
		return icon;
	}

	public void setIcon(boolean icon) {
		this.icon = icon;
	}

	public boolean isResize() {
		return resize;
	}

	public void setResize(boolean resize) {
		this.resize = resize;
	}

	public boolean isResortable() {
		return resortAscending || resortDescending;
	}

	public boolean isResortAscending() {
		return resortAscending;
	}

	public void setResortAscending(boolean resortAscending) {
		this.resortAscending = resortAscending;
	}

	public boolean isResortDescending() {
		return resortDescending;
	}

	public void setResortDescending(boolean resortDescending) {
		this.resortDescending = resortDescending;
	}

	public int getSecondaryResortColumnIndex() {
		return secondaryResortColumnIndex;
	}

	public void setSecondaryResortColumnIndex(int secondaryResortColumnIndex) {
		this.secondaryResortColumnIndex = secondaryResortColumnIndex;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public boolean isSecondaryResort() {
		return secondaryResortColumnIndex > 0;
	}

	public boolean isSecondaryResortDescending() {
		return secondaryResortDescending;
	}

	public void setSecondaryResortDescending(boolean secondaryResortDescending) {
		this.secondaryResortDescending = secondaryResortDescending;
	}

	public boolean isShowTwistie() {
		return showTwistie;
	}

	public void setShowTwistie(boolean showTwistie) {
		this.showTwistie = showTwistie;
	}

	public int getNumberAttrib() {
		return numberAttrib;
	}

	public void setNumberAttrib(int numberAttrib) {
		this.numberAttrib = numberAttrib;
	}

	public int getNumberDigits() {
		return numberDigits;
	}

	public void setNumberDigits(int numberDigits) {
		this.numberDigits = numberDigits;
	}

	public int getTimeDateFmt() {
		return timeDateFmt;
	}

	public void setTimeDateFmt(int timeDateFmt) {
		this.timeDateFmt = timeDateFmt;
	}

	public int getTimeFmt() {
		return timeFmt;
	}

	public void setTimeFmt(int timeFmt) {
		this.timeFmt = timeFmt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Font getHeaderFont() {
		try {
			return new Font(getHeaderFontFace(), getHeaderFontStyle(),
					getHeaderFontPointSize());
		} catch (Exception e) {
			return new Font("Arial", Font.PLAIN, 12);
		}
	}

	public String getListSeparator() {
		return listSep;
	}

	public void setListSeparator(String listSep) {
		this.listSep = listSep;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public boolean isResorted() {
		return resorted;
	}

	public boolean isResortedAscending() {
		return resorted && !resortedDescending;
	}

	public boolean isResortedDescending() {
		return resorted && resortedDescending;
	}

	public void setResorted(int resortType) {
		switch (resortType) {
			case SORT_NONE :
				resorted = false;
				break;
			case SORT_ASCENDING :
				resorted = true;
				resortedDescending = false;
				break;
			case SORT_DESCENDING :
				resorted = true;
				resortedDescending = true;
				break;
		}

	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
