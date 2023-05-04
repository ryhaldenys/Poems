package ua.poems_club.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.dto.author.PasswordDto;
import ua.poems_club.dto.author.UpdateAuthorDto;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.IncorrectAuthorDetailsException;
import ua.poems_club.exception.InvalidImageException;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.security.dto.RegistrationRequestDto;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static ua.poems_club.generator.AuthorGenerator.generateAuthorWithoutId;
import static ua.poems_club.generator.AuthorGenerator.generateAuthorsWithoutId;
import static ua.poems_club.generator.PoemGenerator.generatePoemsWithoutId;
import static ua.poems_club.model.Poem.Status.PRIVATE;

@SpringBootTest
@RequiredArgsConstructor
@ActiveProfiles(profiles = "test")
@Transactional
public class AuthorServicesTest {
    @Autowired
    private DataMapperAuthorService gettingDataAuthorService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ManagementAuthorService managementAuthorService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AuthorRepository authorRepository;
    private List<Author> authors;
    private List<Poem> poems;

    @Value("${DEFAULT_IMAGE}")
    private String defaultName;

    @BeforeEach
    void setUp() {
        addDataToDB();
    }

    @Test
    void getAllAuthorsFromEmptyTableTest(){
        authorRepository.deleteAll();
        var pageable = PageRequest.of(0,10);
        assertThatException()
                .isThrownBy(()-> gettingDataAuthorService.getAllAuthors(1L,"",pageable));

    }

    @Test
    void getAllAuthorsTest(){
        var author = authors.get(0);
        var currentUser = authors.get(1).getId();
        var pageable = PageRequest.of(0,10);
        var authors = gettingDataAuthorService.getAllAuthors(currentUser,"",pageable).getContent();

        assertThat(authors.get(0).getId()).isEqualTo(author.getId());

    }

    @Test
    void getAuthorByIdTest(){
        var author = authors.get(2);
        var foundAuthor = gettingDataAuthorService.getAuthorById(author.getId());

        assertThat(foundAuthor.getId()).isEqualTo(author.getId());

    }

    @Test
    void getAuthorByIdWhenUserIsAbsentTest(){
        assertThatThrownBy(()-> gettingDataAuthorService.getAuthorById(100042L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateAuthorTest(){
        var id = authors.get(0).getId();
        var updateAuthorDto = new UpdateAuthorDto("Denys Ryhal","new@gmail.com","hello");
        managementAuthorService.updateAuthor(id,updateAuthorDto);
        authorRepository.flush();

        var author = authorRepository.findById(id)
                .orElseThrow();

        assertThat(author.getFullName()).isEqualTo(updateAuthorDto.fullName());
        assertThat(author.getEmail()).isEqualTo(updateAuthorDto.email());
        assertThat(author.getDescription()).isEqualTo(updateAuthorDto.description());
    }

    @Test
    void createAuthorTest(){

        var request = new RegistrationRequestDto("fullName","email","password");

        var author = managementAuthorService.createAuthor(request);
        authorRepository.flush();

        var optionalAuthor = authorRepository.findById(author.getId());

        assertThat(optionalAuthor.isPresent()).isTrue();
    }

    @Test
    void createAuthorByEmailWhichAlreadyExistTest(){
        var author = authors.get(1);
        var request = new RegistrationRequestDto("fullName",author.getEmail(),"password");

        assertThatThrownBy(()-> managementAuthorService.createAuthor(request))
                .isInstanceOf(AuthorAlreadyExist.class);

    }

    @Test
    void createAuthorByFullNameWhichAlreadyExistTest(){
        var author = authors.get(1);
        var request = new RegistrationRequestDto(author.getFullName(),"new-email","password");

        assertThatThrownBy(()-> managementAuthorService.createAuthor(request))
                .isInstanceOf(AuthorAlreadyExist.class);

    }

    @Test
    void updateAbsentAuthorTest(){
        var id = 121312423L;
        var updateAuthorDto = new UpdateAuthorDto("Denys Denys","new@gmail.com","hello");

        assertThatThrownBy(()-> managementAuthorService.updateAuthor(id,updateAuthorDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUserWithEmailWhichAlreadyExistTest(){
        var id = authors.get(0).getId();
        var email = authors.get(1).getEmail();
        var updateAuthorDto = new UpdateAuthorDto("Denys Denys",email,"hello");

        assertThatThrownBy(()-> managementAuthorService.updateAuthor(id,updateAuthorDto))
                .isInstanceOf(AuthorAlreadyExist.class);
    }

    @Test
    void updateUserWithFullNameWhichAlreadyExistTest(){
        var id = authors.get(0).getId();
        var fullName = authors.get(1).getFullName();
        var updateAuthorDto = new UpdateAuthorDto(fullName,"new@gmail.com","hello");

        assertThatThrownBy(()-> managementAuthorService.updateAuthor(id,updateAuthorDto))
                .isInstanceOf(AuthorAlreadyExist.class);
    }


    @Test
    void updatePasswordTest(){
        var author = authors.get(1);
        var authorPassword = author.getPassword();

        author.setPassword(encoder.encode(author.getPassword()));
        authorRepository.flush();
        var password = new PasswordDto(authorPassword,"newpassword");

        managementAuthorService.updateAuthorPassword(author.getId(),password);
        authorRepository.flush();
        var foundAuthor = authorRepository.findById(author.getId()).orElseThrow();

        assertThat(encoder.matches(password.newPassword(), foundAuthor.getPassword()))
                .isTrue();
    }

    @Test
    void updatePasswordByWrongIdTest(){
        var author = authors.get(1);

        author.setPassword(encoder.encode(author.getPassword()));

        var password = new PasswordDto("wrongOldPassword","newpassword");

        assertThatThrownBy(()-> managementAuthorService.updateAuthorPassword(author.getId(),password))
                .isInstanceOf(IncorrectAuthorDetailsException.class);

    }

    @Test
    void getAuthorByEmailTest(){
        var author = authors.get(0);
        var foundAuthor = gettingDataAuthorService.getAuthorByEmail(author.getEmail());
        assertThat(foundAuthor).isEqualTo(author);
    }
    @Test
    void getAuthorByWrongEmailTest(){
        assertThatThrownBy(()-> gettingDataAuthorService.getAuthorByEmail("wrongemail@gmail.com"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteAuthorTest(){
        var author = authors.get(1);

        managementAuthorService.deleteAuthor(author.getId());
        authorRepository.flush();

        boolean isAuthor = authorRepository.findById(author.getId()).isPresent();

        assertThat(isAuthor).isFalse();
    }


    @SneakyThrows
    @Test
    void addAuthorImageTest(){
        var author = authors.get(2);

        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "hello.png", String.valueOf(MediaType.IMAGE_PNG),
                "Hello, World!".getBytes()
        );

        managementAuthorService.addAuthorImage(author.getId(), multipartFile);
        var foundAuthor = authorRepository.findById(author.getId()).orElseThrow();

        assertThat(foundAuthor.getImageName().contains("hello.png")).isTrue();
        imageService.removeImage(foundAuthor.getImageName());

    }


    @SneakyThrows
    @Test
    void addAuthorImageByWrongPathTest(){
        var author = authors.get(2);

        assertThatThrownBy(()-> managementAuthorService.addAuthorImage(author.getId(), null))
                .isInstanceOf(InvalidImageException.class);

    }

    @Test
    void addSubscriptionTest(){
        var author = authors.get(0);
        var subscription = authors.get(4);
        managementAuthorService.updateAuthorSubscriptions(author.getId(),subscription.getId());
        authorRepository.flush();

        var foundAuthor = authorRepository.findById(author.getId()).orElseThrow();

        assertThat(foundAuthor.getSubscriptions().contains(subscription)).isTrue();
    }

    @Test
    void removeSubscriptionTest(){
        var author = authors.get(0);
        var subscription = authors.get(1);
        author.addSubscription(subscription);

        managementAuthorService.updateAuthorSubscriptions(author.getId(),subscription.getId());
        authorRepository.flush();

        var foundAuthor = authorRepository.findById(author.getId()).orElseThrow();

        assertThat(foundAuthor.getSubscriptions().contains(subscription)).isFalse();
    }


    @Test
    @SneakyThrows
    void deleteImageTest(){
        var author = authors.get(0);

        var foundAuthor = authorRepository.findById(author.getId())
                .orElseThrow();

        managementAuthorService.deleteImage(author.getId());
        authorRepository.flush();


        assertThat(foundAuthor.getImageName()).isEqualTo(defaultName);

    }


    @Test
    void getAuthorSubscriptionsTest(){
        var author = authors.get(0);

        var subscriptions = gettingDataAuthorService
                .getAuthorSubscriptions(author.getId(),"",PageRequest.of(0,10))
                .getContent();

        var firstAuthorSubscription = authors.get(1);
        var secondAuthorSubscription = authors.get(2);

        assertThat(subscriptions.get(0).getFullName())
                .isEqualTo(firstAuthorSubscription.getFullName());

        assertThat(subscriptions.get(1).getFullName())
                .isEqualTo(secondAuthorSubscription.getFullName());
    }


    @Test
    void getAuthorSubscribersTest(){
        var author = authors.get(1);

        var subscribers = gettingDataAuthorService
                .getAuthorSubscribers(author.getId(),"",PageRequest.of(0,10))
                .getContent();

        var subscriber = authors.get(0);

        assertThat(subscribers.get(0).getFullName())
                .isEqualTo(subscriber.getFullName());
    }

    @Test
    void getAuthorLikesTest(){
        var author = authors.get(0);

        var likes = gettingDataAuthorService
                .getAuthorLikes(author.getId(),"",PageRequest.of(0,10))
                .getContent();

        assertThat(likes.get(0).getName())
                .isEqualTo(poems.get(0).getName());

        assertThat(likes.get(1).getName())
                .isNotEqualTo(poems.get(1).getName());
    }

    private void addDataToDB(){
        authors = generateAuthorsWithoutId(5);
        poems = generatePoemsWithoutId(5);
        poems.get(1).setStatus(PRIVATE);

        for (int i = 0; i < authors.size(); i++) {
            poems.get(i).addAuthor(authors.get(i));
        }
        authorRepository.saveAll(authors);

        authors.get(0).addAllLikes(new HashSet<>(poems));

        authors.get(0).addSubscription(authors.get(1));
        authors.get(0).addSubscription(authors.get(2));
        authors.get(0).addSubscription(authors.get(2));
        authors.get(3).addSubscriber(authors.get(0));

        Author currentUser = generateAuthorWithoutId();
        authorRepository.save(currentUser);
        currentUser.addLike(poems.get(1));
        authorRepository.flush();
    }
}
