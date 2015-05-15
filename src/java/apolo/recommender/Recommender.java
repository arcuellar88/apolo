package apolo.recommender;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.jdbc.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import apolo.db.DWConnector;
import apolo.entity.*;
import apolo.msc.*;

public class Recommender implements IRecommender {
	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------
	// Improve performance
	// https://builds.apache.org/job/Mahout-Quality/javadoc/org/apache/mahout/cf/taste/impl/model/jdbc/MySQLJDBCDataModel.html
	// http://stackoverflow.com/questions/5696857/how-to-change-value-for-innodb-buffer-pool-size-in-mysql-on-mac-os

	private ReloadFromJDBCDataModel dataModel;
	private ItemSimilarity simCache;

	// --------------------------------------------------------
	// Constructor
	// --------------------------------------------------------

	/**
	 * Constructor
	 */
	public Recommender() {
		dataModel = null;
		initialize(Global_Configuration.MYSQL);
	}

	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	/**
	 * Initialize the data model
	 * @param database
	 */
	private void initialize(String database) {
		try {
			// MYSQL
			if (database.equals(Global_Configuration.MYSQL)) {
				
				MysqlDataSource dataSource = new MysqlDataSource();
				dataSource.setServerName(Global_Configuration.MYSQL_HOST);
				dataSource.setUser(Global_Configuration.MYSQL_USER);
				dataSource.setPassword(Global_Configuration.MYSQL_PWD);
				dataSource.setDatabaseName(Global_Configuration.MYSQL_DB);

				// Performance settings:
				dataSource.setCachePreparedStatements(true);
				dataSource.setCacheCallableStmts(true);
				dataSource.setCacheResultSetMetadata(true);
				dataSource.setAlwaysSendSetIsolation(false);
				dataSource.setElideSetAutoCommits(true);

				// Initialize data model
				dataModel = new ReloadFromJDBCDataModel(new MySQLJDBCDataModel(
						dataSource, "taste_preferences", "user_id", "item_id",
						"preference", ""));
				
			} else {
				// ORACLE
				OracleDataSource dataSource = new OracleDataSource();
				dataSource.setServerName(Global_Configuration.ORACLE_HOST);
				dataSource.setUser(Global_Configuration.ORACLE_USER);
				dataSource.setPassword(Global_Configuration.ORACLE_PWD);
				dataSource.setDatabaseName(Global_Configuration.ORACLE_DB);
				dataSource.setPortNumber(1521);
				dataSource.setDriverType("thin");

				// Initialize data model
				dataModel = new ReloadFromJDBCDataModel(new SQL92JDBCDataModel(
						dataSource, "APOLO_MASTER.FACTARTISTRATING",
						"FK_DIMUSER", "FK_DIMARTIST", "RATING", ""));
			}

			//Log.println("Datamodel initialized");

		} catch (TasteException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Ranking getRecommendation(IArtist a, int numRecommendations) {
		//initialize(Global_Configuration.MYSQL);
		Ranking r = getRecommendationNoPreCompute(a.getArtist_id(),
				numRecommendations);

		return r;
	}

	/**
	 * Recommendation with out pre-computing the similarity matrix
	 * @param artistID Id of the artist to search for recommendations for
	 * @param numRecommendations Number of recommendations for the requested artist
	 * @return
	 */
	public Ranking getRecommendationNoPreCompute(int artistID,
			int numRecommendations) {
		
		// Initialize empty recommendation Ranking
		Ranking ranking = new Ranking();

		try {

			// Initialize similarity matrix
			initializeItemSimilarity();

			// Create Recommender
			GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(
					dataModel, simCache);
			//Log.println("Recommender created");

			// Get recommended artists
			List<RecommendedItem> recommendations = recommender
					.mostSimilarItems(artistID, numRecommendations);
			
			System.out.println("MOST SIMILAR: " + recommendations.size());
			
			//Log.println("Initialize connection to DWH");
			DWConnector dw = new DWConnector();

			for (RecommendedItem recommendedItem : recommendations) {
				IRankingItem ri = dw.getArtist((int) recommendedItem
						.getItemID());
				if (ri != null) {
					ri.setItemType(RankingItem.TYPE_ARTIST);
					ri.setSimilarity(recommendedItem.getValue());
					ri.setItemId(recommendedItem.getItemID());
					ri.setItemName(((Artist) ri).getName());
					ranking.addRankingItem(ri);
					/*
					Log.println("Recommended Artists: "
							+ ((IArtist) ri).getName() + " - "
							+ ri.getSimilarity());
					*/

				} else
					Log.println("Artist not found: "
							+ recommendedItem.getItemID() + " - "
							+ recommendedItem.getValue());
			}
			//Log.println("Finish getRecommendationNoPreCompute" + new Date());

			dw.close();
		} catch (NoSuchItemException e) {
			Log.println("NoSuchItemException" + " Artist ID: " + artistID);
		} catch (TasteException e1) {
			e1.printStackTrace();
		}

		return ranking;
	}

	/**
	 * Pre computes the similarity for numArtist
	 * @param numArtist
	 * @param numSimilar
	 */
	public void preComputeSimilarity(int numArtist, int numSimilar,
			PrintWriter pw) {
		try {

			//---------DRAFT!-------------
			
			initialize(Global_Configuration.MYSQL);

			// Similarity Matrix
			initializeItemSimilarity();

			// Create ItemBasedRecommender
			GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(
					dataModel, simCache);

			Log.println("ItemBasedRecommender created");

			int i = 0;
			for (LongPrimitiveIterator items = dataModel.getItemIDs(); items
					.hasNext() && i < numArtist;) {
				long itemId = items.nextLong();
				List<RecommendedItem> recommendations = recommender
						.mostSimilarItems(itemId, numSimilar);
				
				for (RecommendedItem recommendedItem : recommendations) {
					Log.println(itemId + "," + recommendedItem.getItemID()
							+ "," + recommendedItem.getValue());
					pw.println(itemId + "," + recommendedItem.getItemID() + ","
							+ recommendedItem.getValue());
				}
				i++;
			}
			Log.println("preComputeSimilarity finish" + new Date());

		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void validator()
	{
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderBuilder builder = new RecommenderBuilder() {

			@Override
			public org.apache.mahout.cf.taste.recommender.Recommender buildRecommender(
					DataModel datam) throws TasteException {
				initializeItemSimilarity();
				GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(
						datam, simCache);
				return null;
			}
			};	
		try {
			double evaluation = evaluator.evaluate(builder, null,dataModel, 0.9, 1.0);
		Log.println("validator: "+evaluation);
		} 
		catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * initializeItemSimilarity
	 * 
	 * @throws TasteException
	 */
	private void initializeItemSimilarity() throws TasteException {
		if (dataModel != null) {
			ItemSimilarity simLog = new LogLikelihoodSimilarity(dataModel);
			simCache = new CachingItemSimilarity(simLog, dataModel);
			
			// new CachingItemSimilarity(new EuclideanDistanceSimilarity(model),
			// model);
			Log.println("Similarity matrix loaded");

		} else
			Log.println("dataModel is null");

	}

	/**
	 * Main method to TEST the recommender
	 * @param args
	 */
	public static void main(String[] args) {
		Log.println("START");

		Recommender rd = new Recommender();

		IArtist a = new Artist();

		a.setArtist_id(4425485);
		Ranking r = rd.getRecommendation(a, 5);
		System.out.println("\n" + r.getItems().size());
		a.setArtist_id(4425485);
		rd.getRecommendation(a, 5);
		
		
		//rd.validator();
	}

	/*
	 * //
	 * List<RecommendedItem>recommendations=recommender.mostSimilarItems(1019857
	 * , 3); for (RecommendedItem recommendedItem : recommendations) {
	 * Log.println(1019857+" - "+recommendedItem.getItemID()+" - " +
	 * recommendedItem.getValue()); } System.out.println("Checkpoing 3"+ new
	 * Date());
	 * 
	 * recommendations=recommender.mostSimilarItems(1019857, 3); for
	 * (RecommendedItem recommendedItem : recommendations) {
	 * Log.println(1019857+" - "+recommendedItem.getItemID()+" - " +
	 * recommendedItem.getValue()); } System.out.println("Checkpoing 3"+ new
	 * Date()); }
	 * 
	 * catch(TasteException e) { e.printStackTrace(); }
	 * 
	 * /* Example result --Sun Apr 26 23:15:49 CEST 2015: 104 - 1045044 -
	 * 0.9507258 --Sun Apr 26 23:15:49 CEST 2015: 104 - 1008867 - 0.94533014
	 * --Sun Apr 26 23:15:49 CEST 2015: 104 - 1030994 - 0.9426782 --Sun Apr 26
	 * 23:15:49 CEST 2015: 109 - 1033910 - 0.90794903 --Sun Apr 26 23:15:49 CEST
	 * 2015: 109 - 1003697 - 0.884588 --Sun Apr 26 23:15:49 CEST 2015: 109 -
	 * 1075452 - 0.8776636 --Sun Apr 26 23:15:49 CEST 2015: 24538 - 1012319 -
	 * 0.999526 --Sun Apr 26 23:15:49 CEST 2015: 24538 - 1006748 - 0.99938905
	 * --Sun Apr 26 23:15:49 CEST 2015: 24538 - 1010279 - 0.99934304 --Sun Apr
	 * 26 23:15:49 CEST 2015: 1000004 - 1013596 - 0.9990727 --Sun Apr 26
	 * 23:15:49 CEST 2015: 1000004 - 1017764 - 0.99906135 --Sun Apr 26 23:15:49
	 * CEST 2015: 1000004 - 1001862 - 0.9990454
	 */

}
