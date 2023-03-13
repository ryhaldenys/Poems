package ua.poems_club.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ua.poems_club.model.Author;
import ua.poems_club.security.dto.AuthenticationRequestDto;
import ua.poems_club.security.dto.AuthenticationResponseDto;
import ua.poems_club.security.model.JwtTokenProvider;
import ua.poems_club.service.AuthorService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final AuthorService service;
    private final JwtTokenProvider tokenProvider;

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request){
        var author = getAuthorByEmail(request);
        authenticateUser(request);
        String token = createToken(author);
        return getResponse(author,token);
    }

    public AuthenticationResponseDto authenticate(Author author, String password){
        authenticateUser(author.getEmail(),password);
        String token = createToken(author);
        return getResponse(author,token);
    }

    private Author getAuthorByEmail(AuthenticationRequestDto request){
        return service.getAuthorByEmail(request.email());
    }

    private void authenticateUser(AuthenticationRequestDto request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
    }

    private void authenticateUser(String email, String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }

    private String createToken(Author author) {
        return tokenProvider.createToken(author.getEmail(), author.getRole().name());
    }

    private AuthenticationResponseDto getResponse(Author author, String token){
        return new AuthenticationResponseDto(author.getId(),token);
    }
}

