package eu.senla.userservice.entity;

import eu.senla.common.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    private String username;

    @Email(regexp = ".+[@].+[\\.].+")
    @NotNull
    @Column
    private String email;

    @NotNull
    @Column
    private String password;

    @Column(name = "date_birth")
    private LocalDate dateBirth;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "language")
    @Type(type = "pgsql_enum")
    private Language language;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(columnDefinition = "role")
    @Type(type = "pgsql_enum")
    private Role role;

    @Column(name = "refresh_token")
    @NotNull
    private String refreshToken;

    @Column(name = "access_token")
    @NotNull
    private String accessToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
