package com.real.estate.models;

import com.real.estate.enums.Shape;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Land {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private Double area = 0.0;
    private Double length = 0.0;
    private Double width = 0.0;
    private Double meterSquarePrice = 0.0;
    private String latitude = "";
    private String longitude = "";
    private String description = "";
    @Enumerated
    private Shape shape = Shape.IRREGULAR;
    @OneToMany
    private List<Media> medias = new ArrayList<>();
    @OneToMany
    private List<Document> documents = new ArrayList<>();
}
