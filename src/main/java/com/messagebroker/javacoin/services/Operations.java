package com.messagebroker.javacoin.services;

import com.messagebroker.javacoin.dto.BankDTO;
import com.messagebroker.javacoin.dto.CustomerDTO;
import com.messagebroker.javacoin.dto.TransactionDTO;
import com.messagebroker.javacoin.dto.WalletDTO;
import com.messagebroker.javacoin.exceptions.OperationException;
import com.messagebroker.javacoin.models.*;
import com.messagebroker.javacoin.repositoriy.BankRepository;
import com.messagebroker.javacoin.repositoriy.CustomerRepository;
import com.messagebroker.javacoin.repositoriy.TransactionRepository;
import com.messagebroker.javacoin.repositoriy.WalletRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
public class Operations {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(Operations.class);

    /***
     * This Method Create Customer to operate in Dollar or Java Coin
     *
     * @param customerDTO Customer Request
     */
    public Customer createCustomer(CustomerDTO customerDTO){
        return customerRepository.save(Customer.builder()
                .mailCustomer(customerDTO.getMailCustomer())
                .dniCustomer(customerDTO.getDniCustomer())
                .nameCustomer(customerDTO.getNameCustomer())
                .passwordCustomer(customerDTO.getPasswordCustomer()).build());
    }

    /***
     * This Method Create Account with currency of dollar
     *
     * @param accountDTO Account Request
     * @param customer Customer Object
     */
    public Bank createAccount(BankDTO accountDTO, Customer customer){
        return bankRepository.save(Bank.builder()
                .numberAccount(generateNumberAleatory(2))
                .dniAccount(accountDTO.getDniAccount())
                .creationDateAccount(LocalDateTime.now())
                .balanceDollar(new BigDecimal(0))
                .idCustomer(customer.getIdCustomer())
                .operations(0).build());
    }

    /***
     * This Method Create Wallet with a Deposit
     *
     * @param walletDTO Wallet Request
     */
    public Wallet createWalletWithDeposit(WalletDTO walletDTO){
        return walletRepository.save(Wallet.builder()
                .numberWallet(generateNumberAleatory(2))
                .dniWallet(walletDTO.getDniWallet())
                .creationDateWallet(LocalDateTime.now())
                .balanceJavaCoin(new BigDecimal(0))
                .idCustomer(walletDTO.getIdCustomer())
                .build());
    }

    /***
     * This Method Create Deposit to a specific Account
     * accredit dollar or java coin
     *
     * @param bank Bank Object
     * @param amountDollar Amount of Dollar
     * @param amountJavacoin Amount of Java Coin
     */
    public void deposit(Bank bank, BigDecimal amountDollar, BigDecimal amountJavacoin){
        bank.setBalanceDollar(bank.getBalanceDollar().add(amountDollar));
        Wallet walletAux = walletRepository.findWalletByDniWallet(bank.getDniAccount());

        if(walletAux != null) {
            walletAux.setBalanceJavaCoin(walletAux.getBalanceJavaCoin().add(amountJavacoin));
        }else{
            walletAux = createWalletWithDeposit(WalletDTO.builder().dniWallet(bank.getDniAccount()).idCustomer(bank.getIdCustomer()).build());
            walletAux.setBalanceJavaCoin(amountJavacoin);
        }

        bankRepository.save(bank);
        walletRepository.save(walletAux);
    }

    /***
     * This method send a single transaction to process
     *
     * @param dniOrigin Customer DNI for the Origin Account
     * @param dniDestination Customer DNI for the Destination Account
     */
    public String sendTransaction(String dniOrigin, String dniDestination){
        TransactionDTO transactionDTO = TransactionDTO.builder().description("one operation")
                .dniOrigin(dniOrigin).dniDestination(dniDestination).price(BigDecimal.valueOf(1))
                .amount(BigDecimal.valueOf(1)).type(TypeTransaction.BUY).commission(0.00).currency("JavaCoin")
                .status(StatusTransaction.PENDING).build();
        rabbitTemplate.convertAndSend("TopicSell","BUY",transactionDTO);
        return "success";
    }

    /***
     * This Method Create Transaction of BUY and SELL
     * from Origin Account to Destination Account
     *
     * @param transactionDTO Transaction Request
     */
    @RabbitListener(queues = "Queue1")
    public void createTransaction(TransactionDTO transactionDTO) throws OperationException {
        Bank origenAccount = bankRepository.findBankByDniAccount(transactionDTO.getDniOrigin());
        Wallet origenWallet = walletRepository.findWalletByDniWallet(transactionDTO.getDniOrigin());

        Bank destinationAccount = bankRepository.findBankByDniAccount(transactionDTO.getDniDestination());
        Wallet destinationWallet = walletRepository.findWalletByDniWallet(transactionDTO.getDniDestination());

        BigDecimal totalDollarOrigen = origenAccount.getBalanceDollar();
        BigDecimal totalJavaCoinOrigen = origenWallet.getBalanceJavaCoin();

        BigDecimal totalDollarDestination = destinationAccount.getBalanceDollar();
        BigDecimal totalJavaCoinDestination = destinationWallet.getBalanceJavaCoin();

        BigDecimal auxJavaCoin = transactionDTO.getPrice().multiply(transactionDTO.getAmount());

        if(validationBalance(transactionDTO, auxJavaCoin)){
            transactionDTO.setStatus(StatusTransaction.APPROVED);
            validationStatus(transactionDTO);
        }

        if (transactionDTO.getType().equals(TypeTransaction.BUY) &&
                transactionDTO.getStatus().equals(StatusTransaction.APPROVED)) {

            totalDollarOrigen = totalDollarOrigen.subtract(transactionDTO.getAmount())
                    .subtract(auxJavaCoin.multiply(BigDecimal.valueOf(transactionDTO.getCommission())));
            totalJavaCoinOrigen = totalJavaCoinOrigen.add(auxJavaCoin);

            origenAccount.setBalanceDollar(totalDollarOrigen);
            origenWallet.setBalanceJavaCoin(totalJavaCoinOrigen);

            totalDollarDestination = totalDollarDestination.add(transactionDTO.getAmount());
            totalJavaCoinDestination = totalJavaCoinDestination.subtract(auxJavaCoin);

            destinationAccount.setBalanceDollar(totalDollarDestination);
            destinationWallet.setBalanceJavaCoin(totalJavaCoinDestination);

            origenAccount.setOperations(origenAccount.getOperations()+1);

            bankRepository.save(origenAccount);
            walletRepository.save(origenWallet);

            bankRepository.save(destinationAccount);
            walletRepository.save(destinationWallet);

        }

        Transaction order = Transaction.builder().description(transactionDTO.getDescription())
                .dniDestination(transactionDTO.getDniDestination()).dniOrigin(transactionDTO.getDniOrigin())
                .commission(transactionDTO.getCommission()).currency(transactionDTO.getCurrency())
                .price(transactionDTO.getPrice()).amount(transactionDTO.getAmount())
                .status(transactionDTO.getStatus()).type(transactionDTO.getType()).creationDate(LocalDateTime.now())
                .build();
        transactionRepository.save(order);

        LOGGER.info("Nro Order: {}", order.getIdTransaction());
        LOGGER.info("Origen Account: {} The New Balance Account Dollars is: {}", origenAccount.getIdAccount(), origenAccount.getBalanceDollar());
        LOGGER.info("Origen Wallet: {} The New Balance Account JavaCoin is: {}", origenWallet.getIdWallet(), origenWallet.getBalanceJavaCoin());
        LOGGER.info("Destination Account: {} The New Balance Account Dollars is: {}", destinationAccount.getIdAccount(), destinationAccount.getBalanceDollar());
        LOGGER.info("Destination Wallet: {} The New Balance Account JavaCoin is: {}", destinationWallet.getIdWallet(), destinationWallet.getBalanceJavaCoin());
        LOGGER.info("Transaction Status:{} nro Order: {} DNI Customer: {} you buy: {} JavaCoin, at this price: U$D {} and this commission: {}%\n",
                order.getStatus(), order.getIdTransaction(), origenAccount.getDniAccount(), order.getAmount(), order.getPrice(), order.getCommission() * 100);

    }

    /***
     * This Method Validate Status of transaction
     *
     * @param transactionDTO Account Request
     */
    private void validationStatus(TransactionDTO transactionDTO) throws OperationException {

       if (transactionDTO.getStatus().equals(StatusTransaction.PENDING)) {
           sendErrorBankWallet(new OperationException("Please wait, we are verifying the transaction...").getMessage());

       }
    }

    /***
     * This Method Validate Balance of Origin or Destination Account to approved transaction
     * @param transactionDTO Account Request
     * @param auxJavaCoin Amount Java Coin to buy
     */
    private Boolean validationBalance(TransactionDTO transactionDTO, BigDecimal auxJavaCoin) throws OperationException {
        Boolean response = Boolean.TRUE;

        Wallet sellWallet = walletRepository.findWalletByDniWallet(transactionDTO.getDniDestination());
        Bank buyAccount = bankRepository.findBankByDniAccount(transactionDTO.getDniOrigin());

        if (buyAccount.getOperations()<4) {transactionDTO.setCommission(0.05);
        } else if ( buyAccount.getOperations()<6) {transactionDTO.setCommission(0.03);}

        BigDecimal auxCommissionBalance = auxJavaCoin.multiply(BigDecimal.valueOf(transactionDTO.getCommission()));

        if (transactionDTO.getType().equals(TypeTransaction.BUY)
                && buyAccount.getBalanceDollar().compareTo(auxCommissionBalance)<0) {
                response= Boolean.FALSE;
                sendErrorBankWallet(new OperationException("Insufficient balance in dollars to buy Java Coin, Origin Account: "+buyAccount.getIdAccount()).getMessage());
        }

        if (transactionDTO.getType().equals(TypeTransaction.SELL)
                && sellWallet.getBalanceJavaCoin().compareTo(auxJavaCoin)<0) {
            response= Boolean.FALSE;
            sendErrorBankWallet(new OperationException("Insufficient balance in Java Coin to sell").getMessage());
        }
         return response;
    }

    /***
     * This method is only to proof the api with commission %
     * for that add operations number.
     *
     * @param bank Account Customer
     */
    public void addOperationToTestApi(Bank bank){
        if(bank.getDniAccount().equals("3")){
            bank.setOperations(6);
            bankRepository.save(bank);
        } else if (bank.getDniAccount().equals("2")) {
            bank.setOperations(4);
            bankRepository.save(bank);
        }
    }

    /***
     * This Method Generate Automatic Number Account
     * @param cant number of numbers the account will have
     */
    private static String generateNumberAleatory(int cant){
        String generateNumber = "";
        for(int i = 0; i < cant; i++) {
            int newNumber = (int) (Math.random() * 10);
            generateNumber += String.valueOf(newNumber);
        }
        return generateNumber;
    }


    /***
     * This Method sends Error Message from bank or wallet
     *
     * @param messageError message to display.
     */
    private void sendErrorBankWallet(String messageError) {
        rabbitTemplate.convertAndSend("ErrorBankWallet", messageError);
    }

    /***
     * This Method receives Error Message from bank or wallet
     *
     * @param message message to display.
     */

    @RabbitListener(queues = "ErrorBankWallet")
    public void errorApi(String message){
        LOGGER.error(message);
    }

}
