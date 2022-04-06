package ru.jtc.moneytrans.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "mt_account")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Исправить стратегию
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
    private BigDecimal balance;

}
