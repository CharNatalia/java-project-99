package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class TaskStatusDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskStatusDTO taskStatusDTO = (TaskStatusDTO) o;
        return getId() != null && Objects.equals(getId(), taskStatusDTO.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
