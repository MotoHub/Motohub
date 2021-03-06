package online.motohub.model;

import android.databinding.BaseObservable;

public class User extends BaseObservable {

    private String firstName;
    private String lastName;
    private int age;

    public User(String fName, String lName, int ag) {
        firstName = fName;
        lastName = lName;
        age = ag;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }
}
