import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


public class Picture {
	public int COLUMNS = 9;
	
	private int rowNum;
	private int numLines;
	private int numWords;
	private int attempts;
	private int rt;
	private int rsp;
	private int answer;
	private int phase; //1 =  collab. 2 = test
	private boolean correct;
	private String name;
	private String picNum;
	private String fullText;
	private String[] fieldNames = {"rowNum", "name", "picNum", "numLines", "numWords", "attempts", "rt", "rsp", 
			"answer", "correct", "phase", "fullText"};
	
	public Picture(String name, String picNum, String fullText) {
		this.name = name;
		this.picNum = picNum;
		this.fullText = fullText.replaceAll("(NEWLINE)", " ").replaceAll("( )", " ").replaceAll("  ", " ");
	}
	public void updateCollab(int numLines, int numWords, int attempts, int rt, boolean correct, int rowNum) {
		
		this.numLines = numLines;
		this.numWords = numWords;
		this.attempts = attempts;
		this.correct = correct;
		this.rt = rt;
		this.rowNum = rowNum;
		this.phase = 1;
	}
	public void updateTest1(boolean correct, String fullText, int numWords, int rt, int rowNum, int rsp, int answer) {
		
		this.correct = correct;
        fullText = fullText.replaceAll("(NEWLINE)", " ").replaceAll("[()]", " ");
		this.fullText = fullText;
		this.numWords = numWords;
		this.rt = rt;
		this.rowNum = rowNum;
		this.phase = 2;
		this.rsp = rsp;
		this.answer = answer;
	}
	public int getnLines() {
		return numLines;
	}
	public int getRowNum() {
		return rowNum;
	}
	public int getnWords() {
		return numWords;
	}
	public int getAttempts() {
		return attempts;
	}
	public int getRT() {
		return rt;
	}
	public boolean getCorrect() {
		return correct;
	}
	public String getName() {
		return name;
	}
	public String getPicNum() {
		return picNum;
	}
	public String getPhase() {
		if (phase == 1) {
			return "collab";
		} else if (phase == 2) {
			return "test";
		}
		return "problem";
	}
	public String getFullText() {
		return fullText;
	}
	public int getResponse() {
		return rsp;
	}
	public int getAnswer() {
		return this.answer;
	}
	public int size() {
		return fieldNames.length;
	}
	public String[] getFieldNames() {
		return fieldNames;
	}
	public String toString() {
		return "Name: " + name + ". numLines: " + numLines + 
				". numWords: " + numWords + ". Attempts: " + 
				attempts + ". RT: " + rt + ". Correct: " + 
				correct;
	}
	public void toSheet(Sheet sheet, Cell cell) {
		Row row = sheet.createRow(cell.rowNum);
		row.createCell(cell.colNum++).setCellValue(this.rowNum);
		row.createCell(cell.colNum++).setCellValue(this.name);
		row.createCell(cell.colNum++).setCellValue(this.picNum);
		row.createCell(cell.colNum++).setCellValue(this.numLines);
		row.createCell(cell.colNum++).setCellValue(this.numWords);
		row.createCell(cell.colNum++).setCellValue(this.attempts);
		row.createCell(cell.colNum++).setCellValue(this.rt);
		row.createCell(cell.colNum++).setCellValue(this.rsp);
		row.createCell(cell.colNum++).setCellValue(this.answer);
		row.createCell(cell.colNum++).setCellValue(this.correct);
		row.createCell(cell.colNum++).setCellValue(this.phase);
		row.createCell(cell.colNum++).setCellValue(this.fullText);
	}
	
}
