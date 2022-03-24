package ru.jtc.moneytrans.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_number")
    private long accountNumber;
    private long bic;
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_type", referencedColumnName = "id")
    private AccountType accountType;
    private double balance;
}
