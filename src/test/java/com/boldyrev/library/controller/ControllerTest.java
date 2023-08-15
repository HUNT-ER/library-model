package com.boldyrev.library.controller;

import org.springframework.http.HttpHeaders;

public class ControllerTest {

    protected HttpHeaders createPageAndSizeParams(int page, int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("page", Integer.toString(page));
        headers.add("size", Integer.toString(size));

        return headers;
    }

}
