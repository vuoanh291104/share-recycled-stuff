package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.UserSearchRequest;
import com.org.share_recycled_stuff.dto.response.UserSearchResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<UserSearchResponse> searchUsers(UserSearchRequest filter);
}
