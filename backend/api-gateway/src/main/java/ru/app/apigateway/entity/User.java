package ru.app.apigateway.entity;

import ru.app.apigateway.enums.Provider;
import ru.app.apigateway.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("_users")
public class User {
    @Id
    private Long id;

    private String username;

    private String email;

    private String password;

    //private UserRole role; // ?

    private Long vkId;

    private Provider provider;

    private boolean accountLocked;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
