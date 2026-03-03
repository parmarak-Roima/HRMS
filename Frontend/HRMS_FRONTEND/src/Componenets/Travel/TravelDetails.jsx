import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { fetchTravelById } from "../../Services/TravelService";
import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
function TravelDetails({ travell }) {
  const [travel, setTravel] = useState(travell);
  const navigate = useNavigate();
  const {authUser, setAuthUser} = useAuthUserContext()
  useEffect(() => {
    setTravel(travell);
  }, [travell]);
  return (
    <>
      <div className="w-full bg-gray-100 p-6">
        <div className="max-w-4xl mx-auto space-y-6">
          <div className="bg-white rounded-2xl shadow p-6">
            <div className="flex justify-between">
              <h2 className="text-2xl text-center font-semibold mb-4">
                Travel Details{" "}
              </h2>
              { authUser.role == "HR" && travel?.status == "SCHEDULED" && (
                <div className="">
                <button
                  className="p-2 m-2 bg-black text-white font-medium py-0.5 rounded "
                  onClick={() => navigate(`/travel/update/${travel?.id}`)}
                >
                  Update travel
                </button>
                 <button
                className="p-2 bg-black text-white font-medium py-0.5 rounded "
                onClick={() => navigate(`/travel/assign/${travel?.id}`)}
              >
                Assign-Employee
              </button>
              </div>
              )}
             
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-gray-600">Destination</p>
                <p className="text-lg font-medium">{travel?.destination}</p>
              </div>
              <div>
                <p className="text-gray-600">Start-Date:</p>
                <p className="text-lg font-medium">{travel?.startDate}</p>
              </div>
              <div>
                <p className="text-gray-600">End-Date:</p>
                <p className="text-lg font-medium">{travel?.endDate}</p>
              </div>
              <div>
                <p className="text-gray-600">Status:</p>
                <p className="text-lg font-medium">{travel?.status}</p>
              </div>
              <div>
                <p className="text-gray-600">Required-Docs:</p>
                <p className="text-lg font-medium">{travel?.requiredDocs}</p>
              </div>
              <div>
                <p className="text-gray-600">Description</p>
                <p className="text-lg font-medium">{travel?.description}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default TravelDetails;
