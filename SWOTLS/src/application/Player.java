package application;

public class Player extends Competitor {
    private int id;
    private String name;
    private String surname;
    private String nickname;
    private int elo;
    private String language;
    private String contactInfo;
    private String additionalInfo;
    private Integer teamId;

    public Player(int id, String name, String surname, String nickname, int elo, String language, String contactInfo, String additionalInfo, Integer teamId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.elo = elo;
        this.language = language;
        this.contactInfo = contactInfo;
        this.additionalInfo = additionalInfo;
        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getNickname() {
        return nickname;
    }

    public int getElo() {
        return elo;
    }

    public String getLanguage() {
        return language;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public Integer getTeamId() {
        return teamId;
    }

    @Override
    public String displayedName() {
        return name + " '" + nickname + "' " + surname;
    }
}
