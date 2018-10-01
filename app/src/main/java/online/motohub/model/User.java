package online.motohub.model;

import android.databinding.BaseObservable;

public class User extends BaseObservable {

    public User(String fName, String lName, int ag) {
        firstName = fName;
        lastName = lName;
        age = ag;
    }

    private String firstName;
    private String lastName;
    private int age;

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
