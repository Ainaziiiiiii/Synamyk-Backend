package synamyk.dto.admin;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean active;
    private String role;
}
