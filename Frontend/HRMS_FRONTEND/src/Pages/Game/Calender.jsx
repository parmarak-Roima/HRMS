import React, { useEffect, useState } from "react";
import { Calendar, momentLocalizer } from "react-big-calendar";
import moment from "moment";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { getMyBookingHistory } from "../../Services/BookingRequest";
moment.locale("en-GB");
const localizer = momentLocalizer(moment);

export default function ReactBigCalendar() {
  const [eventsData, setEventsData] = useState([]);
  const { authUser, setAuthUser } = useAuthUserContext();

  useEffect(() => {
    fetchMyBookingRequest();
  }, []);

  function convertToDateObjects(dateStr, startStr, endStr) {
    const [year, month, day] = dateStr.split("-");
    const jsMonth = month - 1;

    const [startHours, startMinutes, startSeconds] = startStr
      .split(":");
    const [endHours, endMinutes, endSeconds] = endStr.split(":");

    const startDate = new Date(
      year,
      jsMonth,
      day,
      startHours,
      startMinutes,
      startSeconds,
    );
    const endDate = new Date(
      year,
      jsMonth,
      day,
      endHours,
      endMinutes,
      endSeconds,
    );

    return {
      start: startDate,
      end: endDate,
    };
  }

  const fetchMyBookingRequest = async () => {
    const res = await getMyBookingHistory(authUser.id);
    const CONFIRMED = res?.data.filter((br) => br.requestStatus === "CONFIRMED");
    const events = CONFIRMED.map((br) => {
      const term = convertToDateObjects(br.slotDate, br.startTime, br.endTime);
      return {
        id: br.id,
        title: `Slot booked for ${br.gameName} `,
        start: term.start,
        end: term.end,
      };
    });
    setEventsData(events);
  };

  return (
    <div className="App">
      <Calendar
        className="mt-10"
        views={["day", "agenda","week", "work_week", "month"]}
        selectable
        localizer={localizer}
        defaultDate={new Date()}
        defaultView="month"
        events={eventsData}
        style={{ height: "100vh" }}
        onSelectEvent={(event) => alert(event.title)}
      />
    </div>
  );
}
