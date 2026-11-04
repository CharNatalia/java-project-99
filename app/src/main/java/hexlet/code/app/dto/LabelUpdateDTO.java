package hexlet.code.app.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelUpdateDTO {
    @Size(min = 3, max = 10000)
    private String name;
}
