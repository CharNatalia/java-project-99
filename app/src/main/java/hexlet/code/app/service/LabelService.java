package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceInUseException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    private final TaskRepository taskRepository;

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        return labelMapper.map(label);
    }

    public List<LabelDTO> findAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map).toList();
    }

    @Transactional
    public LabelDTO createLabel(LabelCreateDTO dto) {
        var label = labelMapper.map(dto);
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    public LabelDTO updateLabel(Long id, LabelUpdateDTO dto) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        labelMapper.update(dto, label);
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    public void deleteLabel(Long id) {
        if (taskRepository.existsByLabelsId(id)) {
            throw new ResourceInUseException("Label in use");
        }
        labelRepository.deleteById(id);
    }
}
