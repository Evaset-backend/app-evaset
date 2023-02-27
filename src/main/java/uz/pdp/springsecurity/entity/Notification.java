package uz.pdp.springsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.springsecurity.entity.template.AbsEntity;
import uz.pdp.springsecurity.enums.NotificationType;

import javax.persistence.Entity;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification extends AbsEntity {
    private String name;
    private String message;
    private NotificationType type;
    private UUID objectId;
    private User userFrom;
    private User userTo;
    private boolean delivery;
    private boolean read;
}
