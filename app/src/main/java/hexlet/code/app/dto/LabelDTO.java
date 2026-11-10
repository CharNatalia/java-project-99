package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class LabelDTO {
    private Long id;
    private String name;
    private LocalDate createdAt;


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LabelDTO labelDTO = (LabelDTO) o;
        return getId() != null && Objects.equals(getId(), labelDTO.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
