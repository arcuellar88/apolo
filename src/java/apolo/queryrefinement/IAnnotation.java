package apolo.queryrefinement;

public interface IAnnotation {
	
	/**
	 * Get named entity value
	 */
	public String getEntityValue();
	
	/**
	 * Set named entity value
	 * @param entity value
	 */
	public void setEntityValue(String value);
	
	/**
	 * Get named entity type (Song, Artist, Release, ...)
	 */
	public String getEntityType();
	
	/**
	 * Set named entity type (Song, Artist, Release, ...)
	 * @param entity type
	 */
	public void setEntityType(String type);
	
	/**
	 * Get named entity initial position
	 */
	public int getEntityPosStart();
	
	/**
	 * Set named entity initial position
	 * @param entity initial position
	 */
	public void setEntityPosStart(int pos);
	
	/**
	 * Get named entity final position
	 */
	public int getEntityPosFinal();
	
	/**
	 * Set named entity final position
	 * @param entity final position
	 */
	public void setEntityPosFinal(int pos);
	
	/**
	 * Get named entity edit distance
	 * @param entity final position
	 */
	public int getEntityEditDistance();
	
	/**
	 * Set named entity edit distance
	 * @param entity final position
	 */
	public void setEditDistance(int distance);
	
	/**
	 * Get named entity size
	 * @param entity final position
	 */
	public int getEntitySize();
	
	/**
	 * Set named entity size
	 * @param entity final position
	 */
	public void setEntitySize(int size);

}
