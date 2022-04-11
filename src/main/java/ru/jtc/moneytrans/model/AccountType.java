package ru.jtc.moneytrans.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "mt_account_type")
public class AccountType {

    @Version
    @Column(name = "version")
    private Long version;
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "type")
    private String type;

}
