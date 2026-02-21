import React, { useEffect, useState } from "react";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { getGameInterests, toggelInterest } from "../../Services/GameInterest";
import { Loader } from "../../components/ui/Loader";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

function GameInterest() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const [gameInterests, setGameInterests] = useState([]);
  const [loading, setLoading] = useState(true);
    const navigate = useNavigate()
  useEffect(() => {
    fetchGameInterests();
  }, [authUser]);

  const fetchGameInterests = async () => {
    try {
      const res = await getGameInterests();
      console.log(res.data)
      setGameInterests(res?.data);
      setLoading(false);
    } catch (e) {
      handleGlobalError(e);
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  const changeGameInterest = async (gameId) => {
    try{
        setLoading(true);
        await toggelInterest(gameId);
        fetchGameInterests();
        toast.success("game interest changed successfully")
        setLoading(false);
    }catch(e){
        setLoading(false)
        handleGlobalError(e)
    }finally{
        setLoading(false)
    }
  }

  return (
    <>
      {loading ? (
        <Loader size={32} />
      ) : (
        <>
          <div className="w-full bg-gray-100 p-6">
            <div className="max-w-4xl mx-auto space-y-6">
              <div className="bg-white rounded-2xl shadow p-6">
                <h2 className="text-2xl text-center font-semibold mb-4">
                    Game-Interests
                </h2>
                <div className="space-y-4">
                  {gameInterests?.map((gameInterest) => (
                    <div
                      key={gameInterest.id}
                      className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                    >
                        <div className="md:grid md:grid-cols-4 md:gap-20" >
                            <div>
                            <p className="text-sm text-gray-500">Game</p>
                            <p className="font-medium text-gray-800">
                              {gameInterest?.gameName}
                            </p>
                            </div>
                            <div>
                             <p className="text-sm text-gray-500">Interested</p>
                            <p className="font-medium text-gray-800">
                              {gameInterest?.interested ? "Yes" : "No"}
                            </p>
                            </div>
                            <div>
                             <p className="text-sm text-gray-500">Play-count</p>
                             <p className="font-medium text-gray-800">
                              {gameInterest?.playedInCurrentCycle}
                            </p>
                             
                            </div>
                            <div>
                              <button
                                onClick={() => changeGameInterest(gameInterest?.gameId)}
                                className="w-full  bg-black text-white font-medium py-2 px-3 m rounded "
                              >
                                Change-Interest
                              </button>
                              { gameInterest.interested && 
                              <button
                                onClick={() => navigate(`/game/slot/${gameInterest.gameId}`) }
                                className=" mt-4 w-full  bg-black text-white font-medium py-2 px-3 m rounded "
                              >
                                Book-slot
                              </button>
}
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

export default GameInterest;
