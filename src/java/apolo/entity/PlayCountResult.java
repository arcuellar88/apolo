package apolo.entity;

public class PlayCountResult {
	
	private int nr_users;
	private int nr_plays;
	private int song_id;
	
	public int getNr_users() {
		return nr_users;
	}
	public void setNr_users(int nr_users) {
		this.nr_users = nr_users;
	}
	public int getSong_id() {
		return song_id;
	}
	public void setSong_id(int song_id) {
		this.song_id = song_id;
	}
	public int getNr_plays() {
		return nr_plays;
	}
	public void setNr_plays(int nr_plays) {
		this.nr_plays = nr_plays;
	}
	
	public String toString()
	{
		return "SongID:"+song_id+" Nr Users:"+nr_users+" Nr Plays:"+nr_plays;
	}
	
}
