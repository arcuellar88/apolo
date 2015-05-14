package apolo.querybuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArtistReleaseDataPreparation {
	/**
	 * For song extraction
	 */

	public final String artistFile = "data/artists.csv";
	public final List<HashMap<String, String>> artistList = new ArrayList<HashMap<String, String>>();
	public final List<HashMap<String, String>> releaseList = new ArrayList<HashMap<String, String>>();
	public final List<HashMap<String, String>> releaseSongList = new ArrayList<HashMap<String, String>>();

	public final static String INDEX_SEPARATOR1 = "\\[\\|\\]";
	public final static String INDEX_SEPARATOR2 = "\\{";

	public void readArtist() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(artistFile));
		String line;

		while ((line = br.readLine()) != null) {

			HashMap<String, String> artistMap = new HashMap<String, String>();

			if (line != null) {

				String[] txt = line.split(INDEX_SEPARATOR1, -1);
				for(int i = 0; i < txt.length; i++) {
					txt[i] = txt[i].replaceAll("\\u00a0", " ").trim();
				}

				artistMap.put("artistID", txt[0]);
				artistMap.put("artistName", txt[1]);
				artistMap.put("artistGender", txt[2]);
				artistMap.put("artistType", txt[3]);
				artistMap.put("artistCountry", txt[4]);
				artistMap.put("artistContinent", txt[5]);
				
				if (txt.length >= 7 && (!txt[6].equals("")) && (!txt[7].equals("")) &&
					 (!txt[8].equals("")) && (!txt[9].equals("")) && (!txt[10].equals(""))) {
					artistMap.put("artistRatingAVG", txt[6].trim());
					artistMap.put("artistRatingCount", txt[7].trim());
					artistMap.put("artistP", txt[8].trim());
					artistMap.put("artistNU", txt[9].trim());
					artistMap.put("artistN", txt[10].trim());

				} else {
					artistMap.put("artistRatingAVG", "");
					artistMap.put("artistRatingCount", "");
					artistMap.put("artistP", "");
					artistMap.put("artistNU", "");
					artistMap.put("artistN", "");
				}
				
				if (txt.length >= 12 && txt[11] != "")
					artistMap.put("artistMBID", txt[11].trim());
				else
					artistMap.put("artistMBID", "");

				artistList.add(artistMap);
			}
		}
		br.close();
	}

	public void readRelease() throws IOException {

		String releaseFile = "data/releases.csv";
		BufferedReader br = new BufferedReader(new FileReader(releaseFile));
		String line;

		while ((line = br.readLine()) != null) {

			HashMap<String, String> releaseMap = new HashMap<String, String>();

			if (line != null) {
				String[] txt = line.split(INDEX_SEPARATOR2);
				for(int i = 0 ; i < txt.length; i++) {
					if (txt[i].equals("\\N")) {
						txt[i] = "";
					}
					txt[i] = txt[i].replaceAll("\\u00a0", " ").trim();
				}

				releaseMap.put("releaseID", txt[0]);
				releaseMap.put("releaseName", txt[1]);
				releaseMap.put("releaseType", txt[2]);
				releaseMap.put("releaseMBID", txt[3]);

				if (txt[5].equals("0")) {
					txt[5] = "";
				}
				
				if (txt[7].equals("0")) {
					txt[7] = "";
				}
				
				releaseMap.put("releaseArtist", txt[4]);
	            releaseMap.put("releaseArtistID", txt[5]);
	            releaseMap.put("releaseSong", txt[6]);
	            releaseMap.put("releaseSongID", txt[7]);

				releaseList.add(releaseMap);

			}

		}
		br.close();
	}
}