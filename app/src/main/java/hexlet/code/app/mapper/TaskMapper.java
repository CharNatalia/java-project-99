package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class TaskMapper {
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "taskLabelIds", source = "labels")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "assigneeId", source = "assignee.id")
    public abstract TaskDTO map(Task task);

    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "content")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract void update(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);

    protected Long map(Label label) {
        return label.getId();
    }
}
