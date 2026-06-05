import { useState, useEffect } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'

function App() {

  const[courses, setCourses] = useState([])
  useEffect(() => {
        fetch('http://localhost:8080/courses')
            .then(response => response.json())
            .then(data => setCourses(data))
            .catch(error => console.error('Error fetching courses:', error));
  }, []);
  return (
    <>
    <div>
      {courses.map(course => () => (
        <div key={course.id}>
          <h2>{course.name}</h2>
          <p>{course.description}</p>
        </div>
      ))}
    </div>
    </>
  )
}

export default App
