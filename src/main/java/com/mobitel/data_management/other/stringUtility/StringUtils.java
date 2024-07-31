package com.mobitel.data_management.other.stringUtility;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {
    public String removeTrailingCommaAndSpace(String input) {
        if (input != null && input.endsWith(", ")) {
            // Remove the last ", "
            return input.substring(0, input.length() - 2);
        }
        return input;
    }
}
