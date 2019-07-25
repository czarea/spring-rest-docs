package com.czarea.spring.restdocs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhouzx
 */
@Data
@AllArgsConstructor
public class ApiError {
    private String[] codes;
    private String message;

    public ApiError(String code, String message) {
        this(new String[] {code}, message);
    }
}
