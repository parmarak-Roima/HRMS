import './App.css'
import Login from './Pages/Auth/Login'
import { ToastContainer } from 'react-toastify'
import { Route , Routes } from 'react-router-dom'
import Home from './Pages/Home'
import Layout from './Layout/Layout'
import AuthUserContextProvider from './Contexts/AuthUserContext'
import Profile from './Pages/Profile'
import CreateTravel from './Pages/Travel/CreateTravel'
import ShowTravelDetails from './Pages/Travel/ShowTravelDetails'
import MyTravels from './Pages/Travel/MyTravel'
import UploadDocument from './Pages/Travel/UploadDocument'
import TeamTravel from './Pages/Travel/TeamTravel'
import TravelDocEmployee from './Componenets/TravelDocEmployee'
function App() {

  return (
    <>
      <ToastContainer
            position="top-right"
            autoClose={4000}
            hideProgressBar={false}
          />
       <Routes>
          <Route path="/" element={<Layout />}>
          <Route
              path="/"
              element={
                <Home />
              }
            />
            <Route
              path="/profile/:id"
              element={
                <Profile />
              }
            />
          <Route
              path="/login"
              element={
                <Login />
              }
            />
             <Route
              path="/travel"
              element={
                <MyTravels />
              }
            />
            <Route
              path="/travel/:travelId/:empId"
              element={
                <ShowTravelDetails />
              }
            />
             <Route
              path="/travel/create"
              element={
                <CreateTravel />
              }
            />
             <Route
              path="/travel/uploadDocs/:ownerId/:travelId"
              element={
                <UploadDocument />
              }
            />
             <Route
              path="/travel/manager/:managerId"
              element={
                <TeamTravel />
              }
            />
              <Route
              path="/travelDoc/:travelId/:empId"
              element={
                <TravelDocEmployee />
              }
            />
           </Route>
          
       </Routes>
    </>
    
  )
}

export default App
