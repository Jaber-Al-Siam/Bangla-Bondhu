package com.example.bangla_bondhu;

public class UserModel {
    private String name;
    private String email;
    private String password;

    public UserModel(){
        name = "";
        email = "";
        password = "";
    }

    public UserModel(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "Name: " + name + '\'' +
                "Email: " + email + '\'' +
                '}';
    }
}
