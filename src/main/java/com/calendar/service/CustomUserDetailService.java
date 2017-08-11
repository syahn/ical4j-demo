package com.calendar.service;

import com.calendar.data.CurrentUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by NAVER on 2017-08-11.
 */
@Service("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Build user Authority. some how a convert from your custom roles which are in database to spring GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        return buildUserForAuthentication(user, authorities);

    }

    private User buildUserForAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
        String username = user.getUsername();
        String password = user.getPassword();
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        CurrentUser currentUser = new CurrentUser(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);

        String fileID = UUID.randomUUID().toString();

        currentUser.setFileID(fileID);

        return currentUser;
        //If your database has more information of user for example firstname,... You can fill it here
        //CurrentUser currentUser = new CurrentUser(....)
        //currentUser.setFirstName( user.getfirstName() );
        //.....
        //return currentUser ;
    }
}
