import TravelDetails from "../../Componenets/Travel/TravelDetails";
import TravelAssignments from "../../Componenets/Travel/TravelAssignments";
import { useParams } from "react-router-dom";
import TravelDocHr from "./TravelDocHr";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";

function ShowTravelDetails() {
  
  const { travelId, empId } = useParams();
  const { authUser, setAuthUser } = useAuthUserContext();
  
  return (
    <>
      <TravelDetails travelId={travelId} />
      <TravelAssignments travelId={travelId}  />
      {authUser.role == "HR" && <TravelDocHr travelId={travelId} empId={empId} /> } 
    </>
  );
}
export default ShowTravelDetails;
