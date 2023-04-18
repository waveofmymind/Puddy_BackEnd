package com.team.puddy.domain.user.repository;

import com.team.puddy.domain.type.JwtProvider;
import com.team.puddy.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, UserQueryRepository {

    Optional<User> findByAccount(String account);

    Optional<User> findByUsernameAndEmail(String username, String email);

    Optional<User> findByAccountAndUsernameAndEmail(String account, String username, String email);


    Optional<User> findByAccountAndEmail(String sub, String email);
}
