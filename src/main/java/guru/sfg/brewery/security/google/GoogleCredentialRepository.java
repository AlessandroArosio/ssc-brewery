package guru.sfg.brewery.security.google;

import com.warrenstrange.googleauth.ICredentialRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {
    private final UserRepository userRepository;

    @Override
    public String getSecretKey(String userName) {
        var user = userRepository.findByUsername(userName).orElseThrow();
        return user.getGoogle2FaSecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        var user = userRepository.findByUsername(userName).orElseThrow();
        user.setGoogle2FaSecret(secretKey);
        user.setUseGoogle2fa(Boolean.TRUE);

        userRepository.save(user);
    }
}
