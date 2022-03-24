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
    private long id;
    @ManyToOne
    @JoinColumn(name = "payer_account_id",referencedColumnName = "id")
    private Account payerAccount;
    @ManyToOne
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
    private Account receiverAccount;
    private double amount;
    private String comment;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "modify_date")
    private Date modifyDate;
}
