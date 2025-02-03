package com.demo.authService.service.impl;

import com.demo.authService.constants.ErrorMessages;
import com.demo.authService.exceptions.UserAlreadyExistsException;
import com.demo.authService.exceptions.UserNotFoundException;
import com.demo.authService.exceptions.WrongPasswordException;
import com.demo.authService.models.Session;
import com.demo.authService.models.SessionStatus;
import com.demo.authService.models.User;
import com.demo.authService.repository.SessionRepo;
import com.demo.authService.repository.UserRepo;
import com.demo.authService.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final SessionRepo sessionRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    SecretKey key = Jwts.SIG.HS256.key().build();

    public AuthServiceImpl(UserRepo userRepo, BCryptPasswordEncoder bCryptPasswordEncoder, SessionRepo sessionRepo) {
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepo = sessionRepo;
    }

    @Override
    public boolean signup(String email, String password) throws UserAlreadyExistsException {
        if(userRepo.findByEmail(email)!= null) {
            throw new UserAlreadyExistsException(String.format(ErrorMessages.USER_ALREADY_EXISTS, email));
        }
        else{
            User user = new User();
            user.setEmail(email);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            userRepo.save(user);
            return true;
        }
    }

    @Override
    public String login(String email, String password) {
        User user = userRepo.findByEmail(email);
        if(Objects.isNull(user)) {
            throw new UserNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, email));
        }

        boolean matches = bCryptPasswordEncoder.matches(password, user.getPassword());
        if(matches) {
            String token = createJwtToken(user.getId(), new ArrayList<>(), user.getEmail());

            Session session = new Session();
            session.setToken(token);
            session.setUser(user);
            session.setExpiringAt(get30DaysExpiryDate());
            session.setSessionStatus(SessionStatus.ACTIVE);
            sessionRepo.save(session);

            return token;
        }
        else{
            throw new WrongPasswordException(String.format(ErrorMessages.WRONG_PASSWORD, password));
        }

    }

    @Override
    public boolean validate(String token) {
        try{
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Date expiration = claims.getPayload().getExpiration();
            Long userId = claims.getPayload().get("userId", Long.class);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    private String createJwtToken(Long userId, List<String> roles, String email){
        Map<String, Object> dataInJwt = new HashMap<>();
        dataInJwt.put("userId", userId);
        dataInJwt.put("roles", roles);
        dataInJwt.put("email", email);

        String token = Jwts.builder()
                .claims(dataInJwt)
                .issuer("ujjawal.dev")
                .issuedAt(new Date())
                .expiration(get30DaysExpiryDate())
                .signWith(key)
                .compact();

        return token;

    }

    private Date get30DaysExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        return calendar.getTime();
    }
}
