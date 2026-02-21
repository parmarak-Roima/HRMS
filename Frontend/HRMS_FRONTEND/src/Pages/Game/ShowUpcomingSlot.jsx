import React, { useEffect, useState } from "react";
import { getAllUpcomingSlot } from "../../Services/gameSlot";
import { sl, tr } from "zod/locales";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { Loader } from "../../components/ui/Loader";
import { useNavigate, useParams } from "react-router-dom";

function ShowUpcomingSlot() {
    const {gameId} = useParams();
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    fetchUpcomingSlots();
  }, []);
  const navigate = useNavigate()
  const fetchUpcomingSlots = async () => {
    try {
      setLoading(true);
      console.log(gameId);
      const res = await getAllUpcomingSlot(gameId);
      setSlots(res.data);
      console.log(res)
      setLoading(false);
    } catch (e) {
      handleGlobalError(e);
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {loading ? (
        <Loader size={32} />
      ) : (
        <div className="w-full bg-gray-100 p-6">
          <div className="max-w-4xl mx-auto space-y-6">
            <div className="bg-white rounded-2xl shadow p-6">
              <div>
                <h2 className="text-2xl text-center font-semibold">
                  Upcoming-Slots
                </h2>
                <div className="space-y-4 mt-10">
                  {slots.map((slot) => (
                    <div
                      key={slot?.id}
                      className="bg-white shadow rounded-lg p-4 border border-gray-200 grid md:grid md:grid-cols-6 md:gap-10"
                    >
                        <div>
                            <p className="text-sm text-gray-500">Game</p>
                            <p className="font-medium text-gray-800">
                              {slot?.gameName}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Date</p>
                            <p className="font-medium text-gray-800">
                              {slot?.date}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Start-Time</p>
                            <p className="font-medium text-gray-800">
                              {slot?.startTime}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">End-Time</p>
                            <p className="font-medium text-gray-800">
                              {slot?.endTime}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">Slot-Status</p>
                            <p className="font-medium text-gray-800">
                              {slot?.status}
                            </p>
                          </div>
                           <div>
                              <button
                                disabled={loading}
                                onClick={() =>
                                  navigate(`/game/slot/${slot.id}/${slot.gameId}`)
                                }
                                className="w-full bg-black text-white font-medium py-2 rounded "
                              >
                                Book
                              </button> 
                        </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default ShowUpcomingSlot;
