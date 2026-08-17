package com.trophix.api.offers.model;

import com.trophix.api.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfferTest {

    private Offer offer(BigDecimal original, BigDecimal discount) {
        return Offer.create("PS5 Edição Digital", "https://exemplo.com/img.jpg",
                original, discount, "Amazon", "https://exemplo.com/link", "Consoles", false);
    }

    @Test
    void calculatesSimplePercentage() {
        assertEquals(Integer.valueOf(25), offer(new BigDecimal("100.00"), new BigDecimal("75.00")).discountPercentage());
    }

    @Test
    void roundsHalfUp() {
        assertEquals(Integer.valueOf(67), offer(new BigDecimal("100.00"), new BigDecimal("33.33")).discountPercentage());
    }

    @Test
    void fullDiscountIsOneHundredPercent() {
        assertEquals(Integer.valueOf(100), offer(new BigDecimal("100.00"), BigDecimal.ZERO).discountPercentage());
    }

    @Test
    void noDiscountIsZeroPercent() {
        assertEquals(Integer.valueOf(0), offer(new BigDecimal("100.00"), new BigDecimal("100.00")).discountPercentage());
    }

    @Test
    void rejectsDiscountAboveOriginalPrice() {
        assertThrows(BusinessException.class,
                () -> offer(new BigDecimal("50.00"), new BigDecimal("60.00")));
    }

    @Test
    void rejectsNonPositiveOriginalPrice() {
        assertThrows(BusinessException.class,
                () -> offer(BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
