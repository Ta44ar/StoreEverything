package com.niedzwiecki_syperek.StoreEverything.Services;

import com.niedzwiecki_syperek.StoreEverything.Repositories.InformationRepository;
import com.niedzwiecki_syperek.StoreEverything.Repositories.UserRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Role;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private InformationRepository informationRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
        return new User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toList());
    }

    public UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    public List<UserEntity> findAllUsersToShareInformation(Long informationId, Long currentUserId) {
        Information information = informationRepository.findById(informationId).orElse(null);
        if (information != null) {
            List<UserEntity> allUsers = userRepo.findAll();
            List<UserEntity> sharedUsers = information.getSharedWithUsers();
            return allUsers.stream()
                    .filter(user -> !sharedUsers.contains(user))
                    .filter(user -> !user.getId().equals(currentUserId))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Information with id " + informationId + " not found.");
        }
    }
}
