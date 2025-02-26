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

package de.adorsys.psd2.xs2a.domain.account;

import de.adorsys.psd2.xs2a.core.ais.BookingStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Xs2aTransactionsReportByPeriodRequest {
    private final String consentId;
    private final String accountId;
    private final String acceptHeader;
    private final boolean withBalance;
    private final LocalDate dateFrom;
    private final LocalDate dateTo;
    private final BookingStatus bookingStatus;
    private final String requestUri;
    private final String entryReferenceFrom;
    private final Boolean deltaList;
    private final Integer pageIndex;
    private final Integer itemsPerPage;
}
