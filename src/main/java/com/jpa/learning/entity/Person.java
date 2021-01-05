package com.jpa.learning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Person")
public class Person {
    
    @Id
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "SEQ_REGISTER_KEY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQUENCE")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "entity_Id")
    private Integer entityId;

    private String name;

    private String email;

    private Integer intvalue;

    public Person() {
    }

    public Person(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    public Person(Integer entityId, String name, String email, Integer intvalue) {
        super();
        this.entityId = entityId;
        this.name = name;
        this.email = email;
        this.intvalue = intvalue;
    }

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getIntvalue() {
        return intvalue;
    }

    public void setIntvalue(Integer intvalue) {
        this.intvalue = intvalue;
    }

    @Override
    public String toString() {
        return "User [id="
                + id
                + ", entityId="
                + entityId
                + ", name="
                + name
                + ", email="
                + email
                + ", intvalue="
                + intvalue
                + "]";
    }
}
