package com.example.test.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SignatureResponse {
    private String status;
    private List<Signature> result;
}