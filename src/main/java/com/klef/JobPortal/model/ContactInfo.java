package com.klef.JobPortal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Embeddable
public class ContactInfo {

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(length = 400)
    private String address;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "DD/MM/YYYY")
    private Date birthday;
}
