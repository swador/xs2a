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

package de.adorsys.psd2.xs2a.web.validator.body.piis.v2;

import de.adorsys.psd2.mapper.Xs2aObjectMapper;
import de.adorsys.psd2.model.ConsentsConfirmationOfFunds;
import de.adorsys.psd2.xs2a.core.error.ErrorType;
import de.adorsys.psd2.xs2a.core.error.MessageError;
import de.adorsys.psd2.xs2a.core.error.MessageErrorCode;
import de.adorsys.psd2.xs2a.web.validator.ErrorBuildingService;
import de.adorsys.psd2.xs2a.web.validator.body.AccountReferenceValidator;
import de.adorsys.psd2.xs2a.web.validator.body.FieldLengthValidator;
import de.adorsys.psd2.xs2a.web.validator.body.raw.FieldExtractor;
import de.adorsys.psd2.xs2a.web.validator.header.ErrorBuildingServiceMock;
import de.adorsys.xs2a.reader.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationOfFundsConsentBodyValidatorImplTest {
    private final MessageError messageError = new MessageError(ErrorType.PIIS_400);

    @Mock
    private Xs2aObjectMapper xs2aObjectMapper;
    @Mock
    private AccountReferenceValidator accountReferenceValidator;
    @Mock
    private FieldExtractor fieldExtractor;

    private ConfirmationOfFundsConsentBodyValidatorImpl validator;
    private final JsonReader jsonReader = new JsonReader();

    @BeforeEach
    void setUp() {
        ErrorBuildingService errorBuildingServiceMock = new ErrorBuildingServiceMock(ErrorType.PIIS_400);
        validator =
            new ConfirmationOfFundsConsentBodyValidatorImpl(errorBuildingServiceMock, xs2aObjectMapper,
                                                            accountReferenceValidator, fieldExtractor,
                                                            new FieldLengthValidator(errorBuildingServiceMock));
    }

    @Test
    void validate_success() {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        ConsentsConfirmationOfFunds confirmationOfFunds = jsonReader.getObjectFromFile("json/piis/create-piis-consent.json", ConsentsConfirmationOfFunds.class);

        when(fieldExtractor.mapBodyToInstance(mockRequest, this.messageError, ConsentsConfirmationOfFunds.class))
            .thenReturn(Optional.of(confirmationOfFunds));

        //When
        validator.validate(mockRequest, messageError);

        //Then
        assertTrue(messageError.getTppMessages().isEmpty());
    }

    @Test
    void validate_invalidBody() {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        ConsentsConfirmationOfFunds confirmationOfFunds = jsonReader.getObjectFromFile("json/piis/create-piis-consent.json", ConsentsConfirmationOfFunds.class);

        when(fieldExtractor.mapBodyToInstance(mockRequest, this.messageError, ConsentsConfirmationOfFunds.class))
            .thenReturn(Optional.empty());

        //When
        validator.validate(mockRequest, messageError);

        //Then
        assertTrue(messageError.getTppMessages().isEmpty());
    }

    @Test
    void validate_nullAccount() {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        ConsentsConfirmationOfFunds confirmationOfFunds = jsonReader.getObjectFromFile("json/piis/create-piis-consent-null-account.json", ConsentsConfirmationOfFunds.class);

        when(fieldExtractor.mapBodyToInstance(mockRequest, this.messageError, ConsentsConfirmationOfFunds.class))
            .thenReturn(Optional.of(confirmationOfFunds));

        //When
        validator.validate(mockRequest, messageError);

        //Then
        assertFalse(messageError.getTppMessages().isEmpty());
        assertEquals(MessageErrorCode.FORMAT_ERROR_NULL_VALUE, messageError.getTppMessage().getMessageErrorCode());
    }

    @ParameterizedTest
    @MethodSource("params")
    void validate_oversizedField(String jsonPath, String fieldName, int size) {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        ConsentsConfirmationOfFunds confirmationOfFunds = jsonReader.getObjectFromFile(jsonPath, ConsentsConfirmationOfFunds.class);

        when(fieldExtractor.mapBodyToInstance(mockRequest, this.messageError, ConsentsConfirmationOfFunds.class))
            .thenReturn(Optional.of(confirmationOfFunds));

        //When
        validator.validate(mockRequest, messageError);

        //Then
        assertFalse(messageError.getTppMessages().isEmpty());
        assertEquals(MessageErrorCode.FORMAT_ERROR_OVERSIZE_FIELD, messageError.getTppMessage().getMessageErrorCode());
        assertArrayEquals(new Object[]{fieldName, size}, messageError.getTppMessage().getTextParameters());
    }

    private static Stream<Arguments> params() {
        return Stream.of(Arguments.arguments("json/piis/create-piis-consent-oversized-number.json", "cardNumber", 35),
                         Arguments.arguments("json/piis/create-piis-consent-oversized-info.json", "cardInformation", 140),
                         Arguments.arguments("json/piis/create-piis-consent-oversized-reg.json", "registrationInformation", 140)
        );
    }
}
