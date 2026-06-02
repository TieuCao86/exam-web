package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.ExamHistoryDTO;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.entity.ExamHistory;
import com.exam.exam_web.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExamHistoryMapper {

    @Mapping(source = "exam.examId", target = "examId")
    @Mapping(source = "user.userId", target = "userId")

    @Mapping(source = "exam.examName", target = "examName")
    @Mapping(source = "exam.course.subject.name", target = "subjectName")

    @Mapping(source = "exam.openDate", target = "openTime")
    @Mapping(source = "exam.closeDate", target = "closeTime")
    ExamHistoryDTO toDTO(ExamHistory entity);

    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "user", ignore = true)
    ExamHistory toEntity(ExamHistoryDTO dto);

    @AfterMapping
    default void link(@MappingTarget ExamHistory entity, ExamHistoryDTO dto) {

        if (dto.getExamId() != null) {
            Exam exam = new Exam();
            exam.setExamId(dto.getExamId());
            entity.setExam(exam);
        }

        if (dto.getUserId() != null) {
            User user = new User();
            user.setUserId(dto.getUserId());
            entity.setUser(user);
        }
    }
}