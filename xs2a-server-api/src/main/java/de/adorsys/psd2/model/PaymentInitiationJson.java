package de.adorsys.psd2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Generic Body for a payment initation via JSON.  This generic JSON body can be used to represent valid payment initiations for the following JSON based payment product,  which where defined in the Implementation Guidelines:    * sepa-credit-transfers   * instant-sepa-credit-transfers   * target-2-payments   * cross-border-credit-transfers  For the convenience of the implementer additional which are already predefinded in the Implementation Guidelines  are included (but commented in source code), such that an ASPSP may add them easily.  Take care: Since the format is intended to fit for all payment products  there are additional conditions which are NOT covered by this specification. Please check the Implementation Guidelines for detailes.   The following data element are depending on the actual payment product available (in source code):             &lt;table style&#x3D;\&quot;width:100%\&quot;&gt;  &lt;tr&gt;&lt;th&gt;Data Element&lt;/th&gt;&lt;th&gt;SCT EU Core&lt;/th&gt;&lt;th&gt;SCT INST EU Core&lt;/th&gt;&lt;th&gt;Target2 Paym. Core&lt;/th&gt;&lt;th&gt;Cross Border CT Core&lt;/th&gt;&lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;endToEndIdentification&lt;/td&gt;&lt;td&gt; optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;instructionIdentification&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;debtorName&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;debtorAccount&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;debtorId&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;ultimateDebtor&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;instructedAmount&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;currencyOfTransfer&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;exchangeRateInformation&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt;&lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;creditorAccount&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;creditorAgent&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;conditional &lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;creditorAgentName&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;creditorName&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;td&gt;mandatory&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;creditorId&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;creditorAddress&lt;/td&gt;optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;conditional &lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;creditorNameAndAddress&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;ultimateCreditor&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;purposeCode&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;chargeBearer&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;conditional &lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;serviceLevel&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a. &lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;remittanceInformationUnstructured&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;td&gt; optional&lt;/td&gt; &lt;td&gt;optional&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;remittanceInformationUnstructuredArray&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;remittanceInformationStructured&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;remittanceInformationStructuredArray&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;requestedExecutionDate&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;  &lt;tr&gt;&lt;td&gt;requestedExecutionTime&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;td&gt;n.a.&lt;/td&gt; &lt;/tr&gt;     &lt;/td&gt;&lt;/tr&gt;   &lt;/table&gt;    IMPORTANT: In this API definition the following holds:   *  All data elements mentioned above are defined, but some of them are commented,      i.e. they are only visible in the source code and can be used by uncommenting them.   * Data elements which are mandatory in the table above for all payment products      are set to be mandatory in this specification.   * Data elements which are indicated in the table above as n.a. for all payment products are commented in the source code.   * Data elements which are indicated to be option, conditional or mandatory for at least one payment product      in the table above are set to be optional in the s specification except the case where all are definde to be mandatory.    * Data element which are inticated to be n.a. can be used by the ASPS if needed.      In this case uncomment tthe the relatetd lines in the source code.   * If one uses this data types for some payment products he has to ensure that the used data type is      valid according to the underlying payment product, e.g. by some appropriate validations.
 */
@Schema(description = "Generic Body for a payment initation via JSON.  This generic JSON body can be used to represent valid payment initiations for the following JSON based payment product,  which where defined in the Implementation Guidelines:    * sepa-credit-transfers   * instant-sepa-credit-transfers   * target-2-payments   * cross-border-credit-transfers  For the convenience of the implementer additional which are already predefinded in the Implementation Guidelines  are included (but commented in source code), such that an ASPSP may add them easily.  Take care: Since the format is intended to fit for all payment products  there are additional conditions which are NOT covered by this specification. Please check the Implementation Guidelines for detailes.   The following data element are depending on the actual payment product available (in source code):             <table style=\"width:100%\">  <tr><th>Data Element</th><th>SCT EU Core</th><th>SCT INST EU Core</th><th>Target2 Paym. Core</th><th>Cross Border CT Core</th></tr>  <tr><td>endToEndIdentification</td><td> optional</td> <td>optional</td> <td>optional</td> <td>n.a.</td> </tr>  <tr><td>instructionIdentification</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>debtorName</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>debtorAccount</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> </tr>  <tr><td>debtorId</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>ultimateDebtor</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>instructedAmount</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> </tr>  <tr><td>currencyOfTransfer</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>exchangeRateInformation</td> <td>n.a.</td> <td>n.a.</td><td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>creditorAccount</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> </tr>  <tr><td>creditorAgent</td> <td>optional</td> <td>optional</td> <td>optional</td> <td>conditional </td> </tr>  <tr><td>creditorAgentName</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>creditorName</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> <td>mandatory</td> </tr>  <tr><td>creditorId</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>creditorAddress</td>optional</td> <td>optional</td> <td>optional</td> <td>conditional </td> </tr>  <tr><td>creditorNameAndAddress</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>ultimateCreditor</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>purposeCode</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>chargeBearer</td> <td>n.a.</td> <td>n.a.</td> <td>optional</td> <td>conditional </td> </tr>  <tr><td>serviceLevel</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a. </td> </tr>  <tr><td>remittanceInformationUnstructured</td> <td>optional</td> <td>optional</td> <td> optional</td> <td>optional</td> </tr>  <tr><td>remittanceInformationUnstructuredArray</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>remittanceInformationStructured</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>remittanceInformationStructuredArray</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>requestedExecutionDate</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>  <tr><td>requestedExecutionTime</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> <td>n.a.</td> </tr>     </td></tr>   </table>    IMPORTANT: In this API definition the following holds:   *  All data elements mentioned above are defined, but some of them are commented,      i.e. they are only visible in the source code and can be used by uncommenting them.   * Data elements which are mandatory in the table above for all payment products      are set to be mandatory in this specification.   * Data elements which are indicated in the table above as n.a. for all payment products are commented in the source code.   * Data elements which are indicated to be option, conditional or mandatory for at least one payment product      in the table above are set to be optional in the s specification except the case where all are definde to be mandatory.    * Data element which are inticated to be n.a. can be used by the ASPS if needed.      In this case uncomment tthe the relatetd lines in the source code.   * If one uses this data types for some payment products he has to ensure that the used data type is      valid according to the underlying payment product, e.g. by some appropriate validations. ")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-09T09:54:21.220655+03:00[Europe/Kiev]")


public class PaymentInitiationJson   {
  @JsonProperty("endToEndIdentification")
  private String endToEndIdentification = null;

  @JsonProperty("instructionIdentification")
  private String instructionIdentification = null;

  @JsonProperty("debtorName")
  private String debtorName = null;

  @JsonProperty("debtorAccount")
  private AccountReference debtorAccount = null;

  @JsonProperty("ultimateDebtor")
  private String ultimateDebtor = null;

  @JsonProperty("instructedAmount")
  private Amount instructedAmount = null;

  @JsonProperty("creditorAccount")
  private AccountReference creditorAccount = null;

  @JsonProperty("creditorAgent")
  private String creditorAgent = null;

  @JsonProperty("creditorAgentName")
  private String creditorAgentName = null;

  @JsonProperty("creditorName")
  private String creditorName = null;

  @JsonProperty("creditorAddress")
  private Address creditorAddress = null;

  @JsonProperty("creditorId")
  private String creditorId = null;

  @JsonProperty("ultimateCreditor")
  private String ultimateCreditor = null;

  @JsonProperty("purposeCode")
  private PurposeCode purposeCode = null;

  @JsonProperty("chargeBearer")
  private ChargeBearer chargeBearer = null;

  @JsonProperty("remittanceInformationUnstructured")
  private String remittanceInformationUnstructured = null;

  @JsonProperty("remittanceInformationUnstructuredArray")
  private RemittanceInformationUnstructuredArray remittanceInformationUnstructuredArray = null;

  @JsonProperty("remittanceInformationStructured")
  private RemittanceInformationStructuredMax140 remittanceInformationStructured = null;

  @JsonProperty("remittanceInformationStructuredArray")
  private RemittanceInformationStructuredArray remittanceInformationStructuredArray = null;

  @JsonProperty("requestedExecutionDate")
  private LocalDate requestedExecutionDate = null;

  public PaymentInitiationJson endToEndIdentification(String endToEndIdentification) {
    this.endToEndIdentification = endToEndIdentification;
    return this;
  }

  /**
   * Get endToEndIdentification
   * @return endToEndIdentification
   **/
  @Schema(description = "")

  @Size(max=35)   public String getEndToEndIdentification() {
    return endToEndIdentification;
  }

  public void setEndToEndIdentification(String endToEndIdentification) {
    this.endToEndIdentification = endToEndIdentification;
  }

  public PaymentInitiationJson instructionIdentification(String instructionIdentification) {
    this.instructionIdentification = instructionIdentification;
    return this;
  }

  /**
   * Get instructionIdentification
   * @return instructionIdentification
   **/
  @Schema(description = "")

  @Size(max=35)   public String getInstructionIdentification() {
    return instructionIdentification;
  }

  public void setInstructionIdentification(String instructionIdentification) {
    this.instructionIdentification = instructionIdentification;
  }

  public PaymentInitiationJson debtorName(String debtorName) {
    this.debtorName = debtorName;
    return this;
  }

  /**
   * Debtor name.
   * @return debtorName
   **/
  @Schema(example = "Debtor Name", description = "Debtor name.")

  @Size(max=70)   public String getDebtorName() {
    return debtorName;
  }

  public void setDebtorName(String debtorName) {
    this.debtorName = debtorName;
  }

  public PaymentInitiationJson debtorAccount(AccountReference debtorAccount) {
    this.debtorAccount = debtorAccount;
    return this;
  }

  /**
   * Get debtorAccount
   * @return debtorAccount
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public AccountReference getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(AccountReference debtorAccount) {
    this.debtorAccount = debtorAccount;
  }

  public PaymentInitiationJson ultimateDebtor(String ultimateDebtor) {
    this.ultimateDebtor = ultimateDebtor;
    return this;
  }

  /**
   * Ultimate debtor.
   * @return ultimateDebtor
   **/
  @Schema(example = "Ultimate Debtor", description = "Ultimate debtor.")

  @Size(max=70)   public String getUltimateDebtor() {
    return ultimateDebtor;
  }

  public void setUltimateDebtor(String ultimateDebtor) {
    this.ultimateDebtor = ultimateDebtor;
  }

  public PaymentInitiationJson instructedAmount(Amount instructedAmount) {
    this.instructedAmount = instructedAmount;
    return this;
  }

  /**
   * Get instructedAmount
   * @return instructedAmount
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public Amount getInstructedAmount() {
    return instructedAmount;
  }

  public void setInstructedAmount(Amount instructedAmount) {
    this.instructedAmount = instructedAmount;
  }

  public PaymentInitiationJson creditorAccount(AccountReference creditorAccount) {
    this.creditorAccount = creditorAccount;
    return this;
  }

  /**
   * Get creditorAccount
   * @return creditorAccount
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public AccountReference getCreditorAccount() {
    return creditorAccount;
  }

  public void setCreditorAccount(AccountReference creditorAccount) {
    this.creditorAccount = creditorAccount;
  }

  public PaymentInitiationJson creditorAgent(String creditorAgent) {
    this.creditorAgent = creditorAgent;
    return this;
  }

  /**
   * BICFI
   * @return creditorAgent
   **/
  @Schema(example = "AAAADEBBXXX", description = "BICFI ")

  @Pattern(regexp="[A-Z]{6,6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3,3}){0,1}")   public String getCreditorAgent() {
    return creditorAgent;
  }

  public void setCreditorAgent(String creditorAgent) {
    this.creditorAgent = creditorAgent;
  }

  public PaymentInitiationJson creditorAgentName(String creditorAgentName) {
    this.creditorAgentName = creditorAgentName;
    return this;
  }

  /**
   * Creditor agent name.
   * @return creditorAgentName
   **/
  @Schema(example = "Creditor Agent Name", description = "Creditor agent name.")

  @Size(max=140)   public String getCreditorAgentName() {
    return creditorAgentName;
  }

  public void setCreditorAgentName(String creditorAgentName) {
    this.creditorAgentName = creditorAgentName;
  }

  public PaymentInitiationJson creditorName(String creditorName) {
    this.creditorName = creditorName;
    return this;
  }

  /**
   * Creditor name.
   * @return creditorName
   **/
  @Schema(example = "Creditor Name", required = true, description = "Creditor name.")
      @NotNull

  @Size(max=70)   public String getCreditorName() {
    return creditorName;
  }

  public void setCreditorName(String creditorName) {
    this.creditorName = creditorName;
  }

  public PaymentInitiationJson creditorAddress(Address creditorAddress) {
    this.creditorAddress = creditorAddress;
    return this;
  }

  /**
   * Get creditorAddress
   * @return creditorAddress
   **/
  @Schema(description = "")

    @Valid
    public Address getCreditorAddress() {
    return creditorAddress;
  }

  public void setCreditorAddress(Address creditorAddress) {
    this.creditorAddress = creditorAddress;
  }

  public PaymentInitiationJson creditorId(String creditorId) {
    this.creditorId = creditorId;
    return this;
  }

  /**
   * Identification of Creditors, e.g. a SEPA Creditor ID.
   * @return creditorId
   **/
  @Schema(description = "Identification of Creditors, e.g. a SEPA Creditor ID.")

  @Size(max=35)   public String getCreditorId() {
    return creditorId;
  }

  public void setCreditorId(String creditorId) {
    this.creditorId = creditorId;
  }

  public PaymentInitiationJson ultimateCreditor(String ultimateCreditor) {
    this.ultimateCreditor = ultimateCreditor;
    return this;
  }

  /**
   * Ultimate creditor.
   * @return ultimateCreditor
   **/
  @Schema(example = "Ultimate Creditor", description = "Ultimate creditor.")

  @Size(max=70)   public String getUltimateCreditor() {
    return ultimateCreditor;
  }

  public void setUltimateCreditor(String ultimateCreditor) {
    this.ultimateCreditor = ultimateCreditor;
  }

  public PaymentInitiationJson purposeCode(PurposeCode purposeCode) {
    this.purposeCode = purposeCode;
    return this;
  }

  /**
   * Get purposeCode
   * @return purposeCode
   **/
  @Schema(description = "")

    @Valid
    public PurposeCode getPurposeCode() {
    return purposeCode;
  }

  public void setPurposeCode(PurposeCode purposeCode) {
    this.purposeCode = purposeCode;
  }

  public PaymentInitiationJson chargeBearer(ChargeBearer chargeBearer) {
    this.chargeBearer = chargeBearer;
    return this;
  }

  /**
   * Get chargeBearer
   * @return chargeBearer
   **/
  @Schema(description = "")

    @Valid
    public ChargeBearer getChargeBearer() {
    return chargeBearer;
  }

  public void setChargeBearer(ChargeBearer chargeBearer) {
    this.chargeBearer = chargeBearer;
  }

  public PaymentInitiationJson remittanceInformationUnstructured(String remittanceInformationUnstructured) {
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    return this;
  }

  /**
   * Unstructured remittance information.
   * @return remittanceInformationUnstructured
   **/
  @Schema(example = "Ref Number Merchant", description = "Unstructured remittance information. ")

  @Size(max=140)   public String getRemittanceInformationUnstructured() {
    return remittanceInformationUnstructured;
  }

  public void setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
  }

  public PaymentInitiationJson remittanceInformationUnstructuredArray(RemittanceInformationUnstructuredArray remittanceInformationUnstructuredArray) {
    this.remittanceInformationUnstructuredArray = remittanceInformationUnstructuredArray;
    return this;
  }

  /**
   * Get remittanceInformationUnstructuredArray
   * @return remittanceInformationUnstructuredArray
   **/
  @Schema(description = "")

    @Valid
    public RemittanceInformationUnstructuredArray getRemittanceInformationUnstructuredArray() {
    return remittanceInformationUnstructuredArray;
  }

  public void setRemittanceInformationUnstructuredArray(RemittanceInformationUnstructuredArray remittanceInformationUnstructuredArray) {
    this.remittanceInformationUnstructuredArray = remittanceInformationUnstructuredArray;
  }

  public PaymentInitiationJson remittanceInformationStructured(RemittanceInformationStructuredMax140 remittanceInformationStructured) {
    this.remittanceInformationStructured = remittanceInformationStructured;
    return this;
  }

  /**
   * Get remittanceInformationStructured
   * @return remittanceInformationStructured
   **/
  @Schema(description = "")

    @Valid
    public RemittanceInformationStructuredMax140 getRemittanceInformationStructured() {
    return remittanceInformationStructured;
  }

  public void setRemittanceInformationStructured(RemittanceInformationStructuredMax140 remittanceInformationStructured) {
    this.remittanceInformationStructured = remittanceInformationStructured;
  }

  public PaymentInitiationJson remittanceInformationStructuredArray(RemittanceInformationStructuredArray remittanceInformationStructuredArray) {
    this.remittanceInformationStructuredArray = remittanceInformationStructuredArray;
    return this;
  }

  /**
   * Get remittanceInformationStructuredArray
   * @return remittanceInformationStructuredArray
   **/
  @Schema(description = "")

    @Valid
    public RemittanceInformationStructuredArray getRemittanceInformationStructuredArray() {
    return remittanceInformationStructuredArray;
  }

  public void setRemittanceInformationStructuredArray(RemittanceInformationStructuredArray remittanceInformationStructuredArray) {
    this.remittanceInformationStructuredArray = remittanceInformationStructuredArray;
  }

  public PaymentInitiationJson requestedExecutionDate(LocalDate requestedExecutionDate) {
    this.requestedExecutionDate = requestedExecutionDate;
    return this;
  }

  /**
   * Get requestedExecutionDate
   * @return requestedExecutionDate
   **/
  @Schema(description = "")

    @Valid
    public LocalDate getRequestedExecutionDate() {
    return requestedExecutionDate;
  }

  public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
    this.requestedExecutionDate = requestedExecutionDate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentInitiationJson paymentInitiationJson = (PaymentInitiationJson) o;
    return Objects.equals(this.endToEndIdentification, paymentInitiationJson.endToEndIdentification) &&
        Objects.equals(this.instructionIdentification, paymentInitiationJson.instructionIdentification) &&
        Objects.equals(this.debtorName, paymentInitiationJson.debtorName) &&
        Objects.equals(this.debtorAccount, paymentInitiationJson.debtorAccount) &&
        Objects.equals(this.ultimateDebtor, paymentInitiationJson.ultimateDebtor) &&
        Objects.equals(this.instructedAmount, paymentInitiationJson.instructedAmount) &&
        Objects.equals(this.creditorAccount, paymentInitiationJson.creditorAccount) &&
        Objects.equals(this.creditorAgent, paymentInitiationJson.creditorAgent) &&
        Objects.equals(this.creditorAgentName, paymentInitiationJson.creditorAgentName) &&
        Objects.equals(this.creditorName, paymentInitiationJson.creditorName) &&
        Objects.equals(this.creditorAddress, paymentInitiationJson.creditorAddress) &&
        Objects.equals(this.creditorId, paymentInitiationJson.creditorId) &&
        Objects.equals(this.ultimateCreditor, paymentInitiationJson.ultimateCreditor) &&
        Objects.equals(this.purposeCode, paymentInitiationJson.purposeCode) &&
        Objects.equals(this.chargeBearer, paymentInitiationJson.chargeBearer) &&
        Objects.equals(this.remittanceInformationUnstructured, paymentInitiationJson.remittanceInformationUnstructured) &&
        Objects.equals(this.remittanceInformationUnstructuredArray, paymentInitiationJson.remittanceInformationUnstructuredArray) &&
        Objects.equals(this.remittanceInformationStructured, paymentInitiationJson.remittanceInformationStructured) &&
        Objects.equals(this.remittanceInformationStructuredArray, paymentInitiationJson.remittanceInformationStructuredArray) &&
        Objects.equals(this.requestedExecutionDate, paymentInitiationJson.requestedExecutionDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(endToEndIdentification, instructionIdentification, debtorName, debtorAccount, ultimateDebtor, instructedAmount, creditorAccount, creditorAgent, creditorAgentName, creditorName, creditorAddress, creditorId, ultimateCreditor, purposeCode, chargeBearer, remittanceInformationUnstructured, remittanceInformationUnstructuredArray, remittanceInformationStructured, remittanceInformationStructuredArray, requestedExecutionDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentInitiationJson {\n");

    sb.append("    endToEndIdentification: ").append(toIndentedString(endToEndIdentification)).append("\n");
    sb.append("    instructionIdentification: ").append(toIndentedString(instructionIdentification)).append("\n");
    sb.append("    debtorName: ").append(toIndentedString(debtorName)).append("\n");
    sb.append("    debtorAccount: ").append(toIndentedString(debtorAccount)).append("\n");
    sb.append("    ultimateDebtor: ").append(toIndentedString(ultimateDebtor)).append("\n");
    sb.append("    instructedAmount: ").append(toIndentedString(instructedAmount)).append("\n");
    sb.append("    creditorAccount: ").append(toIndentedString(creditorAccount)).append("\n");
    sb.append("    creditorAgent: ").append(toIndentedString(creditorAgent)).append("\n");
    sb.append("    creditorAgentName: ").append(toIndentedString(creditorAgentName)).append("\n");
    sb.append("    creditorName: ").append(toIndentedString(creditorName)).append("\n");
    sb.append("    creditorAddress: ").append(toIndentedString(creditorAddress)).append("\n");
    sb.append("    creditorId: ").append(toIndentedString(creditorId)).append("\n");
    sb.append("    ultimateCreditor: ").append(toIndentedString(ultimateCreditor)).append("\n");
    sb.append("    purposeCode: ").append(toIndentedString(purposeCode)).append("\n");
    sb.append("    chargeBearer: ").append(toIndentedString(chargeBearer)).append("\n");
    sb.append("    remittanceInformationUnstructured: ").append(toIndentedString(remittanceInformationUnstructured)).append("\n");
    sb.append("    remittanceInformationUnstructuredArray: ").append(toIndentedString(remittanceInformationUnstructuredArray)).append("\n");
    sb.append("    remittanceInformationStructured: ").append(toIndentedString(remittanceInformationStructured)).append("\n");
    sb.append("    remittanceInformationStructuredArray: ").append(toIndentedString(remittanceInformationStructuredArray)).append("\n");
    sb.append("    requestedExecutionDate: ").append(toIndentedString(requestedExecutionDate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
