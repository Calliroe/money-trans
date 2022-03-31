package ru.jtc.moneytrans.model;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "payments")
@FieldNameConstants
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "payer_account_id", referencedColumnName = "id")
    private Account payerAccount;
    @ManyToOne
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
