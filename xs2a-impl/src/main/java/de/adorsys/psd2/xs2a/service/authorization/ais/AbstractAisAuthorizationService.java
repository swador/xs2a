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

package de.adorsys.psd2.xs2a.service.authorization.ais;

import de.adorsys.psd2.core.data.ais.AisConsent;
import de.adorsys.psd2.xs2a.domain.consent.ConsentAuthorisationsParameters;
import de.adorsys.psd2.xs2a.service.authorization.AbstractConsentAuthorizationService;
import de.adorsys.psd2.xs2a.service.authorization.Xs2aAuthorisationService;
import de.adorsys.psd2.xs2a.service.consent.Xs2aAisConsentService;
import de.adorsys.psd2.xs2a.service.consent.Xs2aConsentService;
import de.adorsys.psd2.xs2a.service.mapper.ConsentPsuDataMapper;
import de.adorsys.psd2.xs2a.service.mapper.cms_xs2a_mappers.Xs2aConsentAuthorisationMapper;

import java.util.Optional;


public abstract class AbstractAisAuthorizationService extends AbstractConsentAuthorizationService<AisConsent> {
    private final Xs2aAisConsentService aisConsentService;

    protected AbstractAisAuthorizationService(Xs2aConsentService consentService, Xs2aAuthorisationService authorisationService,
                                              ConsentPsuDataMapper consentPsuDataMapper, Xs2aAisConsentService aisConsentService,
                                              Xs2aConsentAuthorisationMapper xs2aConsentAuthorisationMapper) {
        super(consentService, authorisationService, consentPsuDataMapper, xs2aConsentAuthorisationMapper);
        this.aisConsentService = aisConsentService;
    }

    @Override
    protected Optional<AisConsent> getConsentById(String consentId) {
        return aisConsentService.getAccountConsentById(consentId);
    }

    @Override
    protected void updateConsentAuthorisation(ConsentAuthorisationsParameters updateConsentPsuDataReq) {
        aisConsentService.updateConsentAuthorisation(updateConsentPsuDataReq);
    }
}
