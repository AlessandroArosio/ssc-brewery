package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginSuccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
    private final LoginSuccessRepository loginSuccessRepository;

    @EventListener
    public void listen(AuthenticationSuccessEvent event) {

        // the only way here is to inspect first in the debugger the concrete type of objects and then cast them
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            var builder = LoginSuccess.builder();

            var token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof User) {
                var user = (User) token.getPrincipal();
                builder.user(user);

                log.debug("User '{}' logged in ok", user.getUsername());
            }

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                var details = (WebAuthenticationDetails) token.getDetails();
                builder.sourceIp(details.getRemoteAddress());

                log.debug("Source IP: {}", details.getRemoteAddress());
            }

            var loginSuccess = loginSuccessRepository.save(builder.build());

            log.debug("Login successfully saved. Id: {}", loginSuccess.getId());
        }

    }
}
