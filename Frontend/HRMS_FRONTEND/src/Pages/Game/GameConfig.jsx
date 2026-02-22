import React, { useEffect, useState } from "react";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { getAllGameConfig } from "../../Services/Game";
import { Loader } from "../../components/ui/Loader";
import { useNavigate } from "react-router-dom";

function GameConfig() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  useEffect(() => {
    fetchAllGameConfiguration();
  }, []);

  const fetchAllGameConfiguration = async () => {
    try {
      const res = await getAllGameConfig();
      console.log(res);
      setGames(res.data);
      setLoading(false);
    } catch (e) {
      handleGlobalError(e);
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  if (authUser.role != "HR") {
    return <p>You are not authorized to access this page.</p>;
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
                  Game-Config
                </h2>
                <button onClick={() => navigate("/game/create")}  className="ml-3 mb-3  bg-black text-white font-medium py-2 px-3 m rounded ">
                  Create-Game
                </button>
                <div className="space-y-4">
                  {games?.map((game) => (
                    <div
                      key={game.id}
                      className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                    >
                      <div className="grid grid-cols-3 gap-10 my-3">
                        <div>
                          <div>
                            <p className="text-sm text-gray-500">Game</p>
                            <p className="font-medium text-gray-800">
                              {game?.name}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">
                              Maximum-Player
                            </p>
                            <p className="font-medium text-gray-800">
                              {game?.maxPlayers}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">
                              Minimum-Player
                            </p>
                            <p className="font-medium text-gray-800">
                              {game?.minPlayers}
                            </p>
                          </div>
                        </div>
                        <div>
                          <div>
                            <p className="text-sm text-gray-500">
                              Slot-Duration
                            </p>
                            <p className="font-medium text-gray-800">
                              {game?.slotDuration}
                            </p>
                          </div>

                          <div>
                            <p className="text-sm text-gray-500">start-Time</p>
                            <p className="font-medium text-gray-800">
                              {game?.startTime}
                            </p>
                          </div>
                          <div>
                            <p className="text-sm text-gray-500">End-Time</p>
                            <p className="font-medium text-gray-800">
                              {game?.endTime}
                            </p>
                          </div>
                        </div>
                        <div>
                          <button
                            onClick={() => {
                              navigate(`/game/config/${game?.id}`);
                            }}
                            className="w-full  bg-black text-white font-medium py-2 px-3 m rounded "
                          >
                            Change-Configuration
                          </button>
                          <button
                            onClick={() => {
                              navigate(`/game/slot-monitor/${game?.id}`);
                            }}
                            className="w-full mt-4  bg-black text-white font-medium py-2 px-3 m rounded "
                          >
                            Monitor-slots
                          </button>
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

export default GameConfig;
