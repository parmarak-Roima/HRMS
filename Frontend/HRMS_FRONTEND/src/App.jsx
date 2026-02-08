import './App.css'
import Navbar from './Layout/Navbar'
import Login from './Pages/Auth/Login'
import { ToastContainer } from 'react-toastify'
import { Route , Routes } from 'react-router-dom'
import Home from './Pages/Home'

function App() {

  return (
    <>
     <Navbar />
    
      <ToastContainer
            position="top-right"
            autoClose={4000}
            hideProgressBar={false}
          />
       <Routes>
          <Route
              path="/"
              element={
                <Home />
              }
            />
          <Route
              path="/login"
              element={
                <Login />
              }
            />
       </Routes>
    </>
  )
}

export default App
