package domain;

public class User {
    private String user_id;
    private String user_name;
    private String user_city;
    private String user_hobby;

    public User() {}

    public User(String id, String first, String user_city, String user_hobby) {
        this.setUser_id(id);
        this.setUser_name(first);
        this.setUser_city(user_city);
        this.setUser_hobby(user_hobby);
    }

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_city() {
		return user_city;
	}

	public void setUser_city(String user_city) {
		this.user_city = user_city;
	}

	public String getUser_hobby() {
		return user_hobby;
	}

	public void setUser_hobby(String user_hobby) {
		this.user_hobby = user_hobby;
	}

   
}
