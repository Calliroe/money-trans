package ru.jtc.moneytrans.service.specification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.rest.dto.PaymentFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PaymentSpecificationCreator {

    private final AccountRepository accountRepository;

    public Specification<Payment> create(PaymentFilter filter) {
        Specification<Payment> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (Objects.isNull(filter)) {
            return specification;
        }
        if (Objects.nonNull(filter.getPayerAccountNumber()) && Objects.nonNull(filter.getPayerBic())) {
            Account payerAccount = accountRepository.findByAccountNumberAndBic(filter.getPayerAccountNumber(), filter.getPayerBic());
            if (Objects.isNull(payerAccount)) {
                return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
            }
            specification = specification.and(outgoingPayments(payerAccount.getId()));
        }
        if (Objects.nonNull(filter.getReceiverAccountNumber()) && Objects.nonNull(filter.getReceiverBic())) {
            Account receiverAccount = accountRepository.findByAccountNumberAndBic(filter.getReceiverAccountNumber(), filter.getReceiverBic());
            if (Objects.isNull(receiverAccount)) {
                return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
            }
            specification = specification.and(incomingPayments(receiverAccount.getId()));
        }
        if (Objects.nonNull(filter.getAmount())) {
            specification = specification.and(amount(filter.getAmount()));
        }
        if (Objects.nonNull(filter.getCreateDate())) {
            LocalDate createDate = filter.getCreateDate();
            LocalDateTime start = createDate.atStartOfDay();
            LocalDateTime end = start.plusDays(1);
            Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
            specification = specification.and(createDate(startDate, endDate));
        }
        return specification;
    }

    public static Specification<Payment> outgoingPayments(Long payerAccountId) {
        return (r, cq, cb) -> cb.equal(r.get(Payment.Fields.payerAccount), payerAccountId);
    }

    public static Specification<Payment> incomingPayments(Long receiverAccountId) {
        return (r, cq, cb) -> cb.equal(r.get(Payment.Fields.receiverAccount), receiverAccountId);
    }

    public static Specification<Payment> amount(BigDecimal amount) {
        return (r, cq, cb) -> cb.equal(r.get(Payment.Fields.amount), amount);
    }

    public static Specification<Payment> createDate(Date start, Date end) {
        return (r, cq, cb) -> cb.between(r.get(Payment.Fields.createDate), start, end);
    }

}
