package ua.poems_club.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.security.model.SecurityUser;


@Service
@Primary
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthorRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Author user = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User doesn't exist"));
        return SecurityUser.fromUser(user);
    }
}
