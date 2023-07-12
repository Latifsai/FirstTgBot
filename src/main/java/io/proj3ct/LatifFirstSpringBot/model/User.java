package io.proj3ct.LatifFirstSpringBot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "userDataTable")
@Data
public class User {
    @Id
    private Long chatId;
    private String forename;
    private String surname;
    private String userName;
    private LocalDateTime registeredAT;
}
