package com.real.estate.models;

import javax.persistence.*;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String email = "";
    private String phoneNumber1 = "";
    private String phoneNumber2 = "";
}
