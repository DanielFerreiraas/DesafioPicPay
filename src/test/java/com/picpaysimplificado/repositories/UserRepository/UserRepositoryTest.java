package com.picpaysimplificado.repositories.UserRepository;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.user.UserDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Shold get User successfully from db")
    void findUserByDocument() {
        String document = "99999999901";
        UserDTO data = new UserDTO(
            "Daniel",
            "Ferreira",
            document,
            new BigDecimal(10),
            "daniel@gmail.com",
            "123456",
            UserType.COMMON);
            this.createUser(data);

            Optional<User> result = this.userRepository.findUserByDocument(document);

            assertThat(result.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Shold get User successfully from db")
    void findUserByDocument2() {
        String document = "99999999901";

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isEmpty()).isTrue();

    }


    private User createUser(UserDTO data){
        User newUser = new User(data);
        this.entityManager.persist(newUser);
        return newUser;
    }
}