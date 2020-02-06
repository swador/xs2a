/*
 * Copyright 2018-2019 adorsys GmbH & Co KG
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

package de.adorsys.psd2.xs2a.web.link;

import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import de.adorsys.psd2.xs2a.core.profile.ScaRedirectFlow;
import de.adorsys.psd2.xs2a.domain.HrefType;
import de.adorsys.psd2.xs2a.domain.pis.PaymentInitiationParameters;
import de.adorsys.psd2.xs2a.domain.pis.PaymentInitiationResponse;
import de.adorsys.psd2.xs2a.service.RedirectIdService;
import de.adorsys.psd2.xs2a.service.ScaApproachResolver;
import de.adorsys.psd2.xs2a.web.RedirectLinkBuilder;
import de.adorsys.psd2.xs2a.web.aspect.UrlHolder;

import java.util.EnumSet;

import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.RJCT;
import static de.adorsys.psd2.xs2a.core.profile.ScaApproach.*;

public class PaymentInitiationLinks extends AbstractLinks {

    private ScaApproachResolver scaApproachResolver;
    private RedirectLinkBuilder redirectLinkBuilder;
    private RedirectIdService redirectIdService;
    private boolean explicitMethod;
    private ScaRedirectFlow scaRedirectFlow;
    private boolean authorisationConfirmationRequestMandated;

    public PaymentInitiationLinks(String httpUrl, ScaApproachResolver scaApproachResolver, RedirectLinkBuilder redirectLinkBuilder,
                                  RedirectIdService redirectIdService,
                                  PaymentInitiationParameters paymentRequestParameters, PaymentInitiationResponse body,
                                  boolean explicitMethod, boolean signingBasketModeActive,
                                  ScaRedirectFlow scaRedirectFlow,
                                  boolean authorisationConfirmationRequestMandated) {
        super(httpUrl);
        this.scaApproachResolver = scaApproachResolver;
        this.redirectLinkBuilder = redirectLinkBuilder;
        this.redirectIdService = redirectIdService;
        this.explicitMethod = explicitMethod;
        this.scaRedirectFlow = scaRedirectFlow;
        this.authorisationConfirmationRequestMandated = authorisationConfirmationRequestMandated;

        buildPaymentLinks(paymentRequestParameters, body, signingBasketModeActive);
    }

    private void buildPaymentLinks(PaymentInitiationParameters paymentRequestParameters, PaymentInitiationResponse body, boolean signingBasketModeActive) {
        if (RJCT == body.getTransactionStatus()) {
            return;
        }
        String paymentService = paymentRequestParameters.getPaymentType().getValue();
        String paymentProduct = paymentRequestParameters.getPaymentProduct();
        String paymentId = body.getPaymentId();
        String authorisationId = body.getAuthorizationId();
        String internalRequestId = body.getInternalRequestId();

        setSelf(buildPath(UrlHolder.PAYMENT_LINK_URL, paymentService, paymentProduct, paymentId));
        setStatus(buildPath(UrlHolder.PAYMENT_STATUS_URL, paymentService, paymentProduct, paymentId));

        ScaApproach scaApproach = authorisationId == null ?
                                      scaApproachResolver.resolveScaApproach()
                                      : scaApproachResolver.getScaApproach(authorisationId);
        if (EnumSet.of(EMBEDDED, DECOUPLED).contains(scaApproach)) {
            addEmbeddedDecoupledRelatedLinks(paymentService, paymentProduct, paymentId, authorisationId, signingBasketModeActive);
        } else if (scaApproach == REDIRECT) {
            addRedirectRelatedLinks(paymentService, paymentProduct, paymentId, authorisationId, internalRequestId);
        } else if (scaApproach == OAUTH) {
            setScaOAuth(new HrefType("scaOAuth")); //TODO generate link for oauth https://git.adorsys.de/adorsys/xs2a/aspsp-xs2a/issues/326
        }
    }

    private void addEmbeddedDecoupledRelatedLinks(String paymentService, String paymentProduct, String paymentId,
                                                  String authorisationId, boolean signingBasketModeActive) {
        if (explicitMethod) {
            if (signingBasketModeActive) { // no more data needs to be updated
                setStartAuthorisation(buildPath(UrlHolder.START_PIS_AUTHORISATION_URL, paymentService, paymentProduct, paymentId));
            } else {
                setStartAuthorisationWithPsuAuthentication(buildPath(UrlHolder.START_PIS_AUTHORISATION_URL, paymentService, paymentProduct, paymentId));
            }
        } else {
            setScaStatus(
                buildPath(UrlHolder.PIS_AUTHORISATION_LINK_URL, paymentService, paymentProduct, paymentId, authorisationId));
            setUpdatePsuAuthentication(
                buildPath(UrlHolder.PIS_AUTHORISATION_LINK_URL, paymentService, paymentProduct, paymentId, authorisationId));
        }
    }

    private void addRedirectRelatedLinks(String paymentService, String paymentProduct, String paymentId, String authorisationId, String internalRequestId) {
        if (explicitMethod) {
            setStartAuthorisation(buildPath(UrlHolder.START_PIS_AUTHORISATION_URL, paymentService, paymentProduct, paymentId));
        } else {
            String redirectId = redirectIdService.generateRedirectId(authorisationId);

            String paymentOauthLink = scaRedirectFlow == ScaRedirectFlow.OAUTH
                                          ? redirectLinkBuilder.buildPaymentScaOauthRedirectLink(paymentId, redirectId, internalRequestId)
                                          : redirectLinkBuilder.buildPaymentScaRedirectLink(paymentId, redirectId, internalRequestId);

            setScaRedirectOAuthLink(scaRedirectFlow, paymentOauthLink);
            setScaStatus(
                buildPath(UrlHolder.PIS_AUTHORISATION_LINK_URL, paymentService, paymentProduct, paymentId, authorisationId));

            if (authorisationConfirmationRequestMandated) {
                setConfirmation(buildPath(redirectLinkBuilder.buildPisConfirmationLink(paymentService, paymentProduct, paymentId, redirectId)));
            }
        }
    }
}
