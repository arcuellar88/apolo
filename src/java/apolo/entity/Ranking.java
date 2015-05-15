package apolo.entity;

import java.util.ArrayList;

public class Ranking implements IRanking {
	
	/**
	 * ArraList of items
	 */
	private ArrayList<IRankingItem> items;

	
	
	public Ranking()
	{
		items= new ArrayList<IRankingItem>();
	}
	
	
	public void setItems(ArrayList<IRankingItem> items) {
		this.items = items;
	}
	
	public ArrayList<IRankingItem> getItems() {
		return items;
	}

	@Override
	public void addRankingItem(IRankingItem item) {
		items.add(item);		
	}

	@Override
	public IRankingItem getRankingItem(int pos) {
		IRankingItem ritem=null;
				
	if (pos<items.size())
		ritem=items.get(pos);
		
		return ritem;
	}
	
	
}
