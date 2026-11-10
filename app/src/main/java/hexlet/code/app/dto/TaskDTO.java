package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
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


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskDTO taskDTO = (TaskDTO) o;
        return getId() != null && Objects.equals(getId(), taskDTO.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
