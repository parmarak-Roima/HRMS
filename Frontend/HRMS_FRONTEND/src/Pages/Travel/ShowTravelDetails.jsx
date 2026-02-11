import TravelDetails from "../../Componenets/TravelDetails";
import TravelDocEmployee from "../../Componenets/TravelDocEmployee";
import TravelAssignments from "../../Componenets/TravelAssignments";
import { useParams } from "react-router-dom";
import TravelDocHr from "./TravelDocHr";

function ShowTravelDetails() {
  const { travelId, empId } = useParams();
  return (
    <>
      <TravelDetails travelId={travelId} />
       <TravelAssignments travelId={travelId}  /> 
      <TravelDocHr travelId={travelId} empId={empId} />
    </>
  );
}

export default ShowTravelDetails;
