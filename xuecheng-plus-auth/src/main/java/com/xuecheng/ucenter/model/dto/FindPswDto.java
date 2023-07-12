package com.xuecheng.ucenter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiong
 * @version 1.0
 * @description
 * @date 2023/7/10 21:53:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPswDto {
    String cellphone;

    String email;

    String checkcodekey;

    String checkcode;

    String password;

    String confirmpwd;
}
