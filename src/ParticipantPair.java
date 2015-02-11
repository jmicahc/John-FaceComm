import java.util.HashSet;


public class ParticipantPair {
	
	private Participant participant1;
	private Participant participant2;
	private HashSet<Participant> pair = new HashSet<Participant>();
	private String uniqueID;
	
		
	public ParticipantPair(String uID, Participant prevSj, Participant sj) {
		uniqueID = uID;
		participant1 = prevSj;
		participant2 = sj;
		
	}
	public Participant getParticipant(int num) {
		if (num==1) {
			return participant1;
		} else if (num==2) {
			return participant2;
		} else 
			return null;
		
	}
	public void setParticipant(Participant participant, int oneOrTwo) {
		if (oneOrTwo==1) {
			participant1 = participant;
		} else if (oneOrTwo==2) {
			participant2 = participant;
		}
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public Participant getOther(String name) {
		return FaceCommMain.database.getParticipant(uniqueID.replace(name, ""));
	}
	public void removePariticipant(String name) {
		if (participant1.getName()==name) {
			participant1=null;
		} else {
			participant2=null;
		}
		
	}
}
