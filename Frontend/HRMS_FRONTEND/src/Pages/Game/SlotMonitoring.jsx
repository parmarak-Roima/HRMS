import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { getSlotsForMonitor } from "../../Services/gameSlot";
import { getRequestsForSlot } from "../../Services/BookingRequest";

function SlotMonitoring() {
  const { gameId } = useParams();
  const { authUser } = useAuthUserContext();
  const today = new Date().toLocaleDateString('en-CA'); ;
  const [selectedDate, setSelectedDate] = useState(today);
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(false);
  const [activeSlotId, setActiveSlotId] = useState(null);
  const [slotRequests, setSlotRequests] = useState([]);
  const [requestsLoading, setRequestsLoading] = useState(false);


  if (authUser.role !== "HR") {
    return <p className="text-center mt-10">You are not authorized for this page !!</p>;
  }

  useEffect(() => {
    fetchSlots();
    setActiveSlotId(null); 
    setSlotRequests([]);
  }, [gameId, selectedDate]);

  const fetchSlots = async () => {
    try {
      setLoading(true);
      const res = await getSlotsForMonitor(gameId, selectedDate);
      console.log(res)
      setSlots(res);
    } catch (e) {
      handleGlobalError(e);
    } finally {
      setLoading(false);
    }
  };

  const handleShowRequests = async (slotId) => {
    if (activeSlotId === slotId) {
      setActiveSlotId(null);
      return;
    }
    try {
      setRequestsLoading(true);
      setActiveSlotId(slotId);
      const res = await getRequestsForSlot(slotId);
      setSlotRequests(res);
    } catch (e) {
      handleGlobalError(e);
      setActiveSlotId(null);
    } finally {
      setRequestsLoading(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold">Game Slot Monitor</h2>
        
        <div>
          <label className="mr-2 font-medium text-gray-700">Select Date:</label>
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2"
          />
        </div>
      </div>

      {loading ? (
        <p className="text-center text-gray-500">Loading slots...</p>
      ) : slots.length === 0 ? (
        <p className="text-center text-gray-500">No slots found for this date.</p>
      ) : (
        <div className="space-y-4">
          {slots.map((slot) => (
            <div key={slot?.id} className="bg-white shadow rounded-lg border border-gray-200 overflow-hidden">
              <div className="p-4 grid md:grid-cols-6 gap-4 md:gap-10 items-center">
                <div>
                  <p className="text-sm text-gray-500">Game</p>
                  <p className="font-medium text-gray-800">{slot?.gameName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Date</p>
                  <p className="font-medium text-gray-800">{slot?.date}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Start-Time</p>
                  <p className="font-medium text-gray-800">{slot?.startTime}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">End-Time</p>
                  <p className="font-medium text-gray-800">{slot?.endTime}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Slot-Status</p>
                  <p className="font-medium text-gray-800">{slot?.status}</p>
                </div>
                <div>
                  <button
                    disabled={requestsLoading && activeSlotId === slot.id}
                    onClick={() => handleShowRequests(slot.id)}
                    className="w-full bg-black text-white font-medium py-2 rounded hover:bg-gray-800 transition"
                  >
                    {activeSlotId === slot.id ? "Hide Requests" : "Show Requests"}
                  </button>
                </div>
              </div>
              {activeSlotId === slot.id && (
                <div className="bg-gray-50 p-4 border-t border-gray-200">
                  <h4 className="font-bold text-gray-700 mb-3">Booking Requests</h4>
                  
                  {requestsLoading ? (
                    <p className="text-sm text-gray-500">Loading requests...</p>
                  ) : slotRequests.length === 0 ? (
                    <p className="text-sm text-gray-500">No requests made for this slot yet.</p>
                  ) : (
                    <div className="space-y-2">
                      {slotRequests.map((req) => (
                        <div key={req.id} className="flex justify-between items-center bg-white p-3 rounded border border-gray-200 text-sm">
                          <div>
                            <span className="font-medium text-gray-800">Primary Booker: </span> 
                            {req.primaryBookedEmailId}
                          </div>
                          <div>
                            <span className="font-medium text-gray-800">Status: </span> 
                            <span className={`px-2 py-1 rounded text-xs font-bold ${
                              req.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' : 
                              req.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'
                            }`}>
                              {req.requestStatus}
                            </span>
                          </div>
                          <div>
                            <span className="font-medium text-gray-800">Participants: </span> 
                            {req.participants ? req.participants.length : 0}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}

            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default SlotMonitoring;