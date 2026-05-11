package com.mby.myStore.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
public class BillCardsData {
    BigDecimal incomesPerDay;
    Integer emittedBills;
}
