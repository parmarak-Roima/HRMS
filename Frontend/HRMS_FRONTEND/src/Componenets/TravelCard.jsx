import { useNavigate } from "react-router-dom";
import { useAuthUserContext } from "../Contexts/AuthUserContext";

const TravelCard = ({ assignment }) => {
    const navigate =  useNavigate();
      const {authUser,setAuthUser} = useAuthUserContext();
    
  return (
    <div className="bg-white border border-gray-200 rounded-lg p-6  flex flex-col justify-between h-full">
      <div className="flex justify-between items-start mb-4">
        <div>
          <div className="flex items-center text-gray-500 mb-1 text-xs font-semibold uppercase">
            Destination
          </div>
          <h3 className="text-xl font-bold text-gray-900">
            {assignment.destination}
          </h3>
        </div>
        <div>
          {assignment.status}
        </div>
      </div>
      <div className="flex justify-between items-start mb-4">
        <div>
          <div className="flex items-center text-gray-500 mb-1 text-xs font-semibold uppercase">
            Start Date
          </div>
          <h3 className="text-xl font-bold text-gray-900">
            {assignment.startDate}
          </h3>
        </div>
         <div>
          <div className="flex items-center text-gray-500 mb-1 text-xs font-semibold uppercase">
            end Date
          </div>
          <h3 className="text-xl font-bold text-gray-900">
            {assignment.endDate}
          </h3>
        </div>
      </div>
      <button 
        
        onClick={()=> 
        {
          if(assignment.travelId){
          navigate(`/travel/${assignment.travelId}/${authUser.id}`)
          }else{
            navigate(`/travel/${assignment.id}/${authUser.id}`)
          }
         } }
      className="w-full bg-black text-white font-medium py-3 mt-4 rounded ">
        View Full Details
      </button>
    </div>
  );
};
export default TravelCard;