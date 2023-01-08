package com.real.estate.models;

import com.real.estate.enums.MediaType;

import javax.persistence.*;

@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String title = "";
    private MediaType mediaType = MediaType.IMAGE;
}
