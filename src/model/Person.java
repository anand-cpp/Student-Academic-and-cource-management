package model;

import java.time.LocalDate;
import util.DateUtils;
import util.InputValidator;

/**
 * Abstract base class for people in the academic system.
 *
 * <p>This demonstrates INHERITANCE: Student and Faculty extend Person and
 * inherit the genuinely shared attributes (id, name, email, phone, address).
 * It demonstrates ABSTRACTION: Person is abstract and declares the abstract
 * {@link #getRole()} and {@link #getProfileSummary()} methods that subclasses
 * implement. Fields are private (ENCAPSULATION) and exposed through validated
 * getters/setters.
 */
public abstract class Person {

    protected int id;
    protected String fullName;
    protected String email;
    protected String phone;
    protected String address;
    protected LocalDate dateOfBirth;

    public Person() {
    }

    public Person(int id, String fullName, String email, String phone,
            String address, LocalDate dateOfBirth) {
        setId(id);
        setFullName(fullName);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        setDateOfBirth(dateOfBirth);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = InputValidator.name(fullName, "Full name");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = InputValidator.email(email);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = InputValidator.phone(phone);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = InputValidator.required(address, "Address");
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            DateUtils.notFuture(dateOfBirth, "Date of birth");
        }
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * The role of the person (Student or Faculty). Overridden polymorphically.
     */
    public abstract String getRole();

    /**
     * A one-line summary of the person's profile. Overridden polymorphically.
     */
    public abstract String getProfileSummary();

    @Override
    public String toString() {
        return getFullName() + " (" + getEmail() + ")";
    }
}
