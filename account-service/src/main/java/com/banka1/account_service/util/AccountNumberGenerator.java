package com.banka1.account_service.util;

import com.banka1.account_service.repository.AccountRepository;

import java.util.Random;

/**
 * Utility class for generating and validating 18-digit bank account numbers.
 *
 * Structure:
 *   Positions 1–3   : Bank code (hardcoded 111)
 *   Positions 4–7   : Branch code (hardcoded 0001)
 *   Positions 8–16  : 9 random digits
 *   Positions 17–17 : Account type code (1–2 chars from subtype)
 *   Position  18    : Check digit (modulo 11)
 */
public final class AccountNumberGenerator {

    private AccountNumberGenerator() {}

    /**
     * Calculates the check digit for an account number prefix using modulo 11.
     * Returns a value 0–9 (returns 10 if the result is 10, which is invalid — caller must retry).
     */
    public static int calculateCheckDigit(String prefix) {
        int sum = 0;
        for (char c : prefix.toCharArray()) {
            sum += c - '0';
        }
        return (11 - sum % 11) % 11;
    }

    /**
     * Validates a full 18-digit account number by verifying its check digit.
     *
     * @param number the account number string
     * @return true if the number is valid (18 digits, correct check digit), false otherwise
     */
    public static boolean validateAccountNumber(String number) {
        if (number == null || number.length() != 18) return false;
        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        String prefix = number.substring(0, 17);
        int expected = calculateCheckDigit(prefix);
        if (expected == 10) return false;
        return (number.charAt(17) - '0') == expected;
    }

    /**
     * Generates a unique 18-digit account number.
     * Retries on collision or when check digit would be 10.
     *
     * @param typeVal           the account type code string (e.g. "11", "21")
     * @param random            Random instance to use
     * @param accountRepository used to check uniqueness in the DB
     * @return a unique, valid 18-digit account number
     */
    public static String generate(String typeVal, Random random, AccountRepository accountRepository) {
        StringBuilder sb = new StringBuilder();
        String val = "";
        boolean exists = true;
        while (exists) {
            sb.setLength(0);
            for (int i = 0; i < 9; i++) {
                sb.append(random.nextInt(10));
            }
            val = "111" + "0001" + sb + typeVal;
            int checkDigit = calculateCheckDigit(val);
            if (checkDigit == 10) continue;
            val += checkDigit;
            exists = accountRepository.existsByBrojRacuna(val);
        }
        return val;
    }
}
