package com.picpaysimplificado.services.transactionService;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.transaction.TransactionDTO;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.repositories.transactionRepository.TransactionRepository;
import com.picpaysimplificado.services.authorizationService.AuthorizationService;
import com.picpaysimplificado.services.notificationService.Notificationservice;
import com.picpaysimplificado.services.userService.UserService;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private Notificationservice notificationservice;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {

        User sender = (User) this.userService.findUserById(transaction.senderId());
        User receiver = (User) this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizationService.authorizeTransaction(sender, transaction.value());
        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        notificationservice.sendNotification(sender, "Transação realizada com sucesso!!");
        notificationservice.sendNotification(receiver, "Transação recebida com sucesso!!");

        return newTransaction;

    }
}
