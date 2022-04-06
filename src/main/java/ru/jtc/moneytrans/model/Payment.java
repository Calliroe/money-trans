package ru.jtc.moneytrans.model;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "mt_payment")
@FieldNameConstants
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Исправить стратегию
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "payer_account_id", referencedColumnName = "id")
    private Account payerAccount;
    @ManyToOne
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
    private Account receiverAccount;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "comment")
    private String comment;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "modify_date")
    private Date modifyDate;

}
