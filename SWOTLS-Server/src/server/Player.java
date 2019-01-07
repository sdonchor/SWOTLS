package server;

public class Player extends Competitor {
	private String name;
	private String surname;
	private String nickname;
	private int score;
	private String language;
	private String contact_info;
	private String additional_info;
	private int team_id;
	
	public Player(String name, String surname, String nickname, int score, String language, String contact_info, String additional_info, int team_id)
	{
		this.setName(name);
		this.setSurname(surname);
		this.setNickname(nickname);
		this.setScore(score);
		this.setLanguage(language);
		this.setContact_info(contact_info);
		this.setAdditional_info(additional_info);
		this.setTeam_id(team_id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getContact_info() {
		return contact_info;
	}

	public void setContact_info(String contact_info) {
		this.contact_info = contact_info;
	}

	public String getAdditional_info() {
		return additional_info;
	}

	public void setAdditional_info(String additional_info) {
		this.additional_info = additional_info;
	}

	public int getTeam_id() {
		return team_id;
	}

	public void setTeam_id(int team_id) {
		this.team_id = team_id;
	}
}
