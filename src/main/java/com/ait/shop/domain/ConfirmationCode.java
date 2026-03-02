package com.ait.shop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "confirmation_code")
public class ConfirmationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @Column(name = "expiration")
    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private User user;

    public ConfirmationCode() {
    }

    public ConfirmationCode(String value, LocalDateTime expiration, User user) {
        this.value = value;
        this.expiration = expiration;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ConfirmationCode confirmationCode)) {
            return false;
        }

        return id != null && Objects.equals(id, confirmationCode.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Confirmation code: id - %d, value - %s, expiration - %s, user email - %s",
                id,
                value,
                expiration == null ? "unknown" : expiration,
                user == null || user.getEmail() == null ? "unknown" :user.getEmail()
        );
    }
}
