/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.psd2.xs2a.web.controller.psd2;

import de.adorsys.psd2.api.ConsentApi;
import de.adorsys.psd2.core.data.ais.AisConsent;
import de.adorsys.psd2.model.Consents;
import de.adorsys.psd2.xs2a.core.psu.AdditionalPsuIdData;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppNotificationData;
import de.adorsys.psd2.xs2a.core.tpp.TppRedirectUri;
import de.adorsys.psd2.xs2a.domain.HrefType;
import de.adorsys.psd2.xs2a.domain.NotificationModeResponseHeaders;
import de.adorsys.psd2.xs2a.domain.ResponseObject;
import de.adorsys.psd2.xs2a.domain.authorisation.AuthorisationResponse;
import de.adorsys.psd2.xs2a.domain.consent.*;
import de.adorsys.psd2.xs2a.service.ConsentService;
import de.adorsys.psd2.xs2a.service.NotificationSupportedModeService;
import de.adorsys.psd2.xs2a.service.RequestProviderService;
import de.adorsys.psd2.xs2a.service.mapper.ResponseMapper;
import de.adorsys.psd2.xs2a.service.mapper.psd2.ResponseErrorMapper;
import de.adorsys.psd2.xs2a.web.header.ConsentHeadersBuilder;
import de.adorsys.psd2.xs2a.web.header.ResponseHeaders;
import de.adorsys.psd2.xs2a.web.mapper.AuthorisationMapper;
import de.adorsys.psd2.xs2a.web.mapper.ConsentModelMapper;
import de.adorsys.psd2.xs2a.web.mapper.TppRedirectUriMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unchecked") // This class implements autogenerated interface without proper return values generated
@Slf4j
@RestController
@AllArgsConstructor
public class ConsentController implements ConsentApi {
    private final ConsentService consentService;
    private final ResponseMapper responseMapper;
    private final ConsentModelMapper consentModelMapper;
    private final AuthorisationMapper authorisationMapper;
    private final TppRedirectUriMapper tppRedirectUriMapper;
    private final ResponseErrorMapper responseErrorMapper;
    private final ConsentHeadersBuilder consentHeadersBuilder;
    private final NotificationSupportedModeService notificationSupportedModeService;
    private final RequestProviderService requestProviderService;

    @Override
    public ResponseEntity createConsent(UUID xRequestID, String psuIpAddress, Consents body, String digest, String signature,
                                        byte[] tppSignatureCertificate, String psuId, String psuIdType, String psuCorporateId,
                                        String psuCorporateIdType, Boolean tppRedirectPreferred, Boolean tppDecoupledPreferred,
                                        String tppRedirectUri, String tppNokRedirectUri,
                                        Boolean tppExplicitAuthorisationPreferred, String tppBrandLoggingInformation,
                                        String tppNotificationUri, String tppNotificationContentPreferred, String psuIpPort,
                                        String psuAccept, String psuAcceptCharset, String psuAcceptEncoding,
                                        String psuAcceptLanguage, String psuUserAgent, String psuHttpMethod,
                                        UUID psuDeviceId, String psuGeoLocation) {
        TppRedirectUri xs2aTppRedirectUri = tppRedirectUriMapper.mapToTppRedirectUri(tppRedirectUri, tppNokRedirectUri);
        TppNotificationData tppNotificationData = notificationSupportedModeService.getTppNotificationData(tppNotificationContentPreferred, tppNotificationUri);

        CreateConsentReq createConsent = consentModelMapper.mapToCreateConsentReq(body, xs2aTppRedirectUri, tppNotificationData, tppBrandLoggingInformation,
                                                                                  requestProviderService.getInstanceId());

        PsuIdData psuData = new PsuIdData(psuId, psuIdType, psuCorporateId, psuCorporateIdType, psuIpAddress,
                                          new AdditionalPsuIdData(psuIpPort, psuUserAgent, psuGeoLocation, psuAccept, psuAcceptCharset, psuAcceptEncoding, psuAcceptLanguage, psuHttpMethod, psuDeviceId));

        ResponseObject<CreateConsentResponse> createResponse =
            consentService.createAccountConsentsWithResponse(createConsent, psuData, BooleanUtils.isTrue(tppExplicitAuthorisationPreferred));

        if (createResponse.hasError()) {
            return responseErrorMapper.generateErrorResponse(createResponse.getError());
        }

        CreateConsentResponse createConsentResponse = createResponse.getBody();
        NotificationModeResponseHeaders notificationHeaders = notificationSupportedModeService.resolveNotificationHeaders(createConsentResponse.getTppNotificationContentPreferred());

        ResponseHeaders headers = consentHeadersBuilder.buildCreateConsentHeaders(createConsentResponse.getAuthorizationId(),
                                                                                  Optional.ofNullable(createConsentResponse.getLinks().getSelf())
                                                                                      .map(HrefType::getHref)
                                                                                      .orElseThrow(() -> new IllegalArgumentException("Wrong href type in self link")),
                                                                                  notificationHeaders);

        return responseMapper.created(createResponse, consentModelMapper::mapToConsentsResponse201, headers);
    }

    @Override
    public ResponseEntity getConsentStatus(String consentId, UUID xRequestID, String digest, String signature,
                                           byte[] tpPSignatureCertificate, String psUIPAddress, String psUIPPort,
                                           String psUAccept, String psUAcceptCharset, String psUAcceptEncoding,
                                           String psUAcceptLanguage, String psUUserAgent, String psUHttpMethod,
                                           UUID psUDeviceID, String psUGeoLocation) {

        ResponseObject<ConsentStatusResponse> accountConsentsStatusByIdResponse = consentService.getAccountConsentsStatusById(consentId);
        return accountConsentsStatusByIdResponse.hasError()
                   ? responseErrorMapper.generateErrorResponse(accountConsentsStatusByIdResponse.getError())
                   : responseMapper.ok(accountConsentsStatusByIdResponse, consentModelMapper::mapToConsentStatusResponse200);
    }

    @Override
    public ResponseEntity startConsentAuthorisation(UUID xRequestID, String consentId, Object body,
                                                    String digest, String signature, byte[] tpPSignatureCertificate,
                                                    String psuId, String psUIDType, String psUCorporateID,
                                                    String psUCorporateIDType, Boolean tppRedirectPreferred, Boolean tppDecoupledPreferred,
                                                    String tpPRedirectURI, String tpPNokRedirectURI,
                                                    String tpPNotificationURI, String tpPNotificationContentPreferred,
                                                    String psUIPAddress, String psUIPPort, String psUAccept,
                                                    String psUAcceptCharset, String psUAcceptEncoding,
                                                    String psUAcceptLanguage, String psUUserAgent,
                                                    String psUHttpMethod, UUID psUDeviceID,
                                                    String psUGeoLocation) {
        PsuIdData psuData = new PsuIdData(psuId, psUIDType, psUCorporateID, psUCorporateIDType, psUIPAddress);

        String password = authorisationMapper.mapToPasswordFromBody((Map) body);

        ResponseObject<AuthorisationResponse> createResponse = consentService.createAisAuthorisation(psuData, consentId, password);

        if (createResponse.hasError()) {
            return responseErrorMapper.generateErrorResponse(createResponse.getError());
        }

        AuthorisationResponse authorisationResponse = createResponse.getBody();
        ResponseHeaders responseHeaders = consentHeadersBuilder.buildStartAuthorisationHeaders(authorisationResponse.getAuthorisationId());

        return responseMapper.created(ResponseObject.builder()
                                          .body(authorisationMapper.mapToConsentCreateOrUpdateAuthorisationResponse(createResponse))
                                          .build(),
                                      responseHeaders);
    }

    @Override
    public ResponseEntity updateConsentsPsuData(UUID xRequestID, String consentId, String authorisationId, Object body, String digest,
                                                String signature, byte[] tpPSignatureCertificate, String psuId, String psUIDType,
                                                String psUCorporateID, String psUCorporateIDType, String psUIPAddress, String psUIPPort,
                                                String psUAccept, String psUAcceptCharset, String psUAcceptEncoding, String psUAcceptLanguage,
                                                String psUUserAgent, String psUHttpMethod, UUID psUDeviceID, String psUGeoLocation) {

        PsuIdData psuData = new PsuIdData(psuId, psUIDType, psUCorporateID, psUCorporateIDType, psUIPAddress);

        return updateAisAuthorisation(psuData, authorisationId, consentId, body);
    }

    private ResponseEntity<Object> updateAisAuthorisation(PsuIdData psuData, String authorisationId, String consentId, Object body) {
        ConsentAuthorisationsParameters updatePsuDataRequest = consentModelMapper.mapToUpdatePsuData(psuData, consentId, authorisationId, (Map) body);
        ResponseObject<UpdateConsentPsuDataResponse> updateConsentPsuDataResponse = consentService.updateConsentPsuData(updatePsuDataRequest);

        if (updateConsentPsuDataResponse.hasError()) {
            return responseErrorMapper.generateErrorResponse(updateConsentPsuDataResponse.getError());
        }

        ResponseHeaders responseHeaders = consentHeadersBuilder.buildUpdatePsuDataHeaders(authorisationId);

        return responseMapper.ok(updateConsentPsuDataResponse, authorisationMapper::mapToConsentUpdatePsuAuthenticationResponse, responseHeaders);
    }

    @Override
    public ResponseEntity getConsentScaStatus(String consentId, String authorisationId, UUID xRequestID, String digest,
                                              String signature, byte[] tpPSignatureCertificate, String psUIPAddress, String psUIPPort,
                                              String psUAccept, String psUAcceptCharset, String psUAcceptEncoding,
                                              String psUAcceptLanguage, String psUUserAgent, String psUHttpMethod, UUID psUDeviceID,
                                              String psUGeoLocation) {

        ResponseObject<Xs2aScaStatusResponse> consentAuthorisationScaStatusResponse = consentService.getConsentAuthorisationScaStatus(consentId, authorisationId);
        return consentAuthorisationScaStatusResponse.hasError()
                   ? responseErrorMapper.generateErrorResponse(consentAuthorisationScaStatusResponse.getError())
                   : responseMapper.ok(consentAuthorisationScaStatusResponse, authorisationMapper::mapToScaStatusResponse);
    }

    @Override
    public ResponseEntity getConsentAuthorisation(String consentId, UUID xRequestID, String digest, String signature, byte[] tpPSignatureCertificate,
                                                  String psUIPAddress, String psUIPPort, String psUAccept, String psUAcceptCharset, String psUAcceptEncoding,
                                                  String psUAcceptLanguage, String psUUserAgent, String psUHttpMethod, UUID psUDeviceID,
                                                  String psUGeoLocation) {
        ResponseObject<Xs2aAuthorisationSubResources> consentInitiationAuthorisationsResponse = consentService.getConsentInitiationAuthorisations(consentId);
        return consentInitiationAuthorisationsResponse.hasError()
                   ? responseErrorMapper.generateErrorResponse(consentInitiationAuthorisationsResponse.getError())
                   : responseMapper.ok(consentInitiationAuthorisationsResponse, authorisationMapper::mapToAuthorisations);
    }

    @Override
    public ResponseEntity getConsentInformation(String consentId, UUID xRequestID, String digest, String signature,
                                                byte[] tpPSignatureCertificate, String psUIPAddress, String psUIPPort,
                                                String psUAccept, String psUAcceptCharset, String psUAcceptEncoding,
                                                String psUAcceptLanguage, String psUUserAgent, String psUHttpMethod,
                                                UUID psUDeviceID, String psUGeoLocation) {
        ResponseObject<AisConsent> accountConsentByIdResponse = consentService.getAccountConsentById(consentId);
        return accountConsentByIdResponse.hasError()
                   ? responseErrorMapper.generateErrorResponse(accountConsentByIdResponse.getError())
                   : responseMapper.ok(accountConsentByIdResponse, consentModelMapper::mapToConsentInformationResponse200Json);
    }

    @Override
    public ResponseEntity deleteConsent(String consentId, UUID xRequestID, String digest, String signature, byte[] tpPSignatureCertificate,
                                        String psUIPAddress, String psUIPPort, String psUAccept, String psUAcceptCharset, String psUAcceptEncoding,
                                        String psUAcceptLanguage, String psUUserAgent, String psUHttpMethod, UUID psUDeviceID, String psUGeoLocation) {

        ResponseObject<Void> response = consentService.deleteAccountConsentsById(consentId);
        return response.hasError()
                   ? responseErrorMapper.generateErrorResponse(response.getError())
                   : responseMapper.delete(response);
    }
}
