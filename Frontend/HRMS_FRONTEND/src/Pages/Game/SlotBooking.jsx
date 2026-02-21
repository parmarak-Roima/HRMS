import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { getGameInterestedPlayerByGame } from "../../Services/GameInterest";
import { Loader } from "../../components/ui/Loader";
import Select from "react-select";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { gameById } from "../../Services/Game";
import { toast } from "react-toastify";
import { makeBookingRequest } from "../../Services/BookingRequest";

function SlotBooking() {
  const { slotId } = useParams();
  const { gameId } = useParams();
  const [loading, setLoading] = useState(true);
  const [employees, setEmployees] = useState([]);
  const [participants, setParticipants] = useState([]);
  const { authUser, setAuthUser } = useAuthUserContext();
  const [game, setGame] = useState({});
  const navigate = useNavigate()
  useEffect(() => {
    fetchInterestedByGameId();
    fetchGameByGameId();
  }, []);
  const options = employees.map((emp) => ({
    value: emp.id,
    label: emp.email,
  }));
  const handleChange = (selected) => {
    if (selected.length + 1 <= game.maxPlayers) {
      setParticipants(selected);
    } else {
      toast.warn("you can not assign more then maximum player!!");
    }
  };
  const fetchInterestedByGameId = async () => {
    try {
      const res = await getGameInterestedPlayerByGame(gameId);
      console.log(res);
      setEmployees(res?.data.filter((emp) => emp.id != authUser.id));
      setLoading(false);
    } catch (e) {
      handleGlobalError(e);
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };
  const fetchGameByGameId = async () => {
    try {
      setLoading(true);
      const res = await gameById(gameId);
      console.log(res);
      setGame(res.data);
      setLoading(false);
    } catch(e){
      handleGlobalError(e);
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  const MakeBookingRequest = async () => {
    const payload = {
      slotId: slotId,
      participantsId: participants.map((option) => option.value),
    };
    try{
      if(participants.length + 1 < game.minPlayers){
        toast.warn(`assign minimum ${game.minPlayers} for booking request!! `)
        return;
      }
      setLoading(true)
      await makeBookingRequest(payload)
      toast.success("booking request made on slot successfully")
      navigate(`/game/booking`);
      setLoading(false)
    }catch(e){
      setLoading(false)
      handleGlobalError(e)
    }finally{
      setLoading(false)
    }
    console.log(payload)
  };

  return (
    <>
      {loading ? (
        <Loader size={32} />
      ) : (
        <div className="max-w-lg mx-auto my-30 p-6 bg-white rounded-lg shadow">
          <h2 className="text-2xl font-bold text-center mb-10">
            Make a Booking Request
          </h2>
          <div className="flex justify-around mb-4">
            <h3> Minimum-Player : {game.minPlayers}</h3>
            <h3>Maximum-Player: {game.maxPlayers}</h3>
          </div>
          <div className="flex justify-center gap-4">
            <div>
              <label className="block font-bold text-md ml-4 mb-4 ">
                Select Participants
              </label>
              <Select
                isMulti
                options={options}
                onChange={handleChange}
                value={participants}
              />
              <button
                type="submit"
                disabled={loading}
                onClick={MakeBookingRequest}
                className="px-4 py-1.5 mt-4 c bg-black text-white rounded hover:bg-gray-700"
              >
                Submit
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default SlotBooking;
