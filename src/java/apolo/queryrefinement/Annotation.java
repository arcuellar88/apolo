package apolo.queryrefinement;

public class Annotation {
	
	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------
	private String entity; /** Value of the entity that was recognized */
	private String entityType; /** Type of entity that was recognized */
	private int posStart; /** Starting position of the entity within the original query */
	private int posFinal; /** Final position of the entity within the original query */
	private double editDistance; /** Edit distance to the matched entity (in case of misspelling) */
	private int size; /** Number of characters on the entity value */
	
	public Annotation(String entityValue, String entityType, int posStart, int posFinal, double distance, int size){
		this.entity=entityValue;
		this.entityType=entityType;
		this.posStart=posStart;
		this.posFinal=posFinal;
		this.editDistance=distance;
		this.size=size;
	}
	
	
	public String getEntityValue(){
		return this.entity;
	}
	
	public void setEntityValue(String value){
		this.entity=value;
	}
	
	public String getEntityType(){
		return this.entityType;
	}
	
	public void setEntityType(String type){
		this.entityType=type;
	}
	
	public int getEntityPosStart(){
		return this.posStart;
	}
	
	public void setEntityPosStart(int pos){
		this.posStart=pos;
	}
	
	public int getEntityPosFinal(){
		return this.posFinal;
	}
	
	public void setEntityPosFinal(int pos){
		this.posFinal=pos;
	}
	
	public double getEntityEditDistance(){
		return this.editDistance;
	}
	
	public void setEditDistance(double distance){
		this.editDistance=distance;
	}
	
	public int getEntitySize(){
		return this.size;
	}
	
	public void setEntitySize(int size){
		this.size=size;
	}
	
}
