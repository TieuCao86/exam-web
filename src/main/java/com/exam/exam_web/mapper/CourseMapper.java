package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.ChapterDTO;
import com.exam.exam_web.dto.LessonDTO;
import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.entity.Course;
import com.exam.exam_web.entity.Chapter;
import com.exam.exam_web.entity.Lesson;
import com.exam.exam_web.entity.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    //
    @Mapping(target = "subjectId", source = "subject.subjectId")
    @Mapping(target = "teacherId", source = "teacher.accountId")
    @Mapping(target = "teacherName", source = "teacher.username")
    CourseDTO toDTO(Course course);

    //
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Course toEntity(CourseDTO dto);

    // Định nghĩa cách map từ Entity Chapter sang ChapterDTO
    @Mapping(target = "courseId", source = "course.courseId")
    ChapterDTO toChapterDTO(Chapter chapter);

    // Định nghĩa cách map từ Entity Lesson sang LessonDTO
    @Mapping(target = "chapterId", source = "chapter.chapterId")
    LessonDTO toLessonDTO(Lesson lesson);

    // Định nghĩa cách map từ Entity Exam sang ExamDTO (Đồng bộ id và tên khóa học)
    @Mapping(target = "courseId", source = "course.courseId")
    @Mapping(target = "courseName", source = "course.courseName")
    ExamDTO toExamDTO(Exam exam);
}