package models;

public class User {
    private int id;
    private String name;
    private String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Setters (optional)
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
