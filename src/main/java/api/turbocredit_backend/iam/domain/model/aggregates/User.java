package api.turbocredit_backend.iam.domain.model.aggregates;

import api.turbocredit_backend.iam.domain.model.valueobjects.Role;
import api.turbocredit_backend.iam.domain.model.valueobjects.Roles;
import api.turbocredit_backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 255)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 255)
    private String passwordHash;

    @NotBlank
    @Size(max = 255)
    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String email, String passwordHash, String fullName) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.roles = new HashSet<>();
        this.roles.add(new Role(Roles.ROLE_USER));
    }

    public User(String email, String passwordHash, String fullName, List<Role> roles) {
        this(email, passwordHash, fullName);
        if (roles != null && !roles.isEmpty()) {
            this.roles.clear();
            this.roles.addAll(roles);
        }
    }

    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }
}