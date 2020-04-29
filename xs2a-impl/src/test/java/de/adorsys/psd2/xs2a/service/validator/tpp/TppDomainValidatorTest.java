/*
 * Copyright 2018-2020 adorsys GmbH & Co KG
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

package de.adorsys.psd2.xs2a.service.validator.tpp;

import de.adorsys.psd2.xs2a.core.domain.TppMessageInformation;
import de.adorsys.psd2.xs2a.core.error.ErrorType;
import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import de.adorsys.psd2.xs2a.core.profile.TppUriCompliance;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.service.ScaApproachResolver;
import de.adorsys.psd2.xs2a.service.TppService;
import de.adorsys.psd2.xs2a.service.profile.AspspProfileServiceWrapper;
import de.adorsys.psd2.xs2a.service.validator.ValidationResult;
import de.adorsys.psd2.xs2a.web.validator.ErrorBuildingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static de.adorsys.psd2.xs2a.core.error.MessageErrorCode.FORMAT_ERROR_INVALID_DOMAIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppDomainValidatorTest {
    private static final String INVALID_DOMAIN_MESSAGE = "TPP URIs are not compliant with the domain secured by the eIDAS QWAC certificate of the TPP in the field CN or SubjectAltName of the certificate";
    private static final String URL_HEADER_CORRECT = "www.example-TPP.com/xs2a-client/v1/ASPSPidentifcation/mytransaction-id";
    private static final String URL_HEADER_CORRECT_WITH_PAGE = "www.example-TPP.com/xs2a-client/super.html";
    private static final String URL_HEADER_CORRECT_DE = "www.example-TPP.de/xs2a-client/v1/ASPSPidentifcation/mytransaction-id";
    private static final String URL_HEADER_SUBDOMAIN_CORRECT = "redirections.example-TPP.com/xs2a-client/v1/ASPSPidentifcation/mytransaction-id";
    private static final String URL_HEADER_WRONG_DOMAIN = "www.bad-example-TPP.com/xs2a-client/v1/ASPSPidentifcation/mytransaction-id";
    private static final String URL_HEADER_WRONG_TLD = "www.example-TPP.bad/xs2a-client/v1/ASPSPidentifcation/mytransaction-id";
    private static final String URL_HEADER_WRONG = "example-TPP";

    private static final String TPP_NAME_DOMAIN = "www.example-TPP.com";
    private static final String TPP_NAME_NON_DOMAIN = "Some bank name";
    private static final String TPP_DNS_DOMAIN = "www.example-TPP.de";
    private static final String TPP_WILDCARD_DOMAIN = "*.example-TPP.de";
    private static final TppMessageInformation TPP_MESSAGE_INFORMATION = TppMessageInformation.buildWarning(INVALID_DOMAIN_MESSAGE);

    @InjectMocks
    private TppDomainValidator tppDomainValidator;
    @Mock
    private TppService tppService;
    @Mock
    private AspspProfileServiceWrapper aspspProfileServiceWrapper;
    @Mock
    private ErrorBuildingService errorBuildingService;
    @Mock
    private ScaApproachResolver scaApproachResolver;

    @Test
    void validate_valid_warningMode() {
        //When
        ValidationResult actualResult =  tppDomainValidator.validate(URL_HEADER_WRONG);

        //Then
        assertEquals(ValidationResult.valid(), actualResult);
    }

    @Test
    void validate_valid() {
        //When
        ValidationResult actualResult =  tppDomainValidator.validate("");

        //Then
        assertEquals(ValidationResult.valid(), actualResult);
    }

    @Test
    void validate_valid_rejectMode() {
        //Given
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);
        when(scaApproachResolver.resolveScaApproach()).thenReturn(ScaApproach.REDIRECT);
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.getTppUriComplianceResponse()).thenReturn(TppUriCompliance.REJECT);

        //When
        ValidationResult actualResult =  tppDomainValidator.validate(URL_HEADER_CORRECT);

        //Then
        assertEquals(ValidationResult.valid(), actualResult);
    }

    @Test
    void validate_valid_rejectMode_emptyCertificateValues() {
        //Given
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);
        when(scaApproachResolver.resolveScaApproach()).thenReturn(ScaApproach.REDIRECT);
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(null, null));
        when(aspspProfileServiceWrapper.getTppUriComplianceResponse()).thenReturn(TppUriCompliance.REJECT);

        //When
        ValidationResult actualResult =  tppDomainValidator.validate(URL_HEADER_CORRECT);

        //Then
        assertEquals(ValidationResult.valid(), actualResult);
    }

    @Test
    void validate_invalid() {
        //Given
        ValidationResult expectedResult = buildInvalidResult();

        when(scaApproachResolver.resolveScaApproach()).thenReturn(ScaApproach.REDIRECT);
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.getTppUriComplianceResponse()).thenReturn(TppUriCompliance.REJECT);
        when(errorBuildingService.buildErrorType()).thenReturn(ErrorType.PIS_400);
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        ValidationResult actualResult =  tppDomainValidator.validate(URL_HEADER_WRONG);

        //Then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void buildWarningMessages_valid() {
        //Given
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(false);
        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_WRONG_DOMAIN);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_NoHeader_Valid() {
        //Given
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);
        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages("");
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_ScaEmbedded_Valid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(null, null));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);
        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_WRONG_DOMAIN);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_NoDomainsInTpp_Valid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(null, null));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);
        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_CORRECT);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_NotCorrectDomainsInTpp_Valid() {
        //Given
        when(tppService.getTppInfo())
            .thenReturn(buildTppInfo("example-TPP", "dns-example-TPP"));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);
        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_CORRECT);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_UrlHeaderCorrect_Valid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_CORRECT);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_UrlHeaderCorrect_withPage_Valid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_CORRECT_WITH_PAGE);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_UrlHeaderSubdomainCorrect_Valid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_SUBDOMAIN_CORRECT);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_UrlHeaderSubdomainCorrectTppWildCardDomain_Valid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_WILDCARD_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_SUBDOMAIN_CORRECT);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_UrlHeaderWrong_Invalid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_WRONG);
        //Then
        assertFalse(validate.isEmpty());
        assertEquals(TPP_MESSAGE_INFORMATION, validate.iterator().next());
    }

    @Test
    void buildWarningMessages_UrlHeaderWrongDomain_Invalid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_WRONG_DOMAIN);
        //Then
        assertFalse(validate.isEmpty());
        assertEquals(TPP_MESSAGE_INFORMATION, validate.iterator().next());
    }

    @Test
    void buildWarningMessages_UrlHeaderWrongTld_Invalid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_WRONG_TLD);
        //Then
        assertFalse(validate.isEmpty());
        assertEquals(TPP_MESSAGE_INFORMATION, validate.iterator().next());
    }

    @Test
    void buildWarningMessages_nonDomainTppName_valid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_NON_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_CORRECT_DE);
        //Then
        assertTrue(validate.isEmpty());
    }

    @Test
    void buildWarningMessages_nonDomainTppName_invalid() {
        //Given
        when(tppService.getTppInfo()).thenReturn(buildTppInfo(TPP_NAME_NON_DOMAIN, TPP_DNS_DOMAIN));
        when(aspspProfileServiceWrapper.isCheckUriComplianceToDomainSupported()).thenReturn(true);

        //When
        Set<TppMessageInformation> validate = tppDomainValidator.buildWarningMessages(URL_HEADER_WRONG_DOMAIN);
        //Then
        assertFalse(validate.isEmpty());
        assertEquals(TPP_MESSAGE_INFORMATION, validate.iterator().next());
    }

    private TppInfo buildTppInfo(String name, String dns) {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setTppName(name);
        List<String> dnsList = dns == null
                                   ? Collections.emptyList()
                                   : Collections.singletonList(dns);
        tppInfo.setDnsList(dnsList);

        return tppInfo;
    }

    private ValidationResult buildInvalidResult() {
        return ValidationResult.invalid(
            ErrorType.PIS_400, TppMessageInformation.of(FORMAT_ERROR_INVALID_DOMAIN));
    }
}
