package ru.jtc.moneytrans.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "account_types")
public class AccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String type;
}
