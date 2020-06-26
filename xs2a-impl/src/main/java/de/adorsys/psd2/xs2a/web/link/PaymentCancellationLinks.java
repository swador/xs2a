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
import de.adorsys.psd2.xs2a.domain.pis.CancelPaymentResponse;
import de.adorsys.psd2.xs2a.service.RedirectIdService;
import de.adorsys.psd2.xs2a.service.ScaApproachResolver;
import de.adorsys.psd2.xs2a.web.RedirectLinkBuilder;

import java.util.EnumSet;

import static de.adorsys.psd2.xs2a.core.pis.TransactionStatus.RJCT;
import static de.adorsys.psd2.xs2a.core.profile.ScaApproach.*;

public class PaymentCancellationLinks extends AbstractLinks {
    private ScaApproachResolver scaApproachResolver;
    private RedirectLinkBuilder redirectLinkBuilder;
    private RedirectIdService redirectIdService;
    private boolean isExplicitMethod;
    private ScaRedirectFlow scaRedirectFlow;
    private boolean authorisationConfirmationRequestMandated;
    private String instanceId;

    public PaymentCancellationLinks(String httpUrl, ScaApproachResolver scaApproachResolver, RedirectLinkBuilder redirectLinkBuilder,
                                    RedirectIdService redirectIdService, CancelPaymentResponse response,
                                    boolean isExplicitMethod, ScaRedirectFlow scaRedirectFlow,
                                    boolean authorisationConfirmationRequestMandated, String instanceId) {
        super(httpUrl);
        this.scaApproachResolver = scaApproachResolver;
        this.redirectLinkBuilder = redirectLinkBuilder;
        this.redirectIdService = redirectIdService;
        this.isExplicitMethod = isExplicitMethod;
        this.scaRedirectFlow = scaRedirectFlow;
        this.authorisationConfirmationRequestMandated = authorisationConfirmationRequestMandated;
        this.instanceId = instanceId;

        buildCancellationLinks(response);
    }

    private void buildCancellationLinks(CancelPaymentResponse body) {
        if (RJCT == body.getTransactionStatus()) {
            return;
        }
        String paymentId = body.getPaymentId();
        String paymentService = body.getPaymentType().getValue();
        String paymentProduct = body.getPaymentProduct();
        String authorisationId = body.getAuthorizationId();
        String internalRequestId = body.getInternalRequestId();

        setSelf(buildPath(UrlHolder.PAYMENT_LINK_URL, paymentService, paymentProduct, paymentId));
        setStatus(buildPath(UrlHolder.PAYMENT_STATUS_URL, paymentService, paymentProduct, paymentId));

        ScaApproach scaApproach = scaApproachResolver.resolveScaApproach();
        if (EnumSet.of(EMBEDDED, DECOUPLED).contains(scaApproach)) {
            addEmbeddedDecoupledRelatedLinks(paymentService, paymentProduct, paymentId, authorisationId);
        } else if (scaApproach == REDIRECT) {
            addRedirectRelatedLinks(paymentService, paymentProduct, paymentId, authorisationId, internalRequestId);
        } else if (scaApproach == OAUTH) {
            setScaOAuth(new HrefType("scaOAuth"));
        }
    }

    private void addEmbeddedDecoupledRelatedLinks(String paymentService, String paymentProduct, String paymentId, String authorisationId) {
        if (isExplicitMethod) {
            setStartAuthorisationWithPsuAuthentication(buildPath(UrlHolder.START_PIS_CANCELLATION_AUTH_URL, paymentService, paymentProduct, paymentId));
        } else {
            setScaStatus(
                buildPath(UrlHolder.PIS_CANCELLATION_AUTH_LINK_URL, paymentService, paymentProduct, paymentId, authorisationId));

            setUpdatePsuAuthentication(
                buildPath(UrlHolder.PIS_CANCELLATION_AUTH_LINK_URL, paymentService, paymentProduct, paymentId, authorisationId));
        }
    }

    private void addRedirectRelatedLinks(String paymentService, String paymentProduct, String paymentId, String authorisationId, String internalRequestId) {
        if (isExplicitMethod) {
            setStartAuthorisation(buildPath(UrlHolder.START_PIS_CANCELLATION_AUTH_URL, paymentService, paymentProduct, paymentId));
        } else {
            String redirectId = redirectIdService.generateRedirectId(authorisationId);

            String paymentCancellationOauthLink = scaRedirectFlow == ScaRedirectFlow.OAUTH
                                          ? redirectLinkBuilder.buildPaymentCancellationScaOauthRedirectLink(paymentId, redirectId, internalRequestId)
                                          : redirectLinkBuilder.buildPaymentCancellationScaRedirectLink(paymentId, redirectId, internalRequestId, instanceId);

            setScaRedirectOAuthLink(scaRedirectFlow, paymentCancellationOauthLink);

            setScaStatus(
                buildPath(UrlHolder.PIS_CANCELLATION_AUTH_LINK_URL, paymentService, paymentProduct, paymentId, authorisationId));

            if (authorisationConfirmationRequestMandated) {
                setConfirmation(buildPath(redirectLinkBuilder.buildPisCancellationConfirmationLink(paymentService, paymentProduct, paymentId, redirectId)));
            }
        }
    }
}
