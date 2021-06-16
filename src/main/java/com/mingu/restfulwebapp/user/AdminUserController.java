package com.mingu.restfulwebapp.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

    private UserDaoService service;

    public AdminUserController(UserDaoService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers() {
        List<User> users = service.findAll();
        /*
        필터를 이용한 필드 제어
         */
        // 1. 지정된 필드만 JSON 변환하는 필터 생성
        SimpleBeanPropertyFilter filter =
                SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "password");
        // 2. 1에서 만든 필터와 User 클래스 필터를 provider에 추가한다.
        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
        // 3. filter 및 serialization을 위한 mappingJacksonValue 객체 생성
        MappingJacksonValue mapping = new MappingJacksonValue(users);
        // 4. 필터 세팅
        mapping.setFilters(filters);

        return mapping;
    }

//    @GetMapping("/v1/users/{id}") -- 1) uri
//    @GetMapping(value = "/users/{id}/", params = "version=1") -- 2) request param
//    @GetMapping(value = "/users/{id}", headers = "X-API-VERSION=1") -- 3) header
//    @GetMapping(value = "/users/{id}", produces = "application/vnd.company.appv1+json") -- 4) mime type(Accept)
    @GetMapping("/v1/users/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable int id) {
        User user = service.findOne(id);
        // 찾는 user가 없을 경우, null이 아닌 예외를 던져서 200 응답이 나오지 않게 한다.
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        /*
        필터를 이용한 필드 제어
         */
        // 1. 지정된 필드만 JSON 변환하는 필터 생성
        SimpleBeanPropertyFilter filter =
                SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "password");
        // 2. 1에서 만든 필터와 User 클래스 필터를 provider에 추가한다.
        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
        // 3. filter 및 serialization을 위한 mappingJacksonValue 객체 생성
        MappingJacksonValue mapping = new MappingJacksonValue(user);
        // 4. 필터 세팅
        mapping.setFilters(filters);

        return mapping;
    }

//    @GetMapping("/v2/users/{id}")
//    @GetMapping(value = "/users/{id}/", params = "version=2")
//    @GetMapping(value = "/users/{id}", headers = "X-API-VERSION=2")
//    @GetMapping(value = "/users/{id}", produces = "application/vnd.company.appv2+json") -- 4) mime type(Accept)
    @GetMapping("/v2/users/{id}")
    public MappingJacksonValue retrieveUserV2(@PathVariable int id) {
        User user = service.findOne(id);
        // 찾는 user가 없을 경우, null이 아닌 예외를 던져서 200 응답이 나오지 않게 한다.
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        // User -> UserV2 로 공통된 필드를 복사(BeanUtils.copyProperties())
        UserV2 userV2 = new UserV2();
        BeanUtils.copyProperties(user, userV2);

        userV2.setGrade("VIP");

        /*
        필터를 이용한 필드 제어
         */
        // 1. 지정된 필드만 JSON 변환하는 필터 생성
        SimpleBeanPropertyFilter filter =
                SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "password", "grade");
        // 2. 1에서 만든 필터와 User 클래스 필터를 provider에 추가한다.
        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfoV2", filter);
        // 3. filter 및 serialization을 위한 mappingJacksonValue 객체 생성
        MappingJacksonValue mapping = new MappingJacksonValue(userV2);
        // 4. 필터 세팅
        mapping.setFilters(filters);

        return mapping;
    }
}