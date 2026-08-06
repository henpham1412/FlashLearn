package com.hen.flastcard.utils;

import com.hen.flastcard.entity.User;
import com.hen.flastcard.service.CurrentUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSecurity {
    CurrentUserService currentUserService;
    public boolean isOwner(Long userId) {

        User current = currentUserService.getCurrentUser();

        return current.getId().equals(userId);

    }
}
