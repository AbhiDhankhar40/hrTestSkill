package com.test.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class AnswerEntry {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userId;

    private Long dataEntryId;

    private Integer ans1 ;
    private Integer ans2 ;
    private Integer ans3 ;
    private Integer ans4 ;
    private Integer ans5 ;
    private Integer ans6 ;
    private Integer ans7 ;
    private Integer ans8 ;
    private Integer ans9 ;
    private Integer ans10 ;
    private Integer ans11 ;
    private Integer ans12 ;
    private Integer ans13 ;
    private Integer ans14 ;
    private Integer ans15 ;
    private Integer ans16 ;
    private Integer ans17 ;
    private Integer ans18 ;
    private Integer ans19 ;
    private Integer ans20 ;
    private Integer ans21 ;
    private Integer ans22 ;
    private Integer ans23 ;
    private Integer ans24 ;
    private Integer ans25 ;
    private Integer ans26 ;
    private Integer ans27 ;
    private Integer ans28 ;
    private Integer ans29 ;
    private Integer ans30 ;
    private Integer ans31 ;
    private Integer ans32 ;
    private Integer ans33 ;
    private Integer ans34 ;
    private Integer ans35 ;
    private Integer ans36 ;
    private Integer ans37 ;
    private Integer ans38 ;
    private Integer ans39 ;
    private Integer ans40 ;
    private Integer ans41 ;
    private Integer ans42 ;
    private Integer ans43 ;
    private Integer ans44 ;
    private Integer ans45 ;
    private Integer ans46 ;
    private Integer ans47 ;
    private Integer ans48 ;
    private Integer ans49 ;
    private Integer ans50 ;
    private Integer ans51 ;
    private Integer ans52 ;
    private Integer ans53 ;
    private Integer ans54 ;
    private Integer ans55 ;     
    private Integer ans56 ;
    private Integer ans57 ;
    private Integer ans58 ;
    private Integer ans59 ;
    private Integer ans60 ;

}
