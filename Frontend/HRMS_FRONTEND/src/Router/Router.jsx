import * as React from "react";
import { createBrowserRouter, Outlet } from "react-router-dom";

import Layout from "../Layout/Layout";
import Home from "../Pages/Home";
import AuthUserContextProvider from "../Contexts/AuthUserContext";
import { toast, ToastContainer } from "react-toastify";
import Profile from "../Pages/Profile";
import Login from "../Pages/Auth/Login";
import MyTravels from "../Pages/Travel/MyTravel";
import ShowTravelDetails from "../Pages/Travel/ShowTravelDetails";
import CreateTravel from "../Pages/Travel/CreateTravel";
import UploadDocument from "../Pages/Travel/UploadDocument";
import TravelDocEmployee from "../Pages/Travel/TravelDocEmployee";
import CreateExpense from "../Pages/Travel/CreateExpense";
import ShowTravelExpenses from "../Pages/Travel/ShowTravelExpense";
import NotificationPage from "../Pages/Travel/NotificationPage";
import OrgChart from "../Pages/OrgChart/OrgChart";
import Jobs from "../Pages/JobOpening/Jobs";
import ShowGameHistory from "../Pages/Game/ShowGameHistory";
import AchievementsPage from "../Pages/Achievement/AchievementsPage";
import SlotMonitoring from "../Pages/Game/SlotMonitoring";
import CreateGameConfig from "../Pages/Game/CreateGameConfig";
import UpdateGameConfig from "../Pages/Game/UpdateGameConfig";
import GameConfig from "../Pages/Game/GameConfig";
import SlotBooking from "../Pages/Game/SlotBooking";
import ShowUpcomingSlot from "../Pages/Game/ShowUpcomingSlot";
import Config from "../Pages/Config/Config";
import JobReferrals from "../Pages/JobOpening/JobReferrals";
import ShareJob from "../Pages/JobOpening/ShareJob";
import CreateJob from "../Pages/JobOpening/CreateJob";
import OrgChartNode from "../Componenets/OrgChart/OrgChartNode";
import TeamTravel from "../Pages/Travel/TeamTravel";
import ReferrJob from "../Pages/JobOpening/ReferraJob";
import Calender from "../Pages/Game/Calender";
// import Notificationssse from "../Pages/Notificationssse";
import NotFoundPage from "../Pages/NotFoundPage";
import { fetchEmployeeById } from "../Services/authService";
import { Loader } from "../components/ui/Loader";
import ProfileLoader from "../Loaders/ProfileLoader";
import UpdateTravel from "../Pages/Travel/UpdateTravel";
import AssignEmployee from "../Pages/Travel/AssignEmployee";
import OAuth2RedirectHandler from "../Pages/Auth/OAuth2RedirectHandler";

export const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <AuthUserContextProvider>
        <ToastContainer
          position="top-right"
          autoClose={4000}
          hideProgressBar={false}
        />
        <Layout />
      </AuthUserContextProvider>
    ),
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: "/profile/:id",
        lazy: () =>
          import("../Pages/Profile").then((module) => ({
            Component: module.default,
            loader: module.ProfileLoader,
          })),
        // element:  <Profile />,
        // lazy: async () => {
        //   let module = await import("../Pages/Profile");
        //   return { Component: module.default };
        // },
        loader: ProfileLoader,
        HydrateFallback: Loader,
      },
      { path: "/login", element: <Login /> },
      { path: "/oauth2/redirect", element: <OAuth2RedirectHandler /> },
      {
        path: "/travel",
        element: <Outlet />,
        children: [
          { index: true, element: <MyTravels />,

          },
          { path: ":travelId/:empId", element: <ShowTravelDetails /> },
          { path: "create", element: <CreateTravel /> },
          {
            path: "uploadDocs/:ownerId/:travelId",
            element: <UploadDocument />,
          },
          { path: "manager/:managerId", element: <TeamTravel /> },
          { path: "update/:travelId", element: <UpdateTravel /> },
          { path: "assign/:travelId", element: <AssignEmployee /> },
        ],
      },
      {
        path: "/travelDoc",
        element: <Outlet />,
        children: [
          {
            path: ":travelId/:empId",
            element: <TravelDocEmployee />,
          },
        ],
      },
      {
        path: "/travel-expense",
        element: <Outlet />,
        children: [
          {
            path: ":travelAssignmentId/Create",
            element: <CreateExpense />,
          },
          {
            path: ":travelId/:empId",
            element: <ShowTravelExpenses />,
          },
        ],
      },

      { path: "/notification/:userId", element: <NotificationPage /> },
      { path: "/orgChart/:empId", element: <OrgChart /> },
      // //---------------------------------Job Openings---------------------------------------//
      {
        path: "/jobOpening",
        element: <Outlet />,
        children: [
          { index: true, element: <Jobs /> },
          { path: "Create", element: <CreateJob /> },
          { path: "referrals/:jobId", element: <JobReferrals /> },
        ],
      },

      // //-----------------------------------config-----------------------//
      { path: "/config", element: <Config /> },

      //---------------------------------Game-Booking---------------//
      {
        path: "/game",
        element: <Outlet />,
        children: [
          { path: "booking", element: <ShowGameHistory /> },
          { path: "slot/:gameId", element: <ShowUpcomingSlot /> },
          { path: "slot/:slotId/:gameId", element: <SlotBooking /> },
          { path: "config", element: <GameConfig /> },
          { path: "config/:gameId", element: <UpdateGameConfig /> },
          { path: "create", element: <CreateGameConfig /> },
          { path: "slot-monitor/:gameId", element: <SlotMonitoring /> },
          { path: "calender", element: <Calender /> },
        ],
      },

      //------------------------------Achivements--------------------------//
      { path: "/achievements", element: <AchievementsPage /> },

      // { path: "/not", element: <Notificationssse /> },
      {
        path: "*",
        element: <NotFoundPage />,
      },
      {
        future: {
          v7_partialHydration: true,
        },
        hydrationData: {
          root: {
            /*...*/
          },
          // No hydration data provided for the `invoice` route
        },
      },
    ],
  },
]);
