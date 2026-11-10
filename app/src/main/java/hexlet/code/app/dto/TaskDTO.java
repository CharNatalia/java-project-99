package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class TaskDTO {
    private Long id;
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer index;
    private String content;
    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;
    private LocalDate createdAt;

    @JsonProperty("taskLabelIds")
    private List<Long> taskLabelIds;
}
