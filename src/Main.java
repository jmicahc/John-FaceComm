import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.io.*;
import java.lang.reflect.Array;

import javax.swing.JFileChooser;
public class FaceCommMain {
	
	private static class test1Data {
		int rt;
		boolean correct;
		int rowNum;
		int rsp;
		int answer;
		private test1Data(int rt, boolean correct, int rowNum, int rsp, int answer) {
			this.rsp = rsp;
			this.answer = answer;
			this.rt = rt;
			this.correct = correct;
		    this.rowNum = rowNum;
		}
	}
	
	public static ParcicipantDataBase database = new ParcicipantDataBase();
	public static PicturesDataBase picDatabase = new PicturesDataBase();
	
	private static final int DATA_SIZE = 7;
	
	private static final int typeColumn = 3;
	private static final int namesColumn = 2;
	private static final int textColumn = 7;
	private static final int rtColumn = 6;
	
	private static String prevName = "---";
	static Participant prevSj = null;
	private static int pairNum = 0;
	
	private static Sheet sheet;
	
	private static String endTest1 = "You have now finished the test phase";
	private static String endTest2 = "Experiment finished, please find the experimenter!";
	private static String endCollab = "Test trial-You are now going to do";
	
	private static Workbook workBook = new HSSFWorkbook();
	
	private static HashMap<String, Integer> coOccurances = new HashMap<String, Integer>();
	
	public static void main(String[] args) {
		Participant Sj = null;
		String name = "";
		String text = "";

		JFileChooser filechooser = new JFileChooser();
		int returnValue = filechooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			try {
				Workbook workbook = new HSSFWorkbook(new FileInputStream(filechooser.getSelectedFile()));
				sheet = workbook.getSheetAt(0);
				for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext(); ) { // Finds Participant answers and updates Participant database with num correct
					Row row = rit.next();
					
					if (row.getCell(textColumn)!=null) { 
						text = row.getCell(textColumn).toString().trim(); 
					}
					if (text.contains(".jpg..having to select")) { 
						
						name = text.substring(0, text.indexOf(" is now")).trim();
						String picNum = text.substring(text.indexOf(".jpg")-9, text.indexOf(".jpg")-6);
						String fullText = text.substring((text.indexOf("description:")+11));
						Picture picData = new Picture(name, picNum, fullText);
						
						if (!database.alreadyExists(name)) {
							Sj = new Participant();
							database.addParticipant(name,Sj);
						}
						updatePairsDatabase(name, Sj);
						
						int answer = Integer.parseInt((text.substring(text.indexOf(".jpg")-2, text.indexOf(".jpg"))));
						test1Data t1Data = isCorrect(answer, row.getRowNum(), name);
						picData.updateTest1(t1Data.correct, fullText, 
								fullText.split(" ").length, t1Data.rt, row.getRowNum(), t1Data.rsp, t1Data.answer);
						update(picData, row.getRowNum());
					}
					if (text.contains(".jpg")) {
						String nextJpg = text.substring(text.indexOf(".jpg")+1, text.length());
						if (nextJpg.contains(".jpg") && !row.getCell(typeColumn).toString().contains("(2D)")) {
							name = text.substring(0, text.indexOf(":d"));
							String other = text.substring(text.indexOf(".jpg")+8, text.indexOf("----", text.indexOf("----")+3));
							String pic = text.substring(text.indexOf(":d")+2, text.indexOf("_"));
							collectDataUpdate(other, row.getRowNum(), pic);
						}
					}
				}
				output();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	private static void collectDataUpdate(String other, int rowNum, String pic) {
		int numLines = 0,
				numWords = 0,
				RT,
				attempts = 1,
				rowNumStart = rowNum;

		boolean correct = false;
		String fullText = "";
		rowNum++;
		
		int startTime = (int) Math.round(Double.parseDouble(sheet.getRow(rowNum).getCell(rtColumn).toString()));
		Row row = sheet.getRow(rowNum);
		try {
			while (!row.getCell(textColumn).toString().startsWith(endCollab)) {
				row = sheet.getRow(rowNum);
				String cell = row.getCell(textColumn).toString();
				if (!(cell.contains("Your score is") || cell.contains("****"))) {
					numWords += cell.split(" ").length;
					numLines++;
					fullText += cell + " ";
				} else if (cell.contains("****")){
					attempts++;
					rowNum++;
					if (attempts==3) {
						correct = false;
						break;
					}
				} else {
					correct = true;
					rowNum++;
					break;
				}
					
				while (sheet.getRow(rowNum+=1).getCell(textColumn)==null) {};
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		RT = (int) Math.round(Double.parseDouble(row.getCell(rtColumn).toString())) - startTime;
		Picture picData = new Picture(other, pic, fullText);
		picData.updateCollab(numLines, numWords, attempts, RT, correct, rowNumStart);
		picDatabase.addPic(picData);

		database.getParticipant(other.trim()).updateCollab(picData);
		//database.getParticipant(other).printAll();
		
		
	}
	private static void updatePairsDatabase(String name, Participant Sj) {
		if (!name.equals(prevName) && !name.equals("server") && !prevName.equals("server")) {
			String UID = alphebetizeNames(name, prevName, 0);
			if (coOccurances.containsKey(UID)) {
				coOccurances.put(UID, coOccurances.get(UID)+1);
			} else  {
				
				coOccurances.put(UID, 1);
			}
			if (coOccurances.get(UID)==5) {
				ParticipantPair pair = new ParticipantPair(UID, database.getParticipant(prevName), database.getParticipant(name));
				database.addPair(pairNum, pair);
				pairNum++;
			}
	
		}
		prevName = name;
	}
	private static String alphebetizeNames(String name, String prev, int begin) {
		if (begin >= name.length() || begin >= prev.length()) {
			 return name+prev;
		} 
		if (name.charAt(begin)>prev.charAt(begin)) {
			return prev+name;
		} 
		else if (name.charAt(begin)<prev.charAt(begin)) {
			return name+prev;
		}
		return alphebetizeNames(name, prev, begin+1);
	}
	private static void output() {
		
		try {
			removeTrials(2, 1);
		} catch (Exception e1) {
			System.out.println("failed to remove trials");
			e1.printStackTrace();
		}
		Sheet rawSheet = workBook.createSheet();
		Sheet picSheet = workBook.createSheet();
		Sheet summarySheet = workBook.createSheet();
				
		writeRawSheet(rawSheet);
		writePicSheet(picSheet);
		writeSummarySheet(summarySheet);
		try {
			FileOutputStream output = new FileOutputStream("Data2.xlsx");
			System.out.println("Data written...");
			workBook.write(output);
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
				
	}
	private static void writeRawSheet(Sheet rawSheet) {
		int i = 0;
		for (ParticipantPair pair =  database.getPair(0); pair != null; pair = database.getPair(i+=1) )  {
			Participant p1 = pair.getParticipant(1);
			Participant p2 = pair.getParticipant(2);
			
			Sheet p1Sheet = workBook.createSheet(p1.getName());
			Sheet p2Sheet = workBook.createSheet(p2.getName());
			
			p1.toSheet(p1Sheet);
			p2.toSheet(p2Sheet);
		}
	}
	private static void writeSummarySheet(Sheet summarySheet) {
		int pairNum = 0;
		int rowNum = 0;
		for (ParticipantPair pair = database.getPair(0); pair!=null; pair=database.getPair(pairNum+=1)) {
			Participant p1 = pair.getParticipant(1);
			Participant p2 = pair.getParticipant(2);
			Row summaryRow = summarySheet.createRow(rowNum++);
			int cell = 0;
			summaryRow.createCell(cell++).setCellValue(pairNum); //pair number
			summaryRow.createCell(cell++).setCellValue(p1.getName()); //p1 name
			summaryRow.createCell(cell++).setCellValue(p2.getName()); //p2 name
			summaryRow.createCell(cell++).setCellValue(p1.getTest1Correct()); // p1 test 1 correct
			summaryRow.createCell(cell++).setCellValue(p1.getTest1Size()); // p1 test 1 total
			summaryRow.createCell(cell++).setCellValue(p2.getTest1Correct()); // p2 test 1 correct
			summaryRow.createCell(cell++).setCellValue(p2.getTest1Size()); // p2 test 1 total
			summaryRow.createCell(cell++).setCellValue(p1.getTest1Correct() + p2.getTest1Correct()); // pair correct
			summaryRow.createCell(cell++).setCellValue(p1.getTest1Size() + p2.getTest1Size()); // pair total
			
			summaryRow.createCell(cell++).setCellValue(""); // empty cell separting test 1 and test 2.
		
			summaryRow.createCell(cell++).setCellValue(p1.getTest2Correct()); // p1 test 1 correct
			summaryRow.createCell(cell++).setCellValue(p1.getTest2Size()); // p1 test 1 total
			summaryRow.createCell(cell++).setCellValue(p2.getTest2Correct()); // p2 test 2 correct
			summaryRow.createCell(cell++).setCellValue(p2.getTest2Size()); // p2, test 2 total
			summaryRow.createCell(cell++).setCellValue(p1.getTest2Correct() + p2.getTest2Correct()); //Pair Correct
			summaryRow.createCell(cell++).setCellValue(p1.getTest2Size() + p2.getTest2Size()); //Pair total
		}
		
	}
	private static void writePicSheet(Sheet picSheet) {
		int cell = 0;
		int rowNum = 1;
		for (ArrayList<Picture> picSet : picDatabase.getPictures()) {
			Row picRow = picSheet.createRow(rowNum++);
			picRow.createCell(cell++).setCellValue("Pic Num");
			picRow.createCell(cell++).setCellValue("Name");
			picRow.createCell(cell++).setCellValue("Response");
			picRow.createCell(cell++).setCellValue("Phase");
			picRow.createCell(cell++).setCellValue("Row Num");
			picRow.createCell(cell++).setCellValue("Num Words");
			picRow.createCell(cell++).setCellValue("Num Lines");
			picRow.createCell(cell++).setCellValue("RT");
			picRow.createCell(cell++).setCellValue("Attempts");
			picRow.createCell(cell++).setCellValue("Correct");
			picRow.createCell(cell++).setCellValue("Full Text");
			cell = 0;
			for (Picture pic : picSet) {
				picRow = picSheet.createRow(rowNum++);
				picRow.createCell(cell++).setCellValue(pic.getPicNum());
				picRow.createCell(cell++).setCellValue(pic.getName());
				picRow.createCell(cell++).setCellValue(pic.getResponse());
				picRow.createCell(cell++).setCellValue(pic.getPhase());
				picRow.createCell(cell++).setCellValue(pic.getRowNum());
				picRow.createCell(cell++).setCellValue(pic.getnWords());
				picRow.createCell(cell++).setCellValue(pic.getnLines());
				picRow.createCell(cell++).setCellValue(pic.getRT());
				picRow.createCell(cell++).setCellValue(pic.getAttempts());
				picRow.createCell(cell++).setCellValue(pic.getCorrect());
				picRow.createCell(cell++).setCellValue(pic.getFullText());
				cell = 0;
			}
			rowNum+=2;
			
		}

		
	}
	private static void update(Picture picData, int rowNum) {
			
		for (Row row = sheet.getRow(rowNum); ; row = sheet.getRow(rowNum++)) {
			
			if (row.getCell(textColumn)!=null) {
		
				if (row.getCell(textColumn).toString().contains(endTest1)) {
					database.getParticipant(picData.getName()).updateTest1(picData);
					picDatabase.addPic(picData);
					break;
				} else if (row.getCell(textColumn).toString().contains(endTest2)) {
					database.getParticipant(picData.getName()).updateTest2(picData);
					picDatabase.addPic(picData);
					break;
				}
			}
		}

	}
	private static boolean isDigit(String text) {
		String s = text;
		for(char c : s.toCharArray()) {
		    if(Character.isDigit(c)) {
		        return true;
		    }
		}
		return false;
	}
	private static boolean noLetter(String text) {

		String s = text;
		for(char c : s.toCharArray()) {
		    if(Character.isLetter(c)) {
		        return false;
		    }
		}
		return true;
	}
	private static test1Data isCorrect(int answer, int rowNum, String name) {
		int response = - 1;
		test1Data t1Data = new test1Data(-1, false, rowNum, -1, -1);
		int rt_start = (int) Double.parseDouble(sheet.getRow(rowNum).getCell(rtColumn).toString());
		
		for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
			Row row = rit.next();
			if (row.getRowNum()>rowNum) {
				if (row.getCell(namesColumn)!=null) {
					if (row.getCell(namesColumn).toString().equals(name)) {
						response = parseResponse(row, -1);
						if (response!=-1) {
							int rt_end = (int) Double.parseDouble(row.getCell(rtColumn).toString());
							t1Data.rt = rt_end - rt_start;
							t1Data.rsp = response;
							t1Data.answer = answer;
							if (response==answer) {
								t1Data.correct = true;
								return t1Data;
							} else {
								t1Data.correct = false;
								return t1Data;
							}
						}
					}
				}
				if (row.getCell(textColumn)!=null) {
					String nameRow = row.getCell(textColumn).toString();
					String textRow = row.getCell(textColumn).toString();
					
					if (nameRow.contains(name) && textRow.contains("jpg..having to select") || textRow.contains(endTest1) || textRow.contains(endTest2)) {
						return t1Data;
					}
				}
			}
		}
		return t1Data;
	}
	public static void removeTrials(int endIndex, int testNum) throws Exception {
		Participant p;
		for (Iterator<Participant> pit = database.SsDatabase.values().iterator(); pit.hasNext(); ) {
			p = pit.next();
			p.removePic(endIndex, testNum);
		}
	}
	public static int parseResponse(Row row, int rsp) {
		String cell;
		if (row.getCell(textColumn)!=null) {
			cell = row.getCell(textColumn).toString();
			if ( (noLetter(cell) && isDigit(cell)) || (cell.startsWith("/") && isDigit(cell))) { 
				if (cell.contains(".")) { 
					cell = cell.substring(0, cell.length()-2); 
				}
				if (cell.length()>1) {
					cell = cell.replaceAll("[^0-9]", "");
				}
				rsp = Integer.parseInt(cell);
				if (rsp==1) {
					return parseResponse(sheet.getRow(row.getRowNum()+1), 1);
				} else {
					return rsp;
				}
			}
		}
		return rsp;
	}
}

