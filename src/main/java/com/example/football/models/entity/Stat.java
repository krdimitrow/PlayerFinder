package com.example.football.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "stats")
public class Stat extends BaseEntity {

    private float endurance;
    private float passing;
    private float shooting;

    public Stat() {
    }

    @Column(nullable = false)
    @Positive
    public float getEndurance() {
        return endurance;
    }

    public void setEndurance(float endurance) {
        this.endurance = endurance;
    }

    @Column(nullable = false)
    @Positive
    public float getPassing() {
        return passing;
    }

    public void setPassing(float passing) {
        this.passing = passing;
    }

    @Column(nullable = false)
    @Positive
    public float getShooting() {
        return shooting;
    }

    public void setShooting(float shooting) {
        this.shooting = shooting;
    }
}
