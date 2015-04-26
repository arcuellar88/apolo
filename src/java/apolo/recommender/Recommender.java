package apolo.recommender;

import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import apolo.msc.Global_Configuration;
import apolo.msc.Log;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Recommender {
	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------
		//Improve performance
		//https://builds.apache.org/job/Mahout-Quality/javadoc/org/apache/mahout/cf/taste/impl/model/jdbc/MySQLJDBCDataModel.html
		
		//http://stackoverflow.com/questions/5696857/how-to-change-value-for-innodb-buffer-pool-size-in-mysql-on-mac-os
	// --------------------------------------------------------
	// Constructor
	// --------------------------------------------------------

	/**
	 * Constructor
	 */
	public Recommender()
	{
		//Empty
	}
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	public void initialize()
	{
		try
		{
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName(Global_Configuration.MYSQL_HOST);
		dataSource.setUser(Global_Configuration.MYSQL_USER);
		dataSource.setPassword(Global_Configuration.MYSQL_PWD);
		dataSource.setDatabaseName(Global_Configuration.MYSQL_DB);

		JDBCDataModel dataModel = new MySQLJDBCDataModel(
		    dataSource, "taste_preferences", "user_id",
		    "item_id", "preference","");
	
		ItemSimilarity sim = new LogLikelihoodSimilarity(dataModel);
		GenericItemBasedRecommender recommender= new GenericItemBasedRecommender(dataModel, sim);
		
		/*
		int i=0;
		for(LongPrimitiveIterator items=dataModel.getItemIDs();items.hasNext();)
		{
			long itemId=items.nextLong();
			List<RecommendedItem>recommendations=recommender.mostSimilarItems(itemId, 3);
			
			for (RecommendedItem recommendedItem : recommendations) {
				Log.println(itemId+" - "+recommendedItem.getItemID()+" - " + recommendedItem.getValue());
			}
			i++;
			if(i>10)
				System.exit(1);
		}
		*/
		
		//
		List<RecommendedItem>recommendations=recommender.mostSimilarItems(1019857, 3);
		for (RecommendedItem recommendedItem : recommendations) {
			Log.println(1019857+" - "+recommendedItem.getItemID()+" - " + recommendedItem.getValue());
		}
		}
		catch(TasteException e)
		{
			e.printStackTrace();
		}
	
		/*
		 * Example result
		 * --Sun Apr 26 23:15:49 CEST 2015: 104 - 1045044 - 0.9507258
			--Sun Apr 26 23:15:49 CEST 2015: 104 - 1008867 - 0.94533014
			--Sun Apr 26 23:15:49 CEST 2015: 104 - 1030994 - 0.9426782
			--Sun Apr 26 23:15:49 CEST 2015: 109 - 1033910 - 0.90794903
			--Sun Apr 26 23:15:49 CEST 2015: 109 - 1003697 - 0.884588
			--Sun Apr 26 23:15:49 CEST 2015: 109 - 1075452 - 0.8776636
			--Sun Apr 26 23:15:49 CEST 2015: 24538 - 1012319 - 0.999526
			--Sun Apr 26 23:15:49 CEST 2015: 24538 - 1006748 - 0.99938905
			--Sun Apr 26 23:15:49 CEST 2015: 24538 - 1010279 - 0.99934304
			--Sun Apr 26 23:15:49 CEST 2015: 1000004 - 1013596 - 0.9990727
			--Sun Apr 26 23:15:49 CEST 2015: 1000004 - 1017764 - 0.99906135
			--Sun Apr 26 23:15:49 CEST 2015: 1000004 - 1001862 - 0.9990454
			--Sun Apr 26 23:15:49 CEST 2015: 1000006 - 1003349 - 0.999333
			--Sun Apr 26 23:15:49 CEST 2015: 1000006 - 1013691 - 0.9992562
			--Sun Apr 26 23:15:49 CEST 2015: 1000006 - 1004568 - 0.9992322
			--Sun Apr 26 23:15:49 CEST 2015: 1000009 - 1005638 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000009 - 1040313 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000009 - 1017194 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000010 - 1016450 - 0.9893811
			--Sun Apr 26 23:15:49 CEST 2015: 1000010 - 1015386 - 0.9887255
			--Sun Apr 26 23:15:49 CEST 2015: 1000010 - 1045714 - 0.9876508
			--Sun Apr 26 23:15:49 CEST 2015: 1000012 - 1017819 - 0.99965316
			--Sun Apr 26 23:15:49 CEST 2015: 1000012 - 1000905 - 0.99960023
			--Sun Apr 26 23:15:49 CEST 2015: 1000012 - 1006016 - 0.9995976
			--Sun Apr 26 23:15:49 CEST 2015: 1000015 - 1029847 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000015 - 1015337 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000015 - 1029843 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000016 - 1033411 - 0.9879728
			--Sun Apr 26 23:15:49 CEST 2015: 1000016 - 1005442 - 0.9872213
			--Sun Apr 26 23:15:49 CEST 2015: 1000016 - 1005880 - 0.98714364
			--Sun Apr 26 23:15:49 CEST 2015: 1000017 - 1022291 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000017 - 1032200 - 0.9596665
			--Sun Apr 26 23:15:49 CEST 2015: 1000017 - 1023701 - 0.9596665
		 */
	}
	
	
	
	public static void main(String[] args) {
		 Recommender rd=new Recommender();
		 rd.initialize();
		 
	}

}
