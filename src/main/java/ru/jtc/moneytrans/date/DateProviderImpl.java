package ru.jtc.moneytrans.date;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class DateProviderImpl implements DateProvider {
    @Override
    public Date currentDate() {
        return new Timestamp(new Date().getTime());
    }

}
