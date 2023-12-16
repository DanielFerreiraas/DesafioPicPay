package com.picpaysimplificado.dtos.transaction;

import java.math.BigDecimal;

public record TransactionDTO(BigDecimal value, Long senderId, Long receiverId) {
    
}
