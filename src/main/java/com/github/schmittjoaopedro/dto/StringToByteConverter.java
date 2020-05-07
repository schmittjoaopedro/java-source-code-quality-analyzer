package com.github.schmittjoaopedro.dto;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.nio.charset.Charset;

public class StringToByteConverter extends StdConverter<String, byte[]> {

    @Override
    public byte[] convert(String s) {
        return s.getBytes(Charset.forName("UTF-8"));
    }

}
