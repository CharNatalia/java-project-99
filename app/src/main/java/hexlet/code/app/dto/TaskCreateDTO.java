package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskCreateDTO {
    @NotBlank
    private String title;
    private Integer index;
    private String content;

    @NotNull
    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;
    @JsonProperty("taskLabelIds")
    private List<Long> taskLabelIds;
}
