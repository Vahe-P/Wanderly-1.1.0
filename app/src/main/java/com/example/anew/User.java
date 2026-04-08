package com.example.anew;

/**
 * Data model representing a registered user.
 * Stored in the Firestore "users" collection with the user's UID as the document ID.
 * Holds basic profile information: name, email, and profile picture URL.
 */
public class User {
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String uid, String email, String firstName, String lastName, String profilePictureUrl) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getFullName() {
        return firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
    }
} 