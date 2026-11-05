package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {
    @Size(min = 1)
    private String title;
    private Integer index;
    private String content;
    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;
    private List<Long> taskLabelIds;
}
