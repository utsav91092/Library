package com.utils;


import com.responsedto.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommonUtils {

    public static <T> ResponseEntity<?> getResponseEntity(String textMessage, T value, List<String> errors, HttpStatus status) {

        String message = MessageFormat.format(textMessage, value);
        Error response = new Error();
        response.setId(String.valueOf(value));
        response.setMessage(message);
        response.setErrors(errors);
        if (status.equals(HttpStatus.OK))
            log.info(message);
        else
            log.error(message);
        return new ResponseEntity<>(response, status);
    }


    public static <T> Collector<T, ?, T> singletonCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }

}
