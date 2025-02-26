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

package de.adorsys.psd2.xs2a.service.mapper.psd2;

import de.adorsys.psd2.xs2a.core.domain.TppMessageInformation;
import de.adorsys.psd2.xs2a.service.message.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public abstract class Psd2ErrorMapper<T, R> {
    @Autowired
    protected MessageService messageService;

    public abstract Function<T, R> getMapper();

    public abstract HttpStatus getErrorStatus();

    protected String getErrorText(TppMessageInformation tppMessageInformation) {
        if (StringUtils.isNotBlank(tppMessageInformation.getText())) {
            return tppMessageInformation.getText();
        }
        String textFromProperties = messageService.getMessage(tppMessageInformation.getMessageErrorCode().name());
        return String.format(textFromProperties, tppMessageInformation.getTextParameters());
    }
}
