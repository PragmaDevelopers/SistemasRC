package com.api.sistema_rc.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false,name = "role_id")
    private Role role;
    @Column(length = 255,nullable = false)
    private String name;
    @Lob @Basic(fetch=FetchType.LAZY)
    @Column
    private Blob profilePicture;
    @Column(length = 255)
    private String pictureFormat;
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime registration_date;
    @Column(length = 255,nullable = false,unique = true)
    private String email;
    @Column(length = 255,unique = true)
    private String pushEmail;
    @Column(nullable = false)
    private boolean isVerify;
    @Column(length = 10,unique = true)
    private String codeToVerifyEmail;
    @Column(length = 10,unique = true)
    private String codeToChangeEmail;
    @Column(length = 10,unique = true)
    private String codeToChangePassword;
    @Column(unique = true)
    private String emailToChange;
    @Column(unique = true)
    private String passwordToChange;
    @Column(nullable = false)
    private boolean isReceiveNotification;
    @Column(nullable = false)
    private String password;
    @Column(length = 30,nullable = false)
    private String nationality;
    @Column(length = 20)
    private String gender;
    @Column(length = 255,nullable = false)
    private String permissionLevel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Blob getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Blob profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPictureFormat() {
        return pictureFormat;
    }

    public void setPictureFormat(String pictureFormat) {
        this.pictureFormat = pictureFormat;
    }

    public LocalDateTime getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(LocalDateTime registration_date) {
        this.registration_date = registration_date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPushEmail() {
        return pushEmail;
    }

    public void setPushEmail(String pushEmail) {
        this.pushEmail = pushEmail;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    public String getCodeToVerifyEmail() {
        return codeToVerifyEmail;
    }

    public void setCodeToVerifyEmail(String codeToVerifyEmail) {
        this.codeToVerifyEmail = codeToVerifyEmail;
    }

    public String getCodeToChangeEmail() {
        return codeToChangeEmail;
    }

    public void setCodeToChangeEmail(String codeToChangeEmail) {
        this.codeToChangeEmail = codeToChangeEmail;
    }

    public String getCodeToChangePassword() {
        return codeToChangePassword;
    }

    public void setCodeToChangePassword(String codeToChangePassword) {
        this.codeToChangePassword = codeToChangePassword;
    }

    public String getEmailToChange() {
        return emailToChange;
    }

    public void setEmailToChange(String emailToChange) {
        this.emailToChange = emailToChange;
    }

    public String getPasswordToChange() {
        return passwordToChange;
    }

    public void setPasswordToChange(String passwordToChange) {
        this.passwordToChange = passwordToChange;
    }

    public boolean isReceiveNotification() {
        return isReceiveNotification;
    }

    public void setReceiveNotification(boolean receiveNotification) {
        isReceiveNotification = receiveNotification;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
