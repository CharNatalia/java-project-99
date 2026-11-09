package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    LabelDTO findById(Long id);

    List<LabelDTO> findAll();

    LabelDTO createLabel(LabelCreateDTO dto);

    LabelDTO updateLabel(Long id, LabelUpdateDTO dto);

    void deleteLabel(Long id);
}
