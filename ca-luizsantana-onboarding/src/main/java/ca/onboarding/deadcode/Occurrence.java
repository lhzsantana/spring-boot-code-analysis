package ca.onboarding.deadcode;

public class Occurrence {
	
	private String file;
	
	private int line;
	
	private int column;
	
	private OccurrenceType occurrenceType;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public OccurrenceType getOccurrenceType() {
		return occurrenceType;
	}

	public void setOccurrenceType(OccurrenceType occurrenceType) {
		this.occurrenceType = occurrenceType;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

}
