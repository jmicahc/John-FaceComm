import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class PicturesDataBase {
	
	private HashMap<String, ArrayList<Picture>> pictures;
	private ArrayList<String> picNums;
	
	public PicturesDataBase() {
		pictures = new HashMap<String, ArrayList<Picture>>();
		picNums = new ArrayList<String>();
	}
	public ArrayList<Picture> getPicture(String picNum) {
		return pictures.get(picNum);
	}
	public ArrayList<Picture> getPicure(Integer num) {
		return pictures.get(num.toString());
	}
	public void addPic(Picture pic) {
		if (pictures.containsKey(pic.getPicNum())) {
			pictures.get(pic.getPicNum()).add(pic);
		} else {
			ArrayList<Picture> temp = new ArrayList<Picture>();
			temp.add(pic);
			pictures.put(pic.getPicNum(), temp);
			picNums.add(pic.getPicNum());
		}
	}
	public Collection<ArrayList<Picture>> getPictures() {
		return pictures.values();
	}
}
