package hexlet.code.app.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusUpdateDTO {
    @Size(min = 1)
    @Column(unique = true)
    private String name;

    @Size(min = 1)
    @Column(unique = true)
    private String slug;
}
