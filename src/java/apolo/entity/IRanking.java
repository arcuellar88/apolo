package apolo.entity;

import java.util.ArrayList;

public interface IRanking {

	public ArrayList<IRankingItem> getItems();
	
	public void addRankingItem(IRankingItem item);
	
	public IRankingItem getRankingItem(int pos);
	
}
