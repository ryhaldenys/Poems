package ua.poems_club.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.security.dto.AuthenticationRequestDto;
import ua.poems_club.security.dto.RegistrationRequestDto;
import ua.poems_club.security.service.AuthenticationService;
import ua.poems_club.service.AuthorService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthorService authorService;
    private final AuthorRepository authorRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDto request){
        var response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDto request){
        authorService.createAuthor(request);

        var registeredUser = new AuthenticationRequestDto(request.email(), request.password());

        var response = authenticationService.authenticate(registeredUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        var securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response,null);
    }

}

