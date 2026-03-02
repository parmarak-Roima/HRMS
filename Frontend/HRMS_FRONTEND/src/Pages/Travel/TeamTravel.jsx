import React from "react";
import { getTeamTravel } from "../../Services/TravelAssignment";
import { useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import TravelDocEmployee from "./TravelDocEmployee";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useQuery } from "@tanstack/react-query";
import { Loader } from "../../components/ui/Loader";
function TeamTravel() {
  const { managerId } = useParams();
  const [empId, setEmpId] = useState(0);
  const [travelId, setTravelId] = useState(0);
  const [travels, setTravels] = useState([]);
  const { authUser, setAuthUser } = useAuthUserContext();
  const navigate = useNavigate();

  const { data, error, isPending, isError, isSuccess } = useQuery({
    queryKey: ["team-travel", managerId],
    queryFn: () => {
      return getTeamTravel(managerId);
    },
    staleTime: 5 * 60 * 200,
  });

  useEffect(() => {
    setTravels(data?.data);
  }, [data]);

  if( isError ){
    handleGlobalError(error)
  }
  if( isPending ){
    return <Loader />
  }
  if(authUser.role !== "MANAGER") return <p>you cannot access this page</p>

  if (isSuccess && !travels || travels.length === 0) {
    return <p className="text-gray-500 text-center mt-40">No travel records found for you.</p>;
  }
  return (
    <>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {travels.map((travel) => (
            <div
              key={travel.id}
              className="bg-white shadow-md rounded-lg p-4 border border-gray-200"
            >
              <h2 className="text-lg font-semibold text-gray-800 mb-2">
                Destination: <span>{travel.destination}</span>
              </h2>
              <div className="grid grid-cols-1 gap-2.5">
                <p className="text-sm text-gray-600">
                  <span className="font-medium">Employee:</span>{" "}
                  {travel.employeeName}
                </p>

                <p className="text-sm text-gray-600">
                  <span className="font-medium">Travel ID:</span>{" "}
                  {travel.travelId}
                </p>

                <p className="text-sm text-gray-600">
                  <span className="font-medium">Status:</span>{" "}
                  <span
                    className={`px-2 py-1 rounded text-white text-xs bg-black`}
                  >
                    {travel.status}
                  </span>
                </p>

                <p className="text-sm text-gray-600">
                  <span className="font-medium">Start Date:</span>{" "}
                  {travel.startDate}
                </p>

                <p className="text-sm text-gray-600">
                  <span className="font-medium">End Date:</span>{" "}
                  {travel.endDate}
                </p>
                <div className="flex flex-col justify-center">
                  <button
                    onClick={() => {
                        navigate(`/travelDoc/${travel.travelId}/${travel.employeeId}`)
                    }}
                    className="w-full bg-black text-white font-medium py-2 px-3 mt-4 rounded "
                  >
                    View-Documents
                  </button>
                  <button
                    onClick={() => {
                        navigate(`/travel-expense/${travel.travelId}/${travel.employeeId}`)
                    }}
                    className="w-full bg-black text-white font-medium py-2 px-3 mt-4 rounded "
                  >
                    View-Expenses
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      
    </>
  );
}

export default TeamTravel;
