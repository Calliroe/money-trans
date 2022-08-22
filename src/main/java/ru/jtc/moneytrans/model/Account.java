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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_sequence", allocationSize = 1)
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
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
