import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


public class Participant {
	
	private ArrayList<Picture> test1Data;
	private ArrayList<Picture> collabData;
	private ArrayList<Picture> test2Data;
	private int numCollabTrials = 0;
	
	public Participant() {
		test1Data = new ArrayList<Picture>();
		collabData = new ArrayList<Picture>();
		test2Data = new ArrayList<Picture>();
	}
	public String getName() {
		return test1Data.get(0).getName();
	}
	public void updateCollab(Picture picData) {
		try {
			this.collabData.add(picData);
			numCollabTrials++;
		} catch (Exception e) {
			System.out.print("Problem! Picdata: ");
		}
	}
	public void updateTest1(Picture test1Data) {
		this.test1Data.add(test1Data);
	}
	public void updateTest2(Picture test2Data) {
		this.test2Data.add(test2Data);
	}
	public Picture getTest1PicData(int num) {
	try {
		return test1Data.get(num);
	} catch (IndexOutOfBoundsException e) {
		System.out.println("In getTest1PicData: pic number " + num + 
				" of participant " +  "has no associated picData");
	}
	return null;
}
	public Picture getTest2PicData(int num) {
		try {
			//System.out.println("getting picture number " + num);
			return test2Data.get(num);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("getTest2PicData: number " + num + " has no associated picData");
		}
		return null;
	}
	public Picture getCollabPicData(int num) {
		if (num < collabData.size()) {
			return collabData.get(num);
		} else 
			return null;
	}
	public int getTest1Correct() {
		int count = 0;
		for (Picture pic : test1Data) {
			if(pic.getCorrect()) {
				count++;
			}
		}
		return count;
	}
	public int getTest1Size() {
		return test1Data.size();
	}
	public int getTest2Correct() {
		int count = 0;
		for (Picture pic : test2Data) {
			if(pic.getCorrect()) {
				count++;
			}
		}
		return count;
	}
	public int getTest2Size() {
		return test2Data.size();
	}
	public int getNumCollabTrials() {
		return numCollabTrials;
	}
	public void removePic(int endIndex, int testNum) throws Exception {
		int i = 0;
		try {
			switch (testNum) {
			case 1: while (i<endIndex) test1Data.remove(i++); break;
			case 2: while (i<endIndex) collabData.remove(i++); break;
			case 3: while (i<endIndex) test1Data.remove(i++); break; 
		    }
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Index of removal trial does not exist!");
			e.printStackTrace();
		}
		
	}
 	public int longest() {
 		if (test1Data.size()> collabData.size() && test1Data.size() > test2Data.size()) {
 			return test1Data.size();
 		} else if (collabData.size() > test2Data.size()) {
 			return collabData.size();
 		} else {
 			return test2Data.size();
 		}
 	}
 	public int getCollabSize() {
 		return collabData.size();
 	}
	public void toSheet(Sheet sheet) {
		int rowNum = 0;
		for (Picture pic : test1Data) {
			pic.toSheet(sheet, new Cell(rowNum++, 0));
		}
		rowNum++;
		for (Picture pic : collabData) {
			pic.toSheet(sheet, new Cell(rowNum++, 0));
		}
		rowNum++;
		for (Picture pic : test2Data) {
			pic.toSheet(sheet, new Cell(rowNum++, 0));
		}
		
	}
}
