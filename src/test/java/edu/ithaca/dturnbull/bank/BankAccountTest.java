package edu.ithaca.dturnbull.bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void getBalanceTest() {
        BankAccount bankAccount = new BankAccount("a@b.com", 200);

        assertEquals(200, bankAccount.getBalance(), 0.001);
    }

    @Test
    void withdrawTest() throws InsufficientFundsException {
        BankAccount bankAccount = new BankAccount("a@b.com", 200);
        bankAccount.withdraw(100);
        assertEquals(100, bankAccount.getBalance(), 0.001);

        // only up to two decimal places
        bankAccount.withdraw(0.1);
        assertEquals(99.90, bankAccount.getBalance(), 0.001);
        bankAccount.withdraw(0.01);
        assertEquals(99.89, bankAccount.getBalance(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> bankAccount.withdraw(100.001));

        // checking exception is thrown when withdrawing more than balance
        assertThrows(InsufficientFundsException.class, () -> bankAccount.withdraw(300));
        assertThrows(InsufficientFundsException.class, () -> bankAccount.withdraw(100.01));

        // checking exception is thrown when withdrawing negative or zero amount
        assertThrows(IllegalArgumentException.class, () -> bankAccount.withdraw(-100));
        assertThrows(IllegalArgumentException.class, () -> bankAccount.withdraw(0));
    }

    @Test
    void depositTest() {
        BankAccount bankAccount = new BankAccount("a@b.com", 0);
        assertEquals(bankAccount.getBalance(), 0, 0.001);

        // Valid cases
        bankAccount.deposit(0);
        assertEquals(bankAccount.getBalance(), 0, 0.001);
        bankAccount.deposit(100);
        assertEquals(bankAccount.getBalance(), 100, 0.001);
        bankAccount.deposit(0.01);
        assertEquals(bankAccount.getBalance(), 100.01, 0.001);
        bankAccount.deposit(0.1);
        assertEquals(bankAccount.getBalance(), 100.11, 0.001);
        bankAccount.deposit(0.10);
        assertEquals(bankAccount.getBalance(), 100.21, 0.001);

        // Invalid cases
        assertThrows(IllegalArgumentException.class, () -> bankAccount.deposit(-100));
        assertThrows(IllegalArgumentException.class, () -> bankAccount.deposit(0.001));
        assertThrows(IllegalArgumentException.class, () -> bankAccount.deposit(-100.001));

    }

    @Test
    void transferTest() {
        BankAccount source = new BankAccount("a@b.com", 200);
        BankAccount dest = new BankAccount("a@b.com", 0);

        // Valid cases
        assertDoesNotThrow(() -> BankAccount.transfer(source, dest, 100));
        assertEquals(100, source.getBalance(), 0.001);
        assertEquals(100, dest.getBalance(), 0.001);

        assertDoesNotThrow(() -> BankAccount.transfer(source, dest, 0.01));
        assertEquals(99.99, source.getBalance(), 0.001);
        assertEquals(100.01, dest.getBalance(), 0.001);

        assertDoesNotThrow(() -> BankAccount.transfer(source, dest, 0));
        assertEquals(99.99, source.getBalance(), 0.001);
        assertEquals(100.01, dest.getBalance(), 0.001);

        // Invalid cases
        assertThrows(InsufficientFundsException.class, () -> BankAccount.transfer(source, dest, 100.01));
        assertThrows(InsufficientFundsException.class, () -> BankAccount.transfer(source, dest, 100.001));
        assertThrows(IllegalArgumentException.class, () -> BankAccount.transfer(source, dest, -100));
        assertThrows(IllegalArgumentException.class, () -> BankAccount.transfer(source, dest, 0.001));
        assertThrows(IllegalArgumentException.class, () -> BankAccount.transfer(source, dest, -100.001));
    }

    @Test
    void isAmountValidTest() {
        // True Equivelance Class
        assertDoesNotThrow(() -> new BankAccount("a@b.com", 0));
        assertDoesNotThrow(() -> new BankAccount("a@b.com", 0.01));
        assertDoesNotThrow(() -> new BankAccount("a@b.com", 100));
        assertDoesNotThrow(() -> new BankAccount("a@b.com", 100.01));
        assertDoesNotThrow(() -> new BankAccount("a@b.com", 100.1));
        assertDoesNotThrow(() -> new BankAccount("a@b.com", 100.10));

        // False Equivelance Class
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", -0.01));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", 0.001));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", -0.001));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", -100));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", -100.01));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", -100.1));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", -100.10));
    }

    @Test
    void isEmailValidTest() {
        assertTrue(BankAccount.isEmailValid("a@b.com")); // valid email address
        assertFalse(BankAccount.isEmailValid("")); // empty string

        // False Cases - Local
        assertFalse(BankAccount.isEmailValid("abc-@mail.com")); // hyphen at end
        assertFalse(BankAccount.isEmailValid("abc..def@mail.com")); // double dots
        assertFalse(BankAccount.isEmailValid(".abc@mail.com")); // dot at start
        assertFalse(BankAccount.isEmailValid("a".repeat(65) + "@b.com")); // 65 characters

        // False cases - Domain
        assertFalse(BankAccount.isEmailValid("abc.def@mail.c")); // tld too short
        assertFalse(BankAccount.isEmailValid("abc.def@mail#archive.com")); // illegal special character
        assertFalse(BankAccount.isEmailValid("abc.def@mail")); // no tld
        assertFalse(BankAccount.isEmailValid("abc.def@mail..com")); // double dots
        assertFalse(BankAccount.isEmailValid("abc@" + "abcdefg.".repeat(31) + "abcdef")); // domain too long
        assertFalse(BankAccount.isEmailValid("abc@" + "c".repeat(64) + ".com")); // subdomain too long
        assertFalse(BankAccount.isEmailValid("abc@mail." + "c".repeat(64))); // tld too long

        // Valid Cases - Local
        assertTrue(BankAccount.isEmailValid("abc-d@mail.com"));
        assertTrue(BankAccount.isEmailValid("abc.def@mail.com"));
        assertTrue(BankAccount.isEmailValid("abc@mail.com"));
        assertTrue(BankAccount.isEmailValid("abc_def@mail.com"));
        assertTrue(BankAccount.isEmailValid("a".repeat(64) + "@b.com")); // 64
                                                                         // characters

        // Valid Cases - Domain
        assertTrue(BankAccount.isEmailValid("abc.def@mail.cc"));
        assertTrue(BankAccount.isEmailValid("abc.def@mail-archive.com"));
        assertTrue(BankAccount.isEmailValid("abc.def@mail.org"));
        assertTrue(BankAccount.isEmailValid("abc.def@mail.com"));
        assertTrue(BankAccount.isEmailValid("abc@" + "abcdefg.".repeat(31) + "abcde"));
        assertTrue(BankAccount.isEmailValid("abc@" + "c".repeat(63) + ".com"));
        assertTrue(BankAccount.isEmailValid("abc@mail." + "c".repeat(63)));
    }

    @Test
    void constructorTest() {
        BankAccount bankAccount = new BankAccount("a@b.com", 200);

        assertEquals("a@b.com", bankAccount.getEmail());
        assertEquals(200, bankAccount.getBalance(), 0.001);
        // check for exception thrown correctly
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("", 100));

        // check for invalid amount exception
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", -100));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("a@b.com", 100.001));
    }

}