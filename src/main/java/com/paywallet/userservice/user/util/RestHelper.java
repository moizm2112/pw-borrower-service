package com.paywallet.userservice.user.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.util.TimeZone;

@Component
@Slf4j
public class RestHelper {

    public <T> T get(String url, HttpHeaders httpHeaders, Class<T> cls) {

        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        RestTemplate restTemplate=new RestTemplate();
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, cls).getBody();

    }

    public <T> T post(String url, HttpEntity httpEntity, Class<T> cls) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(DateFormat.getDateInstance());
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        MappingJackson2HttpMessageConverter dateConverter = new MappingJackson2HttpMessageConverter();
        dateConverter.setObjectMapper(mapper);
        RestTemplate restTemplate=new RestTemplate();
        restTemplate.getMessageConverters().add(dateConverter);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, cls).getBody();

    }

}
