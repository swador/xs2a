/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.aspsp.xs2a.service;

import de.adorsys.aspsp.xs2a.domain.*;
import de.adorsys.aspsp.xs2a.domain.account.AccountDetails;
import de.adorsys.aspsp.xs2a.domain.account.AccountReference;
import de.adorsys.aspsp.xs2a.domain.account.AccountReport;
import de.adorsys.aspsp.xs2a.domain.consent.AccountAccess;
import de.adorsys.aspsp.xs2a.exception.MessageError;
import de.adorsys.aspsp.xs2a.service.mapper.AccountMapper;
import de.adorsys.aspsp.xs2a.service.validator.ValidationGroup;
import de.adorsys.aspsp.xs2a.service.validator.ValueValidatorService;
import de.adorsys.aspsp.xs2a.spi.domain.account.SpiBookingStatus;
import de.adorsys.aspsp.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.aspsp.xs2a.spi.service.AccountSpi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static de.adorsys.aspsp.xs2a.domain.MessageErrorCode.CONSENT_INVALID;
import static de.adorsys.aspsp.xs2a.domain.MessageErrorCode.RESOURCE_UNKNOWN_404;
import static de.adorsys.aspsp.xs2a.exception.MessageCategory.ERROR;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class AccountService {

    private final AccountSpi accountSpi;
    private final AccountMapper accountMapper;
    private final ValueValidatorService validatorService;
    private final ConsentService consentService;

    /**
     * @param consentId   String representing an AccountConsent identification
     * @param withBalance boolean representing if the responded AccountDetails should contain
     * @param psuInvolved Not applicable since v1.1
     * @return List of AccountDetails with Balances if requested and granted by consent
     * Gets AccountDetails list based on accounts in provided AIS-consent, depending on withBalance variable and
     * AccountAccess in AIS-consent Balances are passed along with AccountDetails.
     */
    public ResponseObject<Map<String, List<AccountDetails>>> getAccountDetailsList(String consentId, boolean withBalance, boolean psuInvolved) {
        ResponseObject<AccountAccess> allowedAccountData = consentService.getValidatedConsent(consentId);
        if (allowedAccountData.hasError()) {
            return ResponseObject.<Map<String, List<AccountDetails>>>builder()
                       .fail(allowedAccountData.getError()).build();
        }

        List<AccountDetails> accountDetails = getAccountDetailsFromReferences(withBalance, allowedAccountData.getBody());

        return accountDetails.isEmpty()
                   ? ResponseObject.<Map<String, List<AccountDetails>>>builder()
                         .fail(new MessageError(new TppMessageInformation(ERROR, CONSENT_INVALID))).build()
                   : ResponseObject.<Map<String, List<AccountDetails>>>builder()
                         .body(Collections.singletonMap("accountList", accountDetails)).build();
    }

    /**
     * @param consentId   String representing an AccountConsent identification
     * @param accountId   String representing a PSU`s Account at ASPSP
     * @param withBalance boolean representing if the responded AccountDetails should contain
     * @param psuInvolved Not applicable since v1.1
     * @return AccountDetails based on accountId with Balances if requested and granted by consent
     * Gets AccountDetails based on accountId, details get checked with provided AIS-consent, depending on withBalance variable and
     * AccountAccess in AIS-consent Balances are passed along with AccountDetails.
     */
    public ResponseObject<AccountDetails> getAccountDetails(String consentId, String accountId, boolean withBalance, boolean psuInvolved) {
        ResponseObject<AccountAccess> allowedAccountData = consentService.getValidatedConsent(consentId);
        if (allowedAccountData.hasError()) {
            return ResponseObject.<AccountDetails>builder()
                       .fail(allowedAccountData.getError()).build();
        }

        AccountDetails accountDetails = accountMapper.mapToAccountDetails(accountSpi.readAccountDetails(accountId));
        if (accountDetails == null) {
            return ResponseObject.<AccountDetails>builder()
                       .fail(new MessageError(new TppMessageInformation(ERROR, RESOURCE_UNKNOWN_404))).build();
        }

        AccountDetails details = null;
        if (withBalance && consentService.isValidAccountByAccess(accountDetails.getIban(), accountDetails.getCurrency(), allowedAccountData.getBody().getBalances())) {
            details = accountDetails;
        } else if (!withBalance && consentService.isValidAccountByAccess(accountDetails.getIban(), accountDetails.getCurrency(), allowedAccountData.getBody().getAccounts())) {
            details = getAccountDetailNoBalances(accountDetails);
        }

        return details == null
                   ? ResponseObject.<AccountDetails>builder()
                         .fail(new MessageError(new TppMessageInformation(ERROR, CONSENT_INVALID))).build()
                   : ResponseObject.<AccountDetails>builder()
                         .body(details).build();
    }

    /**
     * @param consentId   String representing an AccountConsent identification
     * @param accountId   String representing a PSU`s Account at ASPSP
     * @param psuInvolved Not applicable since v1.1
     * @return List of AccountBalances based on accountId if granted by consent
     * Gets AccountDetails based on accountId, details get checked with provided AIS-consent Balances section
     */
    public ResponseObject<List<Balances>> getBalances(String consentId, String accountId, boolean psuInvolved) {
        ResponseObject<AccountAccess> allowedAccountData = consentService.getValidatedConsent(consentId);
        if (allowedAccountData.hasError()) {
            return ResponseObject.<List<Balances>>builder()
                       .fail(allowedAccountData.getError()).build();
        }

        AccountDetails accountDetails = accountMapper.mapToAccountDetails(accountSpi.readAccountDetails(accountId));
        if (accountDetails == null) {
            return ResponseObject.<List<Balances>>builder()
                       .fail(new MessageError(new TppMessageInformation(ERROR, RESOURCE_UNKNOWN_404))).build();
        }

        return consentService.isValidAccountByAccess(accountDetails.getIban(), accountDetails.getCurrency(), allowedAccountData.getBody().getBalances())
                   ? ResponseObject.<List<Balances>>builder().body(accountDetails.getBalances()).build()
                   : ResponseObject.<List<Balances>>builder()
                         .fail(new MessageError(new TppMessageInformation(ERROR, CONSENT_INVALID))).build();
    }

    /**
     * @param consentId     String representing an AccountConsent identification
     * @param accountId     String representing a PSU`s Account at ASPSP
     * @param dateFrom      ISO Date representing the value of desired start date of AccountReport
     * @param dateTo        ISO Date representing the value of desired end date of AccountReport (if omitted is set to current date)
     * @param transactionId String representing the ASPSP identification of transaction
     * @param psuInvolved   Not applicable since v1.1
     * @param bookingStatus ENUM representing either one of BOOKED/PENDING or BOTH transaction statuses
     * @param withBalance   boolean representing if the responded AccountDetails should contain. Not applicable since v1.1
     * @param deltaList     boolean  indicating that the AISP is in favour to get all transactions after the last report access for this PSU on the addressed account
     * @return AccountReport filled with appropriate transaction arrays Booked and Pending. For v1.1 balances sections is added
     * Gets AccountReport with Booked/Pending or both transactions dependent on request.
     * Uses one of two ways to get transaction from ASPSP: 1. By transactionId, 2. By time period limited with dateFrom/dateTo variables
     * Checks if all transactions are related to accounts set in AccountConsent Transactions section
     */
    public ResponseObject<AccountReport> getAccountReport(String consentId, String accountId, Date dateFrom,
                                                          Date dateTo, String transactionId,
                                                          boolean psuInvolved, BookingStatus bookingStatus, boolean withBalance, boolean deltaList) {
        ResponseObject<AccountAccess> allowedAccountData = consentService.getValidatedConsent(consentId);
        if (allowedAccountData.hasError()) {
            return ResponseObject.<AccountReport>builder()
                       .fail(allowedAccountData.getError()).build();
        }

        AccountDetails accountDetails = accountMapper.mapToAccountDetails(accountSpi.readAccountDetails(accountId));
        if (accountDetails == null) {
            return ResponseObject.<AccountReport>builder().fail(new MessageError(new TppMessageInformation(ERROR, RESOURCE_UNKNOWN_404))).build();
        }

        AccountReport accountReport = consentService.isValidAccountByAccess(accountDetails.getIban(), accountDetails.getCurrency(), allowedAccountData.getBody().getTransactions())
                                          ? getAccountReport(accountDetails, dateFrom, dateTo, transactionId, bookingStatus, allowedAccountData.getBody().getTransactions())
                                          : null;

        return accountReport == null
                   ? ResponseObject.<AccountReport>builder().fail(new MessageError(new TppMessageInformation(ERROR, CONSENT_INVALID))).build()
                   : ResponseObject.<AccountReport>builder().body(accountReport).build();
    }

    List<Balances> getAccountBalancesByAccountReference(AccountReference reference) {
        return Optional.ofNullable(reference)
                   .map(this::getAccountDetailsByAccountReference)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .map(AccountDetails::getBalances)
                   .orElse(Collections.emptyList());
    }

    boolean isAccountExists(AccountReference reference) {
        return getAccountDetailsByAccountReference(reference).isPresent();
    }

    private List<AccountDetails> getAccountDetailsFromReferences(boolean withBalance, AccountAccess accountAccess) {
        List<AccountReference> references = withBalance
                                                ? accountAccess.getBalances()
                                                : accountAccess.getAccounts();
        List<AccountDetails> details = getAccountDetailsFromReferences(references);
        return withBalance
                   ? details
                   : getAccountDetailsNoBalances(details);
    }

    private List<AccountDetails> getAccountDetailsFromReferences(List<AccountReference> references) {
        return CollectionUtils.isEmpty(references)
                   ? Collections.emptyList()
                   : references.stream()
                         .map(this::getAccountDetailsByAccountReference)
                         .filter(Optional::isPresent)
                         .collect(Collectors.mapping(Optional::get, Collectors.toList()));
    }

    private List<AccountDetails> getAccountDetailsNoBalances(List<AccountDetails> details) {
        return details.stream()
                   .map(this::getAccountDetailNoBalances)
                   .collect(Collectors.toList());
    }

    private AccountDetails getAccountDetailNoBalances(AccountDetails detail) {
        return new AccountDetails(detail.getId(), detail.getIban(), detail.getBban(), detail.getPan(),
            detail.getMaskedPan(), detail.getMsisdn(), detail.getCurrency(), detail.getName(),
            detail.getAccountType(), detail.getCashAccountType(), detail.getBic(), null);
    }

    private AccountReport getAccountReport(AccountDetails details, Date dateFrom, Date dateTo, String transactionId, BookingStatus bookingStatus, List<AccountReference> allowedAccountData) {
        Date dateToChecked = dateTo == null ? new Date() : dateTo; //TODO Migrate Date to Instant. Task #126 https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/126
        return StringUtils.isBlank(transactionId)
                   ? getAccountReportByPeriod(details, dateFrom, dateToChecked, bookingStatus, allowedAccountData)
                   : getAccountReportByTransaction(details, transactionId, allowedAccountData);
    }

    private AccountReport getAccountReportByPeriod(AccountDetails details, Date dateFrom, Date dateTo, BookingStatus bookingStatus, List<AccountReference> allowedAccountData) {
        validateAccountIdPeriod(details.getIban(), dateFrom, dateTo);
        return getAllowedTransactionsByAccess(readTransactionsByPeriod(details, dateFrom, dateTo, bookingStatus), allowedAccountData);
    }

    private AccountReport getAccountReportByTransaction(AccountDetails details, String transactionId, List<AccountReference> allowedAccountData) {
        validateAccountIdTransactionId(details.getIban(), transactionId);
        return readTransactionsById(transactionId, allowedAccountData);
    }

    private AccountReport getAllowedTransactionsByAccess(AccountReport accountReport, List<AccountReference> allowedAccountData) {
        if (accountReport == null) {
            return null;
        }
        Transactions[] booked = getAllowedTransactions(accountReport.getBooked(), allowedAccountData);
        Transactions[] pending = getAllowedTransactions(accountReport.getPending(), allowedAccountData);
        return new AccountReport(booked, pending);
    }

    private Transactions[] getAllowedTransactions(Transactions[] transactions, List<AccountReference> allowedAccountData) {
        return Arrays.stream(transactions).allMatch(t -> isAllowedTransaction(t, allowedAccountData))
                   ? transactions
                   : new Transactions[]{};
    }

    private boolean isAllowedTransaction(Transactions transaction, List<AccountReference> allowedAccountData) {
        return consentService.isValidAccountByAccess(transaction.getCreditorAccount().getIban(), transaction.getCreditorAccount().getCurrency(), allowedAccountData)
                   || consentService.isValidAccountByAccess(transaction.getDebtorAccount().getIban(), transaction.getDebtorAccount().getCurrency(), allowedAccountData);
    }

    private AccountReport readTransactionsByPeriod(AccountDetails details, Date dateFrom,
                                                   Date dateTo, BookingStatus bookingStatus) { //NOPMD TODO to be reviewed upon change to v1.1
        Optional<AccountReport> result = accountMapper.mapToAccountReport(accountSpi.readTransactionsByPeriod(details.getIban(), details.getCurrency(), dateFrom, dateTo, SpiBookingStatus.valueOf(bookingStatus.name())));

        return result.orElseGet(() -> new AccountReport(new Transactions[]{}, new Transactions[]{}));
    }

    private AccountReport readTransactionsById(String transactionId, List<AccountReference> allowedAccountData) {
        SpiTransaction spiTransaction = accountSpi.readTransactionsById(transactionId);
        return isAllowedSpiTransaction(spiTransaction, allowedAccountData)
                   ? accountMapper.mapToAccountReport(Collections.singletonList(spiTransaction)).orElseGet(() -> null)
                   : null;
    }

    private boolean isAllowedSpiTransaction(SpiTransaction spiTransaction, List<AccountReference> allowedAccountData) {
        return Optional.ofNullable(spiTransaction)
                   .filter(t -> isAllowedTransaction(accountMapper.mapToTransaction(t), allowedAccountData))
                   .isPresent();
    }

    private Optional<AccountDetails> getAccountDetailsByAccountReference(AccountReference reference) {
        return Optional.ofNullable(reference)
                   .map(ref -> accountSpi.readAccountDetailsByIban(ref.getIban()))
                   .map(Collection::stream)
                   .flatMap(accDets -> accDets
                                           .filter(spiAcc -> spiAcc.getCurrency() == reference.getCurrency())
                                           .findFirst())
                   .map(accountMapper::mapToAccountDetails);
    }

    // Validation
    private void validateAccountIdPeriod(String accountId, Date dateFrom, Date dateTo) {
        ValidationGroup fieldValidator = new ValidationGroup();
        fieldValidator.setAccountId(accountId);
        fieldValidator.setDateFrom(dateFrom);
        fieldValidator.setDateTo(dateTo);

        validatorService.validate(fieldValidator, ValidationGroup.AccountIdAndPeriodIsValid.class);
    }

    private void validateAccountIdTransactionId(String accountId, String transactionId) {
        ValidationGroup fieldValidator = new ValidationGroup();
        fieldValidator.setAccountId(accountId);
        fieldValidator.setTransactionId(transactionId);

        validatorService.validate(fieldValidator, ValidationGroup.AccountIdAndTransactionIdIsValid.class);
    }
}
