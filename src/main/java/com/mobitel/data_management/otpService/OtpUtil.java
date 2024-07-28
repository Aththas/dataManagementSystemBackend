package com.mobitel.data_management.otpService;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10)); // Append random digit (0-9)
        }
        return sb.toString();
    }
}

