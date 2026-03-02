import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { fetchTravelById } from "../../Services/TravelService";
import { useState, useEffect } from "react";
import { toast } from "react-toastify/unstyled";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { handleGlobalError } from "../../Services/GlobalExceptionService";

function TravelAssignments({ travell }) {
  const { authUser, setAuthUser } = useAuthUserContext();

  const [travel, setTravel] = useState({});

  const navigate = useNavigate();
  
  useEffect(() => {
    setTravel(travell);
  }, [travell]);

  return (
    <div className="w-full bg-gray-100 p-6">
      <div className="max-w-4xl mx-auto space-y-6">
        <div className="bg-white rounded-2xl shadow p-6">
          <h2 className="text-2xl text-center font-semibold mb-4">
            Travel Assignments
          </h2>
          <div className="space-y-4">
            {travel?.employeeIdsToAssign?.map((employee) => (
              <div
                key={employee.id}
                className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
              >
                <div className="flex-1">
                  <p className="text-sm text-gray-500">email</p>
                  <p className="font-medium text-gray-800">{employee?.email}</p>
                </div>
                <div className="flex flex-col justify-center">
                  {(authUser.role == "HR" || authUser.id == employee?.id) && (
                    <div>
                      <button
                        onClick={() => {
                          navigate(`/travelDoc/${travel?.id}/${employee.id}`);
                        }}
                        className="w-full bg-black text-white font-medium py-2 mt-4 rounded "
                      >
                        View-Documents
                      </button>
                      <button
                        onClick={() => {
                          navigate(
                            `/travel-expense/${travel?.id}/${employee.id}`,
                          );
                        }}
                        className="w-full bg-black text-white font-medium py-2  mt-4 rounded "
                      >
                        View-Expense
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default TravelAssignments;
