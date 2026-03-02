import TravelDetails from "../../Componenets/Travel/TravelDetails";
import TravelAssignments from "../../Componenets/Travel/TravelAssignments";
import { useParams } from "react-router-dom";
import TravelDocHr from "./TravelDocHr";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useQuery } from "@tanstack/react-query";
import { fetchTravelById } from "../../Services/TravelService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { Loader } from "../../components/ui/Loader";
import { useEffect, useState } from "react";

function ShowTravelDetails() {
  const { travelId, empId } = useParams();
  const { authUser, setAuthUser } = useAuthUserContext();
  const [travel, settravel] = useState({});
  const { data, error, isPending, isError, isSuccess } = useQuery({
    queryKey: ["travel", parseInt(travelId)],
    queryFn: () => {
      return fetchTravelById(travelId);
    },
    staleTime: 5 * 60 * 200,
  });

  useEffect(() => {
    settravel(data?.data);
  }, [data]);

  if (isError) {
    handleGlobalError(error);
  }

  return (
    <>
      {isPending ? (
        <Loader />
      ) : (
        <>
          {" "}
          <TravelDetails travell={travel} />
          <TravelAssignments travell={travel} />
          {authUser.role == "HR" && (
            <TravelDocHr travelId={travelId} empId={empId} />
          )}
        </>
      )}
    </>
  );
}
export default ShowTravelDetails;
