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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
    @SequenceGenerator(name = "payment_seq", sequenceName = "payment_sequence", allocationSize = 1)
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "modify_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;

}
