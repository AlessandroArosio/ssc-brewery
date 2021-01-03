package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {
    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Login failure");

        // the only way here is to inspect first in the debugger the concrete type of objects and then cast them
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            var token = (UsernamePasswordAuthenticationToken) event.getSource();
            var builder = LoginFailure.builder();

            if (token.getPrincipal() instanceof String) {
                var user = (String) token.getPrincipal();
                builder.username(user);

                userRepository.findByUsername(user).ifPresent(builder::user);

                log.debug("Failed login for: {}", user);
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                var details = (WebAuthenticationDetails) token.getDetails();
                builder.sourceIp(details.getRemoteAddress());

                log.debug("Source IP: {}", details.getRemoteAddress());
            }
            var loginFailure = loginFailureRepository.save(builder.build());
            log.debug("Failure event ID {}", loginFailure.getId());

            if (loginFailure.getUser() != null) {
                lockUserAccount(loginFailure.getUser());
            }
        }
    }

    private void lockUserAccount(User user) {
        var failures = loginFailureRepository.findAllByUserAndCreatedDateIsAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        if (failures.size() > 3) {
            log.debug("Locking user account...");
            user.setAccountNotLocked(false);
            userRepository.save(user);
        }
    }
}
