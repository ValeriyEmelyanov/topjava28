package ru.javawebinar.topjava.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UsersUtil;
import ru.javawebinar.topjava.util.exception.EmailDuplicationException;

import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.UsersUtil.prepareToSave;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service("userService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final MessageSource messageSource;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        checkEmailDuplication(user.getEmail(), null);
        return prepareAndSave(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id), id);
    }

    public User get(int id) {
        return checkNotFoundWithId(repository.get(id), id);
    }

    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        return checkNotFound(repository.getByEmail(email), "email=" + email);
    }

    @Cacheable("users")
    public List<User> getAll() {
        return repository.getAll();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void update(User user) {
        Assert.notNull(user, "user must not be null");
//      checkNotFoundWithId : check works only for JDBC, disabled
        checkEmailDuplication(user.getEmail(), user.getId());
        prepareAndSave(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void update(UserTo userTo) {
        User user = get(userTo.id());
        checkEmailDuplication(user.getEmail(), user.getId());
        prepareAndSave(UsersUtil.updateFromTo(user, userTo));
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void enable(int id, boolean enabled) {
        User user = get(id);
        user.setEnabled(enabled);
        repository.save(user);  // !! need only for JDBC implementation
    }

    @Override
    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.getByEmail(email.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " is not found");
        }
        return new AuthorizedUser(user);
    }

    private User prepareAndSave(User user) {
        return repository.save(prepareToSave(user, passwordEncoder));
    }

    public User getWithMeals(int id) {
        return checkNotFoundWithId(repository.getWithMeals(id), id);
    }

    private void checkEmailDuplication(String email, Integer id) {
        User dbUser = repository.getByEmail(email);
        if (dbUser == null) {
            return;
        }
        if (!Objects.equals(dbUser.getId(), id)) {
            throw new EmailDuplicationException(messageSource.getMessage("exception.user.emailDuplication", null, LocaleContextHolder.getLocale()));
        }
    }
}