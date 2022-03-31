package ru.jtc.moneytrans.service.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.rest.dto.FilteringDto;

import java.util.Date;
import java.util.Objects;

public class PaymentSpecification {

    public static Specification<Payment> makeSpecification(FilteringDto dto) {
        Specification<Payment> specification = (root, query, criteriaBuilder) -> null;
        if (Objects.nonNull(dto.getPayerAccountId())) {
            specification = specification.and(outgoingPayments(dto.getPayerAccountId()));
        }
        if (Objects.nonNull(dto.getReceiverAccountId())) {
            specification = specification.and(incomingPayments(dto.getReceiverAccountId()));
        }
        if (Objects.nonNull(dto.getAmount())) {
            specification = specification.and(amount(dto.getAmount()));
        }
        if (Objects.nonNull(dto.getCreateDate())) {
            specification = specification.and(createDate(dto.getCreateDate()));
        }
        return specification;
    }

    public static Specification<Payment> outgoingPayments(Long payerAccountId) {
        return (r, cq, cb) -> cb.equal(r.get(Payment.Fields.payerAccount), payerAccountId);
    }

    public static Specification<Payment> incomingPayments(Long receiverAccountId) {
        return (r, cq, cb) -> cb.equal(r.get(Payment.Fields.receiverAccount), receiverAccountId);
    }

    public static Specification<Payment> amount(Double amount) {
        return (r, cq, cb) -> cb.equal(r.get(Payment.Fields.amount), amount);
    }

    public static Specification<Payment> createDate(Date date) {
        return (r, cq, cb) -> cb.equal(r.get(Payment.Fields.createDate), date);
    }

}
