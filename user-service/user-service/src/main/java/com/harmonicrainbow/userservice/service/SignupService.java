package com.harmonicrainbow.userservice.service;

import com.harmonicrainbow.userservice.model.DTOS.SignupForm;
import com.harmonicrainbow.userservice.model.User;
import com.harmonicrainbow.userservice.repository.UsersRepo;
import com.harmonicrainbow.userservice.service.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SignupService {
    private UsersRepo usersRepo;
    private EmailSenderService emailSenderService;
    private static final String EMAIL_ADDRESS = "harmonicrainbow7@gmail.com";
    private static final String DOMAIN = "192.168.1.65";
    private static final String PORT = "8060";

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
    private boolean validatePassword(String password) {
        return password.matches(PasswordValidator.VERSION1.PASSWORD_VALIDATOR_REGEX);
    }
    private boolean checkIfAlreadyRegistered(String email) {
        List<User> users = usersRepo.findByEmail(email);
        return users.size() > 0;
    }
    private void sendEmail(UUID emailConfirmationToken, String email) {
        String confirmationCall = "http://" + DOMAIN + ":" + PORT + "/api/user/confirm?" + "token=" + emailConfirmationToken;
        String body = "Please confirm your email address by clicking on the following link: \n" + confirmationCall;
        emailSenderService.sendEmail(
                EMAIL_ADDRESS,
                email,
                "confirmation",
                body);
    }
    public Map<String, String> registerUser(SignupForm signupForm) {
        Map<String, String> validation = new HashMap<>();

        validation.put("isSignupSuccessful", "true");
        validation.put("reason", "everything's valid, email sent for confirmation");

        if (!Validator.validateEmail(signupForm.email())) {
            validation.put("isSignupSuccessful", "false");
            validation.put("reason", "wrong email format");
            return validation;
        }
        if (!validatePassword(signupForm.password())) {
            validation.put("isSignupSuccessful", "false");
            validation.put("reason", PasswordValidator.VERSION1.MESSAGE);
            return validation;
        }
        if (checkIfAlreadyRegistered(signupForm.email())) {
            validation.put("isSignupSuccessful", "false");
            validation.put("reason", "already registered email");
            return validation;
        }
        UUID emailConfirmationToken = UUID.randomUUID();
        usersRepo.save(
                new User(
                        signupForm.email(),
                        signupForm.password(),
                        false,
                        emailConfirmationToken));
        sendEmail(emailConfirmationToken, signupForm.email());
        return validation;
    }
    public Map<String, String> checkToken(String token) {
        Map<String, String> response = new HashMap<>();
        response.put("validation successful", "true");
        UUID uuidToken = UUID.fromString(token);
        List<User> users = usersRepo.findByEmailConfirmationToken(uuidToken);
        if (users.size() > 0) {
            User user = users.get(0);
            user.setActive(true);
            user.setEmailConfirmationToken(null);
            usersRepo.save(user);
            return response;
        }
        response.put("validation successful", "false");
        return response;
    }
}
