package apolo.api;

import apolo.entity.IArtist;
import apolo.entity.IRelease;
import apolo.entity.ISong;

public interface IDBpedia {

	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	/**
	 * Get AdditionalInformation about a song
	 * @param r, song to search in DBPedia
	 * @return
	 */
	public ISong getAdditionalInformationSong(ISong s);
	
	/**
	 * Get AdditionalInformation about a release
	 * @param r, release to search in DBPedia
	 * @return
	 */
	public IRelease getAdditionalInformationRelease(IRelease r);
	
	/**
	 * Get AdditionalInformation about an artist
	 * @param a, artist to search in DBPedia
	 * @return
	 */
	public IArtist getAdditionalInformationArtist(IArtist a);
	
}
