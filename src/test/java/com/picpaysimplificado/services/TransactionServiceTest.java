package com.picpaysimplificado.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.transaction.TransactionDTO;
import com.picpaysimplificado.repositories.transactionRepository.TransactionRepository;
import com.picpaysimplificado.services.authorizationService.AuthorizationService;
import com.picpaysimplificado.services.notificationService.Notificationservice;
import com.picpaysimplificado.services.transactionService.TransactionService;
import com.picpaysimplificado.services.userService.UserService;

class TransactionServiceTest {

    @Mock 
    private UserService userService;
    
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private Notificationservice notificationservice;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Shold create transaction successfully when everything is OK")
    void createTransactionCase1() throws Exception {

        User sender = new User(1L, "Maria", "Souza", "99999999901", "maria@gmail.com", "123456", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Lucas", "Oliveira", "99999999902", "lucas@gmail.com", "1234567", new BigDecimal(10), UserType.MERCHANT);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(request);

        verify(transactionRepository, times(1)).save(any());

        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationservice, times(1)).sendNotification(sender, "Transação realizada com sucesso!!");
        verify(notificationservice, times(1)).sendNotification(receiver, "Transação recebida com sucesso!!");
        
    }

    @Test
    @DisplayName("Shold throw Exception when Transaction is not allowed")
    void createTransactionCase2() throws Exception{
        User sender = new User(1L, "Maria", "Souza", "99999999901", "maria@gmail.com", "123456", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Lucas", "Oliveira", "99999999902", "lucas@gmail.com", "1234567", new BigDecimal(10), UserType.MERCHANT);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(request);
        });

        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
    }
    
}
