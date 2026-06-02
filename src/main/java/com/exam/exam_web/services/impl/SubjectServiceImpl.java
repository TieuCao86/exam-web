package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.SubjectDTO;
import com.exam.exam_web.entity.Subject;
import com.exam.exam_web.mapper.SubjectMapper;
import com.exam.exam_web.repository.SubjectRepository;
import com.exam.exam_web.services.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            SubjectMapper subjectMapper
    ) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
    }

    // ================= CREATE =================

    @Override
    public SubjectDTO createSubject(SubjectDTO dto) {

        Subject subject = subjectMapper.toEntity(dto);
        Subject saved = subjectRepository.save(subject);

        return subjectMapper.toDTO(saved);
    }

    // ================= FIND BY ID =================

    @Override
    public SubjectDTO findById(String id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));

        return subjectMapper.toDTO(subject);
    }

    // ================= FIND ALL =================

    @Override
    public List<SubjectDTO> findAll() {

        return subjectRepository.findAll()
                .stream()
                .map(subjectMapper::toDTO)
                .toList();
    }

    // ================= UPDATE =================

    @Override
    public SubjectDTO updateSubject(String id, SubjectDTO dto) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));

        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        subject.setImage(dto.getImage());

        return subjectMapper.toDTO(subjectRepository.save(subject));
    }

    // ================= DELETE =================

    @Override
    public void deleteSubject(String id) {

        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject not found: " + id);
        }

        subjectRepository.deleteById(id);
    }

    // ================= CUSTOM QUERY =================

    @Override
    public String getSubjectImageByCourseId(String courseId) {
        return subjectRepository.getSubjectImageByCourseId(courseId);
    }
}