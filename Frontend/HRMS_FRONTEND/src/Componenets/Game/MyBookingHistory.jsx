import React, { useEffect, useState } from "react";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { getMyBookingHistory } from "../../Services/BookingRequest";
import { handleGlobalError } from "../../Services/GlobalExceptionService";

function MyBookingHistory() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const [bookingRequests, setBookingRequests] = useState([]);
  useEffect(() => {
    fetchMyBookingRequestHistory();
  }, []);

  const fetchMyBookingRequestHistory = async () => {
    try {
      const res = await getMyBookingHistory();
      console.log(res.data)
      setBookingRequests(res.data);
    } catch (e) {
      handleGlobalError(e);
    }
  };

  return (
    <>
      <div className="w-full bg-gray-100 p-6">
        <div className="max-w-4xl mx-auto space-y-6">
          <div className="bg-white rounded-2xl shadow p-6">
            <h2 className="text-2xl text-center font-semibold mb-4">
              Booking History
            </h2>
            <div className="space-y-4">
              {bookingRequests.map((bookingRequest) => (
                <div
                  key={bookingRequest.id}
                  className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                >
                    <div className="">
                    <div >
                    <p className="text-sm text-gray-500">Game</p>
                    <p className="font-medium text-gray-800">
                      {bookingRequest?.gameName}
                    </p>
                  </div>
                   <div >
                    <p className="text-sm text-gray-500">Primary-Email</p>
                    <p className="font-medium text-gray-800">
                      {bookingRequest?.primaryBookedEmailId}
                    </p>
                  </div>
                  <div >
                    <p className="text-sm text-gray-500">Start-Time</p>
                    <p className="font-medium text-gray-800">
                      {bookingRequest?.startTime}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">End-Time</p>
                    <p className="font-medium text-gray-800">
                      {bookingRequest?.endTime}
                    </p>
                  </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default MyBookingHistory;
