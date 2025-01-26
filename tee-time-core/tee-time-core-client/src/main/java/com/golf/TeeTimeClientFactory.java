package com.golf;

import feign.Retryer;
import com.golf.teetimecore.api.ApiClient;
import com.golf.teetimecore.api.TeeTimeApi;

import java.util.function.Supplier;

public class TeeTimeClientFactory {

    public static TeeTimeApi buildClient(Supplier<String> url) {
        ApiClient apiClient = new ApiClient();
        apiClient.getFeignBuilder().retryer(Retryer.NEVER_RETRY);
        apiClient.setBasePath(url.get());
        return apiClient.buildClient(TeeTimeApi.class);
    }
}
