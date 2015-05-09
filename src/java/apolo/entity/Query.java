package apolo.entity;

import java.util.*;
import apolo.queryrefinement.*;

public class Query implements IQuery{

	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------

	
	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------

	private String originalQuery; /** Original query entered by the user */
	private ArrayList<Annotation> annotations; /** List of entities recognized. If empty, then no entities were recognized */
	
	// --------------------------------------------------------
	// Constructor
	// --------------------------------------------------------

	


	/**
	 * Constructor
	 * @param originalQuery
	 */
	public Query(String originalQuery) {
		super();
		this.originalQuery = originalQuery;
	}


	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------

	
	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}


	@Override
	public String getOriginalQuery() {
		return originalQuery;
	}
	
	public void annotateQuery(){
		NER ner = new NER(this.originalQuery);
		this.annotations = ner.annotateQuery();
	}
	
	@Override
	public ArrayList<Annotation> getAnnotations() {
		return annotations;
	}
}
