package com.mingu.restfulwebapp.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
public class User {

    private Integer id;
    @Size(min = 2, message = "이름은 2글자 이상 입력해야합니다.")
    private String name;
    @Past
    private Date joinDate;
}
