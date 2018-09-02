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

package de.adorsys.aspsp.xs2a.service.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.aspsp.xs2a.domain.account.AccountReference;
import de.adorsys.aspsp.xs2a.domain.consent.AccountAccessType;
import de.adorsys.aspsp.xs2a.domain.consent.AccountConsent;
import de.adorsys.aspsp.xs2a.domain.consent.ConsentStatusResponse;
import de.adorsys.aspsp.xs2a.domain.consent.CreateConsentAuthorizationResponse;
import de.adorsys.aspsp.xs2a.domain.consent.CreateConsentReq;
import de.adorsys.aspsp.xs2a.domain.consent.CreateConsentResponse;
import de.adorsys.aspsp.xs2a.domain.consent.UpdateConsentPsuDataReq;
import de.adorsys.aspsp.xs2a.domain.consent.UpdateConsentPsuDataResponse;
import de.adorsys.psd2.api.ConsentApi;
import de.adorsys.psd2.model.AccountAccess;
import de.adorsys.psd2.model.AuthenticationObject;
import de.adorsys.psd2.model.AuthenticationType;
import de.adorsys.psd2.model.ConsentInformationResponse200Json;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentStatusResponse200;
import de.adorsys.psd2.model.Consents;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.ScaMethods;
import de.adorsys.psd2.model.StartScaprocessResponse;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;

public class ConsentModelMapper {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.instance();

    public static CreateConsentReq mapToCreateConsentReq(Consents consent) {
        return Optional.ofNullable(consent)
            .map(cnst -> {
                CreateConsentReq createAisConsentRequest = new CreateConsentReq();
                createAisConsentRequest.setAccess(mapToAccountAccessInner(cnst.getAccess()));
                createAisConsentRequest.setRecurringIndicator(cnst.getRecurringIndicator());
                createAisConsentRequest.setValidUntil(cnst.getValidUntil());
                createAisConsentRequest.setFrequencyPerDay(cnst.getFrequencyPerDay());
                createAisConsentRequest.setCombinedServiceIndicator(BooleanUtils.toBoolean(cnst.isCombinedServiceIndicator()));
                return createAisConsentRequest;
            })
            .orElse(null);
    }

    public static ConsentStatusResponse200 mapToConsentStatusResponse200(ConsentStatusResponse consentStatusResponse) {
        return Optional.ofNullable(consentStatusResponse)
            .map(cstr -> new ConsentStatusResponse200().consentStatus(ConsentStatus.fromValue(cstr.getConsentStatus())))
            .orElse(null);
    }

    public static StartScaprocessResponse mapToStartScaprocessResponse(CreateConsentAuthorizationResponse createConsentAuthorizationResponse) {
        return Optional.ofNullable(createConsentAuthorizationResponse)
            .map(csar -> {
                StartScaprocessResponse response = new StartScaprocessResponse().scaStatus(createConsentAuthorizationResponse.getScaStatus())
                    ._links(new HashMap());

                ControllerLinkBuilder link = linkTo(methodOn(ConsentApi.class)._updateConsentsPsuData(csar.getConsentId(), csar.getAuthorizationId(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
                response.getLinks().put(csar.getResponseLinkType().toString(), link.toString());

                return response;
            })
            .orElse(null);
    }

    public static UpdatePsuAuthenticationResponse mapToUpdatePsuAuthenticationResponse(UpdateConsentPsuDataResponse response) {
        return new UpdatePsuAuthenticationResponse();
    }

    public static ConsentsResponse201 mapToConsentsResponse201(CreateConsentResponse createConsentResponse) {
        return Optional.ofNullable(createConsentResponse)
            .map(cnst ->
                new ConsentsResponse201()
                    .consentStatus(ConsentStatus.fromValue(cnst.getConsentStatus()))
                    .consentId(cnst.getConsentId())
                    .scaMethods(mapToScaMethodsOuter(cnst))
                    ._links(OBJECT_MAPPER.convertValue(cnst.getLinks(), Map.class))
                    .message(cnst.getPsuMessage())
            )
            .orElse(null);
    }

    public static ConsentInformationResponse200Json mapToConsentInformationResponse200Json(AccountConsent accountConsent) {
        return Optional.ofNullable(accountConsent)
            .map(consent ->
                new ConsentInformationResponse200Json()
                    .access(mapToAccountAccessDomain(consent.getAccess()))
                    .recurringIndicator(consent.isRecurringIndicator())
                    .validUntil(consent.getValidUntil())
                    .frequencyPerDay(consent.getFrequencyPerDay())
                    .lastActionDate(consent.getLastActionDate())
                    .consentStatus(ConsentStatus.fromValue(consent.getConsentStatus().getValue()))
            )
            .orElse(null);
    }

    private static ScaMethods mapToScaMethodsOuter(CreateConsentResponse createConsentResponse) {
        List<AuthenticationObject> authList = Optional.ofNullable(createConsentResponse.getScaMethods())
            .map(arr -> Arrays.stream(createConsentResponse.getScaMethods())
                .map(au -> new AuthenticationObject()
                    .authenticationType(AuthenticationType.fromValue(au.getAuthenticationType().getDescription()))
                    .authenticationVersion(au.getAuthenticationVersion())
                    .authenticationMethodId(au.getAuthenticationMethodId())
                    .name(au.getName())
                    .explanation(au.getExplanation()))
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
        ScaMethods scaMethods = new ScaMethods();
        scaMethods.addAll(authList);

        return scaMethods;
    }

    private static de.adorsys.aspsp.xs2a.domain.consent.AccountAccess mapToAccountAccessInner(AccountAccess accountAccess) {
        return Optional.ofNullable(accountAccess)
            .map(acs ->
                new de.adorsys.aspsp.xs2a.domain.consent.AccountAccess(
                    mapToAccountReferencesInner(acs.getAccounts()),
                    mapToAccountReferencesInner(acs.getBalances()),
                    mapToAccountReferencesInner(acs.getTransactions()),
                    mapToAccountAccessTypeFromAvailableAccounts(acs.getAvailableAccounts()),
                    mapToAccountAccessTypeFromAllPsd2Enum(acs.getAllPsd2())
                ))
            .orElse(null);
    }

    private static AccountAccess mapToAccountAccessDomain(de.adorsys.aspsp.xs2a.domain.consent.AccountAccess accountAccess) {
        return Optional.ofNullable(accountAccess)
            .map(access -> {
                    AccountAccess mappedAccountAccess = new AccountAccess();

                    mappedAccountAccess.setAccounts(new ArrayList<>(access.getAccounts()));
                    mappedAccountAccess.setBalances(new ArrayList<>(access.getBalances()));
                    mappedAccountAccess.setTransactions(new ArrayList<>(access.getTransactions()));
                    mappedAccountAccess.setAvailableAccounts(
                        AccountAccess.AvailableAccountsEnum.fromValue(
                            Optional.ofNullable(access.getAvailableAccounts())
                                .map(AccountAccessType::getDescription)
                                .orElse(null)
                        )
                    );
                    mappedAccountAccess.setAllPsd2(
                        AccountAccess.AllPsd2Enum.fromValue(
                            Optional.ofNullable(access.getAllPsd2())
                                .map(AccountAccessType::getDescription)
                                .orElse(null)
                        )
                    );

                    return mappedAccountAccess;
                }
            )
            .orElse(null);
    }

    private static AccountAccessType mapToAccountAccessTypeFromAvailableAccounts(AccountAccess.AvailableAccountsEnum accountsEnum) {
        return Optional.ofNullable(accountsEnum)
            .flatMap(en -> AccountAccessType.getByDescription(en.toString()))
            .orElse(null);
    }

    private static AccountAccessType mapToAccountAccessTypeFromAllPsd2Enum(AccountAccess.AllPsd2Enum allPsd2Enum) {
        return Optional.ofNullable(allPsd2Enum)
            .flatMap(en -> AccountAccessType.getByDescription(en.toString()))
            .orElse(null);
    }

    private static List<AccountReference> mapToAccountReferencesInner(List<Object> references) {
        return Optional.ofNullable(references)
            .map(ref -> ref.stream()
                .map(ConsentModelMapper::mapToAccountReferenceInner)
                .collect(Collectors.toList()))
            .orElseGet(Collections::emptyList);
    }

    private static AccountReference mapToAccountReferenceInner(Object reference) {
        return OBJECT_MAPPER.convertValue(reference, AccountReference.class);
    }

    public static UpdateConsentPsuDataReq mapToUpdatePsuData(String psuId, String consentId, String authorizationId, Map body) {
        UpdateConsentPsuDataReq updatePsuData = new UpdateConsentPsuDataReq();
        updatePsuData.setPsuId(psuId);
        updatePsuData.setConsentId(consentId);
        updatePsuData.setAuthenticationMethodId(authorizationId);

        if (!body.isEmpty()) {
            Optional.ofNullable(body.get("psuData"))
                .map(o -> (LinkedHashMap<String, String>) o)
                .ifPresent(psuData -> {
                    updatePsuData.setPassword(psuData.get("password"));
                });

            Optional.ofNullable(body.get("authenticationMethodId"))
                .map(o -> (String) o)
                .ifPresent(authenticationMethodId -> updatePsuData.setAuthenticationMethodId(authenticationMethodId));

            Optional.ofNullable(body.get("scaAuthenticationData"))
                .map(o -> (String) o)
                .ifPresent(scaAuthenticationData -> updatePsuData.setScaAuthenticationData(scaAuthenticationData));
        } else {
            updatePsuData.setUpdatePsuIdentification(true);
        }

        return updatePsuData;
    }
}
