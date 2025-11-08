package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.UserSearchRequest;
import com.org.share_recycled_stuff.dto.response.UserSearchResponse;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.entity.enums.Role;
import com.org.share_recycled_stuff.repository.UserRepository;
import com.org.share_recycled_stuff.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<UserSearchResponse> searchUsers(UserSearchRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "ratingAverage")
        );

        Page<User> userPage = userRepository.searchUsers(
                filter.getKeyword(),
                filter.getLocation(),
                filter.getIsProxySeller(),
                pageable
        );

        return userPage.map(user -> {
            UserSearchResponse response = new UserSearchResponse();
            response.setId(user.getId());
            response.setDisplayName(user.getFullName());
            response.setAvatarUrl(user.getAvatarUrl());
            response.setLocation(user.getCity());
            response.setAverageRating(user.getRatingAverage());

            boolean isProxy = user.getAccount().getRoles().stream()
                    .anyMatch(userRole -> userRole.getRoleType() == Role.PROXY_SELLER);
            response.setProxySeller(isProxy);

            return response;
        });
    }
}
