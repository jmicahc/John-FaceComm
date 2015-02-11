import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class sheetWriter {
	private Workbook workbook;
	private Cell sheetPtr;
	
	public sheetWriter(Workbook workbook) {
		this.workbook = workbook;
		this.sheetPtr = new Cell(0, 0);
		
	}
	public void createSheet(String sheetName) {
		workbook.createSheet(sheetName);
		
	}
	public Cell writePicDataToSheet(String sheetName, Picture[] pictures) {
		return writePicDataToSheet(sheetName, pictures, new Cell(sheetPtr.colNum, sheetPtr.rowNum));
	}
	public Cell writePicDataToSheet(String sheetName, Picture[] pictures, Cell cell) {
		int startCell = cell.colNum;
		
		Sheet sheet = workbook.getSheet(sheetName);
		Row namesRow = sheet.createRow(cell.rowNum);
		for (String name : pictures[0].getFieldNames()) {
			namesRow.createCell(cell.colNum++).setCellValue(name);
		}
		cell.colNum = 0;
		
		for (Picture picture : pictures) {
			Row row = sheet.createRow(cell.rowNum++);
			cell.colNum = startCell;
			row.createCell(cell.colNum++).setCellValue(picture.getRowNum());
			row.createCell(cell.colNum++).setCellValue(picture.getName());
			row.createCell(cell.colNum++).setCellValue(picture.getPicNum());
			row.createCell(cell.colNum++).setCellValue(picture.getnLines());
			row.createCell(cell.colNum++).setCellValue(picture.getnWords());
			row.createCell(cell.colNum++).setCellValue(picture.getAttempts());
			row.createCell(cell.colNum++).setCellValue(picture.getRT());
			row.createCell(cell.colNum++).setCellValue(picture.getResponse());
			row.createCell(cell.colNum++).setCellValue(picture.getAnswer());
			row.createCell(cell.colNum++).setCellValue(picture.getCorrect());
			row.createCell(cell.colNum++).setCellValue(picture.getPhase());
			row.createCell(cell.colNum++).setCellValue(picture.getFullText());
		}
		return cell;
	}
	public void setSheetPointer(int row, int cell) {
		sheetPtr.colNum = cell;
		sheetPtr.rowNum = row;
	}
	
}
