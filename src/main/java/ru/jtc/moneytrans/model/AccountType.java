package ru.jtc.moneytrans.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "account_type")
public class AccountType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Исправить стратегию
    private Long id;
    @Column(name = "type")
    private String type;

}
