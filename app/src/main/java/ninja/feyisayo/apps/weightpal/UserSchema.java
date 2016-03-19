package ninja.feyisayo.apps.weightpal;

public class UserSchema {

    private String name;
    private String email;
    private String password;


    // Empty constructor for Firebase.
    public UserSchema(){}

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
