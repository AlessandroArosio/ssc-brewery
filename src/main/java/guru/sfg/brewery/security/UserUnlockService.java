package guru.sfg.brewery.security;

import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUnlockService {
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 5000)
    public void unlockAccounts() {
        log.debug("Running Unlock accounts...");

        var lockedUsers = userRepository.findAllByAccountNotLockedAndLastModifiedDateIsBefore(
                Boolean.FALSE, Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));

        if (!lockedUsers.isEmpty()) {
            log.debug("Locked accounts found. Unlocking.");

            lockedUsers.forEach(u -> u.setAccountNotLocked(true));
        }

        userRepository.saveAll(lockedUsers);
    }
}
