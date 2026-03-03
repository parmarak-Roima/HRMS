import React, { useEffect } from "react";
import { useLoaderData, useNavigate, useParams } from "react-router-dom";
import { Loader } from "../components/ui/Loader";
import { useState } from "react";
import { fetchAllEmployee, fetchBirthDayEmployee, fetchEmployeeById, fetchJoiningDayEmployee } from "../Services/authService";
import { toast } from "react-toastify";
import { useAuthUserContext } from "../Contexts/AuthUserContext";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { handleGlobalError } from "../Services/GlobalExceptionService";
// import { toast } from "react-toastify";
// import { fetchEmployeeById } from "../Services/authService";

// export const ProfileLoader = async ({ params }) => {
//   try {
//     const data = await fetchEmployeeById(params?.id);
//     console.log(data.data);
//     return data.data;
//   } catch (e) {
//     toast.error(e.data.message);
//   } finally {
//   }
// };
function Profile() {
  const { id } = useParams();
  const [loading, setLoading] = useState(true);
  const [Employee, setEmployee] = useState(useLoaderData());
  const { authUser, setAuthUser } = useAuthUserContext();
  const [birthdayEmployees, setBirthdayEmployees] = useState([]);
  const [joiningDayEmployee, setJoiningDayEmployee] = useState([])
  if (id != authUser.id) {
    return (
      <p className="text-center mt-4">
        You are not authorized to see this profile!!
      </p>
    );
  }
  useEffect(() => {
    const getAllEmployee = async () => {
      try {
        const response  = await fetchBirthDayEmployee();
        setBirthdayEmployees(response.data);
        const response2 = await fetchJoiningDayEmployee();
        setJoiningDayEmployee(response2.data)
        setLoading(false);
      } catch (err) {
        handleGlobalError(err);
      }
    };
    getAllEmployee();
  }, []);
  const navigate = useNavigate();
  return (
    <>
      <div className="flex flex-col items-center gap-4 p-6 pt-0">
        <div className=" w-full min-h-screen bg-gray-100 p-6">
          <div className="max-w-4xl mx-auto space-y-6">
            <Card className="  ">
              <CardHeader>
                <CardTitle className="m-auto text-3xl font-bold">
                  Profile
                </CardTitle>
                <CardDescription className="m-auto">
                  Details about employee
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <p className="text-gray-600">Full Name</p>
                    <p className="text-lg font-medium">{Employee?.name}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Designation:</p>
                    <p className="text-lg font-medium">
                      {Employee?.designation}
                    </p>
                  </div>
                  <div>
                    <p className="text-gray-600">Email:</p>
                    <p className="text-lg font-medium">{Employee?.email}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Role:</p>
                    <p className="text-lg font-medium">{Employee?.role}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Joining-Date:</p>
                    <p className="text-lg font-medium">
                      {Employee?.joiningDate}
                    </p>
                  </div>
                  <div>
                    <p className="text-gray-600">Birth-Date:</p>
                    <p className="text-lg font-medium">{Employee?.birthdate}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Manager-Name:</p>
                    <p className="text-lg font-medium">
                      {Employee?.mangerName == null
                        ? "NA"
                        : Employee.mangerName}
                    </p>
                  </div>
                </div>
              </CardContent>
              <CardFooter className="m-auto">
                <div>
                  {authUser.role == "HR" && (
                    <div>
                      <button
                        onClick={() => navigate("/config")}
                        className="px-6 py-2 bg-black text-white border rounded-2xl"
                      >
                        Mail-config
                      </button>
                      <button
                        onClick={() => navigate("/game/config")}
                        className="px-6 py-2 bg-black text-white border rounded-2xl"
                      >
                        Game-Config
                      </button>
                    </div>
                  )}
                </div>
              </CardFooter>
            </Card>
          </div>
          <div className="grid md:grid-cols-2 gap-2 mt-3">
            <div className="space-y-6">
              <Card className="  ">
                <CardHeader>
                  <CardTitle className="m-auto text-3xl font-bold">
                    Today's birthday
                  </CardTitle>
                  <CardDescription className="m-auto">
                    Wishing you all very happy birthday...
                  </CardDescription>
                </CardHeader>
                <CardContent className="overflow-y-scroll max-h-50">
                  { birthdayEmployees.length == 0 && <p className="m-auto text-center font-extralight" >No party today...</p> }
                  {birthdayEmployees?.map((emp) => (
                    <div className="w-full mt-2 bg-gray-100 p-6 rounded-2xl">
                      {emp.email}
                    </div>
                  ))}
                </CardContent>
               
              </Card>
            </div>
             <div className="space-y-6">
              <Card className="  ">
                <CardHeader>
                  <CardTitle className="m-auto text-3xl font-bold">
                    Today's Joining Aniversary
                  </CardTitle>
                  <CardDescription className="m-auto">
                    Wishing you all happy joining aniversary...
                  </CardDescription>
                </CardHeader>
                <CardContent className="overflow-y-scroll max-h-50">
                  { joiningDayEmployee.length == 0 && <p className="m-auto text-center font-extralight" >No party today...</p> }
                  {joiningDayEmployee?.map((emp) => (
                    <div className="w-full mt-2 bg-gray-100 p-6 rounded-2xl">
                      {emp.email}
                    </div>
                  ))}
                </CardContent>
                
              </Card>
            </div>
            
          </div>
        </div>
      </div>
    </>
  );
}

export default Profile;
