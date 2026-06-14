package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByContentContainingIgnoreCase(String keyword);

    List<Question> findBySubject_SubjectId(String subjectId);

    Page<Question> findBySubject_SubjectId(String subjectId, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN ExamQuestion eq ON eq.question = q WHERE eq.exam.examId = :examId")
    List<Question> findByExamId(@Param("examId") String examId);
}