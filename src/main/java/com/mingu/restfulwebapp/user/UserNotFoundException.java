package com.mingu.restfulwebapp.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 그냥 exception만 던지면 5XX 결과코드로 응답하므로, not_found(404) 코드로 에러를 특정해서 응답한다
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
