package edu.ithaca.dturnbull.bank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankAccount {

    private String email;
    private double balance;

    /**
     * @throws IllegalArgumentException if email is invalid
     * @throws IllegalArgumentException if startingBalance is negative or has more
     */
    public BankAccount(String email, double startingBalance) {
        isAmountValid(startingBalance);

        if (!isEmailValid(email)) {
            throw new IllegalArgumentException("Email address: " + email + " is invalid, cannot create account");
        }

        this.email = email;
        this.balance = startingBalance;
    }

    /**
     * 
     * @param amount amount to check
     * @throws IllegalArgumentException if amount is negative or has more than two
     */
    private void isAmountValid(double amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        if (amount * 100 != (int) (amount * 100)) {
            throw new IllegalArgumentException("Amount must have at most two decimal places");
        }
    }

    public double getBalance() {
        return balance;
    }

    public String getEmail() {
        return email;
    }

    /**
     * @pre amount is non-negative
     * @param amount amount to withdraw (non-negative with two decimal places)
     * @throws InsufficientFundsException if amount is greater than balance
     * @throws IllegalArgumentException   if amount is negative or has more than two
     *                                    decimal places
     * @post reduces the balance by amount if amount is non-negative and smaller
     *       than balance
     */
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0)
            throw new IllegalArgumentException("Cannot withdraw a negative or zero amount");

        if (amount * 100 != (int) (amount * 100))
            throw new IllegalArgumentException("Amount must have at most two decimal places");

        if (amount <= balance) {
            balance -= amount;
        } else {
            throw new InsufficientFundsException("Insufficient balance for requested withdrawal amount");
        }
    }

    /**
     * 
     * @param amount amount to deposit
     * @throws IllegalArgumentException if amount is negative or has more than two
     *                                  decimal places
     * @post increases the balance by amount if amount is non-negative
     */
    public void deposit(double amount) {
        isAmountValid(amount);
        balance += amount;
    }

    /**
     * 
     * @param source account to transfer from
     * @param dest   account to transfer to
     * @param amount amount to transfer
     * @throws InsufficientFundsException if amount is greater than balance
     * @throws IllegalArgumentException   if amount is negative or has more than two
     *                                    decimal places
     */
    public static void transfer(BankAccount source, BankAccount dest, double amount)
            throws InsufficientFundsException {

    }

    /**
     * @param email email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isEmailValid(String email) {
        if (email.indexOf('@') == -1) {
            return false;
        }
        String[] emailParts = email.split("@");
        if (emailParts.length != 2) {
            return false;
        }

        return (validatePrefix(emailParts[0]) &&
                validateDomain(emailParts[1]));
    }

    private static final Pattern PREFIX_PATTERN = Pattern.compile(
            "^(?!\\.)(?!.*\\.\\.)(?!.*\\.$)" +
                    "[a-zA-Z0-9]([a-zA-Z0-9!#\\$%&'\\*\\+\\-\\/=\\?\\^_`\\.\\{\\|\\}~]{0,62}[a-zA-Z0-9])?$");
    // no leading, trailing, or consecutive dots
    // valid characters and length

    private static final Pattern HOST_PATTERN = Pattern.compile(
            "^(?!\\.)(?!.*\\.\\.)(?!.*\\.$)" +
                    "[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*\\.[a-zA-Z]{2,63}$");
    // no leading, trailing, or consecutive dots
    // alphanumeric sequences of 1-63 characters separated by dots, with hypens
    // allowed but not at the beginning or end

    private static final Pattern IPV4_PATTERN = Pattern.compile("^\\[(([0-9]{1,3}\\.){3}[0-9]{1,3})\\]$");
    // four sequences of 1-3 digits separated by dots within square brackets

    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^\\[IPv6:(([a-fA-F0-9]{1,4}:){7}[a-fA-F0-9]{1,4}" +
                    "|([a-fA-F0-9]{1,4}:){1,7}:" +
                    "|:([a-fA-F0-9]{1,4}:){1,7}" +
                    "|::" +
                    "|([a-fA-F0-9]{1,4}:){1,6}:[a-fA-F0-9]{1,4}" +
                    "|([a-fA-F0-9]{1,4}:){1,5}(:[a-fA-F0-9]{1,4}){1,2}" +
                    "|([a-fA-F0-9]{1,4}:){1,4}(:[a-fA-F0-9]{1,4}){1,3}" +
                    "|([a-fA-F0-9]{1,4}:){1,3}(:[a-fA-F0-9]{1,4}){1,4}" +
                    "|([a-fA-F0-9]{1,4}:){1,2}(:[a-fA-F0-9]{1,4}){1,5}" +
                    "|[a-fA-F0-9]{1,4}:((:[a-fA-F0-9]{1,4}){1,6}))\\]$");
    // IPv6 address within square brackets, with 8 groups of 1-4 hexadecimal
    // digits separated by colons, with double colons allowed
    // to represent multiple groups of zeros

    private static boolean validatePrefix(String local) {
        Matcher localmatcher = PREFIX_PATTERN.matcher(local);
        return localmatcher.find();
    }

    private static boolean validateDomain(String domain) {
        return validateHostname(domain) || validateIPv4(domain) ||
                validateIPv6(domain);
    }

    private static boolean validateHostname(String domain) {
        Matcher hostMatcher = HOST_PATTERN.matcher(domain);
        return hostMatcher.find() && domain.length() <= 253;
    }

    private static boolean validateIPv4(String domain) {
        Matcher IPv4matcher = IPV4_PATTERN.matcher(domain);
        if (!IPv4matcher.find()) {
            return false;
        }
        String[] ipStrings = domain.substring(1, domain.length() - 1).split("\\.");
        for (String part : ipStrings) {
            int num = Integer.parseInt(part);
            if (num < 0 || num > 255) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateIPv6(String domain) {
        Matcher IPv6matcher = IPV6_PATTERN.matcher(domain);
        return IPv6matcher.find();
    }

}