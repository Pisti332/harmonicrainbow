package com.harmonicrainbow.userservice.service.utility;

public class Validator {
    private static final String RFC5322_EMAIL_VALIDATOR_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static boolean validateEmail(String email) {
        return email.matches(RFC5322_EMAIL_VALIDATOR_REGEX);
    }
}
