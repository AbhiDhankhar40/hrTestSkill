package com.test.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name", nullable = false, length = 150)
    private String courseName;

    @Column(name = "course_code", nullable = false, unique = true, length = 50)
    private String courseCode;

    @Column(name = "course_short_name", length = 50)
    private String courseShortName;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "course_type", length = 50)
    private String courseType;

    @Column(name = "faculty_id", nullable = false)
    private Integer facultyId;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "modify_date", nullable = false)
    private LocalDateTime modifyDate;

    @Column(name = "modify_by", length = 100)
    private String modifyBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_prospectus_sale", nullable = false)
    private Boolean isProspectusSale = true;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.modifyDate = now;
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isProspectusSale == null) {
            this.isProspectusSale = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = LocalDateTime.now();
    }
}
