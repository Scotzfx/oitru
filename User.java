package Main;

public class User {
    private int id;
    private String course;
    private String studentName;
    private String username;
    private String email;
    private String role;
    
    public User(int id, String course, String studentName, String username, String email, String role) {
        this.id = id;
        this.course = course;
        this.studentName = studentName;
        this.username = username;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public String getCourse() { return course; }
    public String getStudentName() { return studentName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    
    public void setCourse(String course) { this.course = course; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role){
        this.role = role;
    }
}