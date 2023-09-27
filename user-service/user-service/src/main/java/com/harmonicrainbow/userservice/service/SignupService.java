package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.model.User;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SignupService {
    private UsersRepo usersRepo;
    private static final String RFC5322_EMAIL_VALIDATOR_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private EmailSenderService emailSenderService;
    private static final String EMAIL_ADDRESS = "harmonicrainbow7@gmail.com";

    private enum PasswordValidator {
        VERSION1("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", "wrong password format: must have at least\n  8 characters\n one uppercase English letter\n one lowercase English letter\n one digit\n one special character");
        private final String PASSWORD_VALIDATOR_REGEX;
        private final String MESSAGE;
        PasswordValidator(String PASSWORD_VALIDATOR_REGEX, String MESSAGE) {
            this.MESSAGE = MESSAGE;
            this.PASSWORD_VALIDATOR_REGEX = PASSWORD_VALIDATOR_REGEX;
        }


    }

    @Autowired
    public SignupService(UsersRepo usersRepo, EmailSenderService emailSenderService) {
        this.usersRepo = usersRepo;
        this.emailSenderService = emailSenderService;
    }
    private boolean validateEmail(String email) {
        return email.matches(RFC5322_EMAIL_VALIDATOR_REGEX);
    }
    private boolean validatePassword(String password) {
        return password.matches(PasswordValidator.VERSION1.PASSWORD_VALIDATOR_REGEX);
    }
    private boolean checkIfAlreadyRegistered(String email) {
        List<User> users = usersRepo.findByEmail(email);
        return users.size() > 0;
    }
    private void sendEmail(UUID emailConfirmationToken, String email) {
        emailSenderService.sendEmail(
                EMAIL_ADDRESS,
                email,
                "confirmation",
                emailConfirmationToken.toString());
    }
    public Map<String, String> registerUser(SignupForm signupForm) {
        Map<String, String> validation = new HashMap<>();
        validation.put("isSignupSuccessful", "true");
        validation.put("reason", "everything's valid, email sent for confirmation");
        if (!validateEmail(signupForm.email())) {
            validation.put("isSignupSuccessful", "false");
            validation.put("reason", "wrong email format");
            return validation;
        }
        else if (!validatePassword(signupForm.password())) {
            validation.put("isSignupSuccessful", "false");
            validation.put("reason", PasswordValidator.VERSION1.MESSAGE);
            return validation;
        }
        else if (checkIfAlreadyRegistered(signupForm.email())) {
            validation.put("isSignupSuccessful", "false");
            validation.put("reason", "already registered email");
            return validation;
        }
        UUID emailConfirmationToken = UUID.randomUUID();
        usersRepo.save(new User(signupForm.email(), signupForm.password(), false, emailConfirmationToken));
        sendEmail(emailConfirmationToken, signupForm.email());
        // generate token +
        // store token +
        // send api call with token via email

        return validation;
    }
}
