package ru.jtc.moneytrans.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_account_id", referencedColumnName = "id")
    private Account payerAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
    private Account receiverAccount;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "comment")
    private String comment;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "modify_date")
    private Date modifyDate;
}
