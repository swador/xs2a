package de.adorsys.aspsp.xs2a.spi.config;

import java.util.HashMap;
import java.util.Map;

public class RemoteSpiUrls {
    private String baseUrl;
    private Map<String, String> urls;

    RemoteSpiUrls(String baseUrl) {
        this.baseUrl = baseUrl;
        this.urls = new HashMap<>();
        this.urls.put("getAllAccounts", "/account/");
        this.urls.put("getConsents", "/consent/");
        this.urls.put("getConsentById", "/consent/{id}");
        this.urls.put("deleteConsentById", "/consent/{id}");
    }

    public String getUrl(String key) {
        return this.baseUrl + this.urls.getOrDefault(key, "");
    }
}
