package com.mfahproj.webapp.models;

public class Member {
    // Types of memberships that exist.
    public enum Memberships {
        NONE, // Daily pass member
        REGULAR, // Reoccuring monthly member
        SEASONAL, // Seasonal
        SENIOR, // Senior Citizen
        EMPLOYEE
    }

    private int memberId = -1;
    private String firstName;
    private String lastName;
    private String membershipType = Memberships.NONE.name();
    private java.sql.Date birthDate;
    private String emailAddress;
    private String password;
    private java.sql.Date lastLogin;

    // Create a blank member.
    public Member() {
    };

    // Used to create a new member.
    // memberId is created by AUTOINCREMENT.
    public Member(String firstName, String lastName, Memberships type,
            java.sql.Date birthDate, String emailAddress, String password) {
        this(-1, firstName, lastName, type.name(), birthDate, emailAddress, password,
                new java.sql.Date(System.currentTimeMillis()));
    }

    public Member(int memberId, String firstName, String lastName, String membershipType,
            java.sql.Date birthDate, String emailAddress, String password,
            java.sql.Date lastLogin) {
        this.setMemberId(memberId);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setMembershipType(membershipType);
        this.setBirthDate(birthDate);
        this.setEmailAddress(emailAddress);
        this.setPassword(password);
        this.setLastLogin(lastLogin);
    }

    // Member Id getter.
    public int getMemberId() {
        return this.memberId;
    }

    // Member Id setter.
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    // First name getter.
    public String getFirstName() {
        return this.firstName;
    }

    // First name setter.
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Last name getter.
    public String getLastName() {
        return this.lastName;
    }

    // Last name setter.
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Membership type getter.
    public String getMembershipType() {
        return this.membershipType;
    }

    // Membership type setter.
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    // Birth date getter.
    public java.sql.Date getBirthDate() {
        return this.birthDate;
    }

    // Birth date setter.
    public void setBirthDate(java.sql.Date birthDate) {
        this.birthDate = birthDate;
    }

    // Email address getter.
    public String getEmailAddress() {
        return this.emailAddress;
    }

    // Email address setter.
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    // Password getter.
    public String getPassword() {
        return this.password;
    }

    // Password setter.
    public void setPassword(String password) {
        this.password = password;
    }

    // Last login getter.
    public java.sql.Date getLastLogin() {
        return this.lastLogin;
    }

    // Last log setter.
    public void setLastLogin(java.sql.Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Overrides the toString() method for custom printing.
    @Override
    public String toString() {
        return "Member{"
                + "memberId=" + this.memberId
                + ", firstName='" + this.firstName + '\''
                + ", lastName='" + this.lastName + '\''
                + ", membershipType='" + this.membershipType + '\''
                + ", birthDate=" + this.birthDate
                + ", emailAddress='" + this.emailAddress + '\''
                + ", password='" + this.password + '\''
                + ", lastLogin=" + this.lastLogin
                + '}';
    }
}