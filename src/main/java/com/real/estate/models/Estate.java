package com.real.estate.models;

import javax.persistence.*;

@Entity
public class Estate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private Double price = 0.0;
    private String country = "";
    private String city = "";
    private String latitude = "";
    private String longitude = "";
    private String description = "";
}
