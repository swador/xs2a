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

package de.adorsys.psd2.xs2a.spi.domain.consent;

import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
public class SpiVerifyScaAuthorisationResponse {
    @NotNull
    private SpiConsentStatus consentStatus;
    private SpiAuthorisationStatus spiAuthorisationStatus;

    public SpiVerifyScaAuthorisationResponse(@NotNull SpiConsentStatus consentStatus) {
        this.consentStatus = consentStatus;
        spiAuthorisationStatus = null;
    }

    public SpiVerifyScaAuthorisationResponse(@NotNull SpiConsentStatus consentStatus, SpiAuthorisationStatus spiAuthorisationStatus) {
        this.consentStatus = consentStatus;
        this.spiAuthorisationStatus = spiAuthorisationStatus;
    }
}
