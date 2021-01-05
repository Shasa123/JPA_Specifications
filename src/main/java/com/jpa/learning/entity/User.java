package com.jpa.learning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "SEQ_REGISTER_KEY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQUENCE")
    @Column(name = "ID")
    private long id;

    @Column(name = "entity_Id")
    private Integer entityId;

    private String name;

    private String email;

    private Integer intvalue;

    private Date datevalue;

    public User() {
    }

    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    public User(Integer entityId, String name, String email, Integer intvalue) {
        super();
        this.entityId = entityId;
        this.name = name;
        this.email = email;
        this.intvalue = intvalue;
    }

    public User(Integer entityId, String name, String email, Integer intvalue, Date dateValue) {
        super();
        this.entityId = entityId;
        this.name = name;
        this.email = email;
        this.intvalue = intvalue;
        this.datevalue = dateValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Date getDateValue() {
        return datevalue;
    }

    public void setDateValue(Date dateValue) {
        this.datevalue = dateValue;
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
                + ", datevalue="
                + datevalue
                + "]";
    }

    
}
