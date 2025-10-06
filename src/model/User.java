package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullname;

    public User(String username, String password, String email, String fullname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
    }

    public User(int id, String username, String password, String email, String fullname) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFullname() { return fullname; }
}
