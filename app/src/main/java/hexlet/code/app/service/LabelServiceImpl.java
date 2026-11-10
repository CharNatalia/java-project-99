package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    private static final String LABEL_NOT_FOUND = "Label not found";

    @Override
    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND));
        return labelMapper.map(label);
    }

    @Override
    public List<LabelDTO> findAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map).toList();
    }

    @Transactional
    @Override
    public LabelDTO createLabel(LabelCreateDTO dto) {
        var label = labelMapper.map(dto);
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    @Override
    public LabelDTO updateLabel(Long id, LabelUpdateDTO dto) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LABEL_NOT_FOUND));
        labelMapper.update(dto, label);
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    @Override
    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }
}
