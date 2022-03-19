package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.RequestIdDTO;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.RequestIdResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

import javax.xml.bind.DatatypeConverter;
import java.util.Optional;

import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;

import static com.paywallet.userservice.user.util.NullableWrapperUtil.*;



@Component
@Slf4j
public class RequestIdUtil {

    @Value("${identifyProviderService.eureka.uri}")
    private String identifyProviderServiceUri;

    @Autowired
    RestTemplate restTemplate;

    @Value("${jwt.secret}")
    private String JWT_SECRET_KEY;


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


    /**
     * This function will call to request Service to fetch the request ID details
     *
     * @param requestId
     * @return
     * @throws RequestIdNotFoundException
     * @throws ResourceAccessException
     * @throws GeneralCustomException
     */
    public RequestIdDetails fetchRequestIdDetails(String requestId) throws RequestIdNotFoundException,ResourceAccessException,GeneralCustomException{
        log.info(" Inside fetchRequestIdDetails request id : {}  ", requestId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(REQUEST_ID, requestId);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(identifyProviderServiceUri);
            RequestIdResponseDTO requestIdResponse = restTemplate
                    .exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, RequestIdResponseDTO.class)
                    .getBody();
            Optional<RequestIdDetails> requestIdDetails = resolve(() -> requestIdResponse.getData());
            if (requestIdDetails.isPresent()) {
                return requestIdDetails.get();
            } else {
                throw new RequestIdNotFoundException(" Request ID details not found for request ID : {}" + requestId);
            }
        } catch (ResourceAccessException resourceException) {
            throw new ServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE.toString(), resourceException.getMessage());
        } catch (Exception ex) {
            throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
        }
    }


    /**
     *
     * This function will decode the JWT encoded string, it will return Option.empty() in case of any exception.
     *
     * @param encRequestID
     * @return
     */
    public Optional<String> getDecodedRequestID(String encRequestID) {

        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JWT_SECRET_KEY))
                    .parseClaimsJws(encRequestID).getBody();
            return Optional.ofNullable(claims.getSubject());
        }catch (Exception ex){
            log.error(" Exception while decoding the string {} ",ex.getMessage());
            return Optional.empty();
        }

    }

}
