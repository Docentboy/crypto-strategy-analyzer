package ru.innopolis.attestations.attestation03.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class CandleParsingException extends RuntimeException {
    public CandleParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}