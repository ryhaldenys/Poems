package ua.poems_club.security.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ua.poems_club.model.Author;

import java.util.Collection;

import static ua.poems_club.model.Author.Status.*;

@Setter
@Getter
public class SecurityUser extends User {
    private Long id;

    public SecurityUser(String username, String password,
                        boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                        boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }

    public static UserDetails fromUser(Author user) {
        var status = checkStatusIsActive(user);
        return new SecurityUser(
                user.getEmail(), user.getPassword(),
                status, status, status, status,
                user.getRole().getAuthorities(),
                user.getId()
        );
    }

    private static boolean checkStatusIsActive(Author user){
        return user.getStatus().equals(ACTIVE);
    }
}
