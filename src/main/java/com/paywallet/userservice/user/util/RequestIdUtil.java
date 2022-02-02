package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.RequestIdDTO;
import com.paywallet.userservice.user.model.RequestIdResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class RequestIdUtil {

    @Value("${identifyProviderService.eureka.uri}")
    private String identifyProviderServiceUri;

    @Autowired
    RestTemplate restTemplate;

    /**
     * Method communicates with the identity service provider to generate request details by request ID.
     * @param apiKey
     * @return
     * @throws ResourceAccessException
     * @throws GeneralCustomException
     */
    public RequestIdResponseDTO generateRequestIdDetails(String apiKey) throws ResourceAccessException, GeneralCustomException, ServiceNotAvailableException {
        log.info("Inside generateRequestIdDetails");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-api-key", apiKey);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RequestIdResponseDTO requestIdResponse = new RequestIdResponseDTO();
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(identifyProviderServiceUri);

            requestIdResponse = restTemplate
                    .exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity, RequestIdResponseDTO.class)
                    .getBody();

        } catch (ResourceAccessException resourceException) {
            throw new ServiceNotAvailableException( HttpStatus.SERVICE_UNAVAILABLE.toString(), resourceException.getMessage());
        } catch (Exception ex) {
            throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        }
        log.info("respons from  generateRequestIdDetails : " + requestIdResponse);
        return requestIdResponse;
    }

    /**
     * Method communicates with the identity service provider to update request details by request ID.
     * @param requestId
     * @return
     * @throws ResourceAccessException
     * @throws GeneralCustomException
     */
    public RequestIdResponseDTO updateRequestIdDetails(String requestId, String customerId, String virtualAccountNumber)
            throws ResourceAccessException, GeneralCustomException, ServiceNotAvailableException {
        log.info("Inside updateRequestIdDetails");

        /* SET INPUT (REQUESTIDDTO) TO ACCESS THE IDENTITY PROVIDER SERVICE*/
        RequestIdDTO requestIdDTO = new RequestIdDTO();
        requestIdDTO.setUserId(customerId);
        requestIdDTO.setVirtualAccountNumber(virtualAccountNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-request-id", requestId);
        HttpEntity<String> requestEntity = new HttpEntity(requestIdDTO, headers);

        RequestIdResponseDTO requestIdResponse = new RequestIdResponseDTO();
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(identifyProviderServiceUri);

            HttpClient httpClient = HttpClientBuilder.create().build();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

            requestIdResponse = restTemplate
                    .exchange(uriBuilder.toUriString(), HttpMethod.PATCH, requestEntity, RequestIdResponseDTO.class)
                    .getBody();

        } catch (ResourceAccessException resourceException) {
            throw new ServiceNotAvailableException( HttpStatus.SERVICE_UNAVAILABLE.toString(), resourceException.getMessage());
        } catch (Exception ex) {
            throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        }
        log.info("response from  updateRequestIdDetails : " + requestIdResponse);
        return requestIdResponse;
    }
}
