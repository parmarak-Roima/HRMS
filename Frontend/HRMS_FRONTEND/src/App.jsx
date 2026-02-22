import './App.css'
import Login from './Pages/Auth/Login'
import { ToastContainer } from 'react-toastify'
import { Route , Routes } from 'react-router-dom'
import Home from './Pages/Home'
import Layout from './Layout/Layout'
import Profile from './Pages/Profile'
import CreateTravel from './Pages/Travel/CreateTravel'
import ShowTravelDetails from './Pages/Travel/ShowTravelDetails'
import MyTravels from './Pages/Travel/MyTravel'
import UploadDocument from './Pages/Travel/UploadDocument'
import TeamTravel from './Pages/Travel/TeamTravel'
import TravelDocEmployee from './Pages/Travel/TravelDocEmployee'
import CreateExpense from './Pages/Travel/CreateExpense'
import ShowTravelExpense from './Pages/Travel/ShowTravelExpense'
import NotificationPage from './Pages/Travel/NotificationPage'
import OrgChart from './Pages/OrgChart/OrgChart'
import Jobs from './Pages/JobOpening/Jobs'
import CreateJob from './Pages/JobOpening/CreateJob'
import ShareJob from './Pages/JobOpening/ShareJob'
import ReferraJob from './Pages/JobOpening/ReferraJob'
import Config from './Pages/Config/Config'
import JobReferrals from './Pages/JobOpening/JobReferrals'
import ShowGameHistory from './Pages/Game/ShowGameHistory'
import ShowUpcomingSlot from './Pages/Game/ShowUpcomingSlot'
import SlotBooking from './Pages/Game/SlotBooking'
import GameConfig from './Pages/Game/GameConfig'
import UpdateGameConfig from './Pages/Game/UpdateGameConfig'
import CreateGameConfig from './Pages/Game/CreateGameConfig'
import SlotMonitoring from './Pages/Game/SlotMonitoring'
import AchievementsPage from './Pages/Achievement/AchievementsPage'
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
            <Route
              path="/travel-expense/:travelAssignmentId/Create"
              element={
                <CreateExpense />
              }
            />
            <Route
              path="/travel-expense/:travelId/:empId"
              element={
                <ShowTravelExpense />
              }
            />
            <Route
              path="/notification/:userId"
              element={
                <NotificationPage />
              }
            />
             <Route
              path="/orgChart/:empId"
              element={
                <OrgChart />
              }
            />

//---------------------------------Job Openings ---------------------------------------//
             <Route
              path="/jobOpening"
              element={
                <Jobs />
              }
            />
            <Route
              path="/jobOpening/Create"
              element={
                <CreateJob />
              }
            />
             <Route
              path="/jobOpening/share/:jobId"
              element={
                <ShareJob />
              }
            />
            <Route
              path="/jobOpening/referr/:jobId"
              element={
                <ReferraJob />
              }
            />
            <Route
              path="/job-referrals/:jobId"
              element={
                <JobReferrals />
              }
            />
            //-----------------------------------config-----------------------//
             <Route
              path="/config"
              element={
                <Config />
              }
            />
            //---------------------------------Game-Booking---------------//
             <Route
              path="/game/booking"
              element={
                <ShowGameHistory />
              }
            />
             <Route
              path="/game/slot/:gameId"
              element={
                <ShowUpcomingSlot />
              }
            />
            <Route
              path="/game/slot/:slotId/:gameId"
              element={
                <SlotBooking />
              }
            />
            <Route
              path="/game/config"
              element={
                <GameConfig />
              }
            />
            <Route
              path="/game/config/:gameId"
              element={
                <UpdateGameConfig />
              }
            />
            <Route
              path="/game/create"
              element={
                <CreateGameConfig />
              }
            />
             <Route
              path="/game/slot-monitor/:gameId"
              element={
                <SlotMonitoring />
              }
            />
            <Route
  path="/achievements"
  element={
    <AchievementsPage />
  }
/>
           </Route>
       </Routes>
    </>
    
  )
}

export default App