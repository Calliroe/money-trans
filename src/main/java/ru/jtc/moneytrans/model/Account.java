package ru.jtc.moneytrans.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "bic")
    private Long bic;
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_type", referencedColumnName = "id")
    private AccountType accountType;
    @Column(name = "balance")
    private Double balance;
}
