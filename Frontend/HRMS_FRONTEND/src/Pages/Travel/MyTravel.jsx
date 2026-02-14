import React, { useState, useEffect } from "react";
import TravelCard from "../../Componenets/Travel/TravelCard";
import { Loader } from "../../components/ui/Loader";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { fetchEmployeeTravel } from "../../Services/TravelAssignment";
import { fetchAllTravel } from "../../Services/TravelService";
import { useNavigate } from "react-router-dom";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
const MyTravels = () => {
  const [travels, setTravels] = useState([]);
  const [loading, setLoading] = useState(true);
  const { authUser, setAuthUser } = useAuthUserContext();
  const  navigate = useNavigate();
  
  useEffect(() => {
    const setEmployeeTravel = async () => {
      try {
        if (authUser.role == "EMPLOYEE" || authUser.role == "MANAGER") {
          const response = await fetchEmployeeTravel(authUser.id);
          setTravels(response.data);
        } else if (authUser.role == "HR") {
          const response = await fetchAllTravel(authUser.id);
          setTravels(response.data);
        }
      } catch (e) {
        setLoading(false);
        handleGlobalError(e)
      } finally {
        setLoading(false);
      }
    };
    setEmployeeTravel();
  }, []);

  return (
    <div>
      {loading ? (
        <Loader size={32} />
      ) : (
        <div className="min-h-screen bg-gray-50 p-8 font-sans">
          <div className="max-w-6xl mx-auto mb-8">
            <div className="grid grid-cols-1">
                 <div className="flex justify-start gap-10">
                    <h1 className="text-3xl font-bold text-gray-900">
                      
                      My Travels</h1>
                <div className="flex justify-end mb-3">
                {authUser.role == "MANAGER" &&
                  <button
                    onClick={() => {navigate(`/travel/manager/${authUser.id}`)}}
                    className="w-25 bg-black text-white font-medium py-2  px-3 rounded-2xl "
                  >
                    Team Travel
                  </button>
}
                </div>
            </div>
              
              <div className="flex justify-start mt-4 ">
                {authUser.role == "HR" &&
                <button
                  onClick={() => {
                    navigate("/travel/create")
                  }}
                  className="w-50 bg-black text-white font-medium py-2  px-3 rounded-2xl "
                >
                  Create travel
                </button>
                }
              </div>
            </div>
          </div>
          <p className="text-gray-700 mb-1 text-2xl font-bold">
            {travels.length == 0 && "No travels found for you!!!"}
          </p>
          <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {travels?.map((assignment) => (
              <TravelCard key={assignment.id} assignment={assignment} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default MyTravels;
