package com.real.estate.models;

import com.real.estate.enums.Privilege;
import com.real.estate.constants.Role;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ng_user", uniqueConstraints = { @UniqueConstraint(columnNames = {"firstName", "lastName"}) })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String firstName = "";
    @Column(nullable = false)
    private String lastName = "";
    @Column(nullable = false, unique = true)
    private String email = "";
    @Column(nullable = false)
    private String telephone = "";
    @Column(nullable = false, unique = true)
    private String username = "";
    @Column(nullable = false)
    private String password = "";
    @Column(name = "actif", nullable = false)
    private int enabled = 1;
    @Column(nullable = false)
    private String roles = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }

    public List<String> getRoleList() {
        Role[] values = Role.values();
        Privilege[] items = Privilege.values();
        List<String> privileges = new ArrayList<>();
        for(Role value: values){
            if(roles.contains(value.name())) privileges.add(value.name());
        }
        for(Privilege value: items){
            if(roles.contains(value.name())) privileges.add(value.name());
        }
        return privileges;
    }

    public boolean hasRole(String role){
        return this.getRoleList().contains(role);
    }

    public void setRoles(String roles) {
        this.roles = roles;
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

    public String getFullName(){
        return (StringUtils.defaultString(firstName)  + " " + StringUtils.defaultString(lastName)).trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public User() {
    }

    public User(Long id, String password, String email, String roles) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public void normalize(){
        if(this.firstName != null) this.firstName = this.firstName.trim().toUpperCase();
        if(this.lastName != null) this.lastName = Arrays.stream(this.lastName.trim().toLowerCase().split("\\s+")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(this.email != null) this.email = this.email.trim();
    }
}
