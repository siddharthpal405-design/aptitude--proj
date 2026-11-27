package com.aptitude.platform.service;

import com.aptitude.platform.dto.AuthRequest;
import com.aptitude.platform.dto.AuthResponse;
import com.aptitude.platform.dto.RegisterRequest;
import com.aptitude.platform.dto.UserDto;
import com.aptitude.platform.model.Role;
import com.aptitude.platform.model.User;
import com.aptitude.platform.model.UserStreak;
import com.aptitude.platform.repository.UserRepository;
import com.aptitude.platform.repository.UserStreakRepository;
import com.aptitude.platform.security.CustomUserDetails;
import com.aptitude.platform.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserStreakRepository userStreakRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            UserStreakRepository userStreakRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.userStreakRepository = userStreakRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                Role.ROLE_STUDENT
        );
        user = userRepository.save(user);

        // Initialize streak for user
        UserStreak streak = new UserStreak(user);
        userStreakRepository.save(streak);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, mapToDto(user));
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, mapToDto(user));
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return mapToDto(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    private UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
