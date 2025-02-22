package com.payMyBuddy.model;

import com.payMyBuddy.exception.SelfAddContactException;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users", schema = "pay_my_buddy")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Account> accounts;

    @ManyToMany
    @JoinTable(
            name = "user_contacts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private Set<User> contacts;

    public void addContact(User contact) {
        if (this.equals(contact)) {
            throw new SelfAddContactException("Un utilisateur ne peut pas s'ajouter lui-même.");
        }
        if (!contacts.contains(contact)) {
            contacts.add(contact);
            contact.getContacts().add(this);
        }
    }

    public void removeContact(User contact) {
        if (contacts.contains(contact)) {
            contacts.remove(contact);
            contact.getContacts().remove(this);
        }
    }

}
