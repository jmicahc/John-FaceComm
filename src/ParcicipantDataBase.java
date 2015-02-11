import java.util.HashMap;
import java.util.Iterator;


public class ParcicipantDataBase {
	
	
	public HashMap<String, Participant> SsDatabase; //I want to definite an iterator for this that can be accessed from different classes
	private HashMap<Integer, ParticipantPair> PairsDatabase; //And this too, but less important.
	
	public ParcicipantDataBase() {
		SsDatabase = new HashMap<String, Participant>();
		PairsDatabase = new HashMap<Integer, ParticipantPair>();
	}
	public void addParticipant(String name, Participant participant) {
		SsDatabase.put(name, participant);
	}
	public Participant getParticipant(String name) {
		try {
			return SsDatabase.get(name);
		} catch (NullPointerException e) {
			System.out.println("No participant named: " +  name);
			return null;
		}
	}	
	public void addPair(int num, ParticipantPair pair) {
		Integer number = num;
		PairsDatabase.put(number, pair);
	}
	public ParticipantPair getPair(int num) {
		Integer number = num;
		if (num>=0) {
			return PairsDatabase.get(number);
		} else
			return null;
	}
	public ParticipantPair getPair(String name) {
		Integer pairNum = null;
		for (Iterator<Integer> kit = PairsDatabase.keySet().iterator(); kit.hasNext(); )
			pairNum = kit.next();
			if (PairsDatabase.get(pairNum).getUniqueID().contains(name)) {
				return PairsDatabase.get(pairNum);
			}
		return null;
	}
	public boolean alreadyExists(String name) {
		if (SsDatabase.get(name)==null) {
			return false;
		} else
			return true;
	}
	public void removeParticipant(String name) {
		SsDatabase.remove(name);
	}
	public int size() {
		return SsDatabase.size();
	}

}
