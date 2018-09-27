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

package de.adorsys.aspsp.xs2a.spi.domain.code;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum SpiFrequencyCode {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    EVERYTWOWEEKS("EveryTwoWeeks"),
    MONTHLY("Monthly"),
    EVERYTWOMONTHS("EveryTwoMonths"),
    QUARTERLY("Quarterly"),
    SEMIANNUAL("SemiAnnual"),
    ANNUAL("Annual");

    private String value;

    private final static Map<String, SpiFrequencyCode> container = new HashMap<>();

    SpiFrequencyCode(String value) {
        this.value = value;
    }

    static {
        for (SpiFrequencyCode type : values()) {
            container.put(type.getValue(), type);
        }
    }

    public String getValue() {
        return value;
    }

    public static Optional<SpiFrequencyCode> getByValue(String name) {
        return Optional.ofNullable(container.get(name));
    }
}
