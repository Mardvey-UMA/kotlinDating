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
@Table("_user")
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private String email;
    private Long vkId;

    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
