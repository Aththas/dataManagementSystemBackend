package com.mobitel.data_management.other.otpService;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpStorage {

    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

    public void storeOtp(String email, String otp) {
        otpStorage.put(email, otp);
    }

    public String retrieveOtp(String email) {
        return otpStorage.get(email);
    }

    public void removeOtp(String email) {
        otpStorage.remove(email);
    }

}

