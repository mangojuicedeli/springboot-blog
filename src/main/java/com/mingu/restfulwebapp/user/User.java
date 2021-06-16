package com.mingu.restfulwebapp.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
//@JsonIgnoreProperties(value = {"password"})
@JsonFilter("UserInfo") // UserInfo 라는 이름의 필터를 추가
public class User {

    private Integer id;
    @Size(min = 2, message = "이름은 2글자 이상 입력해야합니다.")
    private String name;
    @Past
    private Date joinDate;
//    @JsonIgnore
    private String password;
//    @JsonIgnore
    private String ssn;

}
