import { useState, useEffect } from 'react'

function CourseList() {

    const[courses, setCourses] = useState([])
    useEffect(() => {
        fetch('http://localhost:8080/courses')
            .then(response => response.json())
            .then(data => setCourses(data))
            .catch(error => console.error('Error fetching courses:', error));
    }, []);
    return (

        <div>
            {courses.map((course) =>(
                <div key={course.courseId}>
                    <h2>{course.courseName}</h2>
                    <p>{course.academicYear}</p>
                </div>
            ))}
        </div>
    )
}

export default CourseList
