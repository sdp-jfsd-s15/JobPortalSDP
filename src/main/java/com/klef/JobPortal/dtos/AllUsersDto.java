package com.klef.JobPortal.dtos;

import com.klef.JobPortal.model.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class AllUsersDto {

    private List<UserInfo> users;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long user_id;
        private String userName;
        private String email;
        private Role role;
        private String firstName;
        private String middleName;
        private String lastName;
        private String phone;
    }

    public AllUsersDto(List<UserInfo> users) {
        this.users = users;
    }
}
