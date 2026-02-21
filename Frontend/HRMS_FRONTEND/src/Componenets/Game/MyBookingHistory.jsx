import React, { useEffect, useState } from "react";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import {
  cancelBooking,
  getMyBookingHistory,
} from "../../Services/BookingRequest";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { Loader } from "../../components/ui/Loader";
import { useNavigate } from "react-router-dom";

function MyBookingHistory() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const [bookingRequests, setBookingRequests] = useState([]);
  const [loading, setLoanding] = useState(true);
  const navigate = useNavigate()
  useEffect(() => {
    fetchMyBookingRequestHistory();
  }, []);

  const fetchMyBookingRequestHistory = async () => {
    try {
      const res = await getMyBookingHistory();
      console.log(res.data);
      setBookingRequests(res.data);
      setLoanding(false);
    } catch (e) {
      handleGlobalError(e);
      setLoanding(false);
    }
    {
      setLoanding(false);
    }
  };

  const cancelBookingRequest = async (bookingId) => {
    try {
      setLoanding(true);
      await cancelBooking(bookingId);
      fetchMyBookingRequestHistory();
      setLoanding(false);
    } catch (e) {
      setLoanding(false);
      handleGlobalError(e);
    } finally {
      setLoanding(false);
    }
  };

  return (
    <>
      {loading ? (
        <Loader size={32} />
      ) : (
        <>
          <div className="w-full bg-gray-100 p-6">
            <div className="max-w-4xl mx-auto space-y-6">
              <div className="bg-white rounded-2xl shadow p-6">
                <div>
                <h2 className="text-2xl text-center font-semibold">
                  Booking History
                </h2>
                </div>
                <div className="space-y-4">
                  {bookingRequests.map((bookingRequest) => (
                    <div
                      key={bookingRequest.id}
                      className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                    >
                      <div className="grid md:grid-cols-3 md:gap-30 ">
                        <div>
                          <div>
                            <p className="text-sm text-gray-500">Game</p>
                            <p className="font-medium text-gray-800">
                              {bookingRequest?.gameName}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">
                              Primary-Email
                            </p>
                            <p className="font-medium text-gray-800">
                              {bookingRequest?.primaryBookedEmailId}
                            </p>
                          </div>
                           <div>
                            <p className="text-sm text-gray-500">
                              Slot-Date                            </p>
                            <p className="font-medium text-gray-800">
                              {bookingRequest?.slotDate}
                            </p>
                          </div>
                          <div>
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
                        <div>
                          <div>
                            <p className="text-sm text-gray-500">
                              Request-Status
                            </p>
                            <p className="font-medium text-gray-800">
                              {bookingRequest?.requestStatus}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Slot-Status</p>
                            <p className="font-medium text-gray-800">
                              {bookingRequest?.slotStatus}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">
                              Request-Time
                            </p>
                            <p className="font-medium text-gray-800">
                              {bookingRequest?.requestedAt
                                ? new Date(
                                    bookingRequest.requestedAt,
                                  ).toLocaleString("en-IN", {
                                    day: "2-digit",
                                    month: "short",
                                    year: "numeric",
                                    hour: "2-digit",
                                    minute: "2-digit",
                                  })
                                : "N/A"}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">
                              Participants's-Email
                            </p>
                            {
                              !bookingRequest?.participant &&
                              "NA"
                            }
                            {bookingRequest?.participants?.map(
                              (participant) => {
                                if (
                                  participant.email !=
                                  bookingRequest.primaryBookedEmailId
                                ) {
                                  return <p>{participant?.email}</p>;
                                }
                              },
                            )}
                          </div>
                        </div>
                        <div>
                          {(bookingRequest?.requestStatus == "PENDING" ||
                            bookingRequest?.requestStatus == "CONFIRMED") &&
                            authUser.id == bookingRequest.primaryBookerId && (
                              <button
                                disabled={loading}
                                onClick={() =>
                                  cancelBookingRequest(bookingRequest?.id)
                                }
                                className="w-full bg-black text-white font-medium py-2 mt-4 rounded "
                              >
                                Cancel-Booking
                              </button>
                            )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </>
  );
}

export default MyBookingHistory;
