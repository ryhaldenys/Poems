package ua.poems_club.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/login",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> authenticate(AuthenticationRequestDto request){
        var response = authenticationService.authenticate(request);
        return getEntityResponseWithOkStatus(response);
    }

    @PostMapping(value = "/register",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> register(RegistrationRequestDto request){

        var createdAuthor = authorService.createAuthor(request);
        var response = authenticationService.authenticate(createdAuthor, request.password());

        return getEntityResponseWithOkStatus(response);
    }


    private <T> ResponseEntity<?> getEntityResponseWithOkStatus(T response){
        return ResponseEntity.ok(response);
    }
}

