import React, { useEffect } from "react";
import { useLoaderData, useNavigate, useParams } from "react-router-dom";
import { Loader } from "../components/ui/Loader";
import { useState } from "react";
import { fetchEmployeeById } from "../Services/authService";
import { toast } from "react-toastify";
import { useAuthUserContext } from "../Contexts/AuthUserContext";
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
  const {id } = useParams()
  const [loading, setLoading] = useState(false);
  const [Employee, setEmployee] = useState(useLoaderData());
  const {authUser , setAuthUser} = useAuthUserContext();

  if( id != authUser.id){
    return <p className="text-center mt-4">
      You are not authorized to see this profile!!
    </p>
  }
  const navigate = useNavigate()
  return (
    <>
      <div className="flex flex-col items-center gap-4 p-6">
         
          <div className="overflow-x-scroll w-full min-h-screen bg-gray-100 p-6">
            <div className="max-w-4xl mx-auto space-y-6">
              <div className="bg-white rounded-2xl shadow p-6">
                <div className="flex justify-around items-center">
                  <h2 className="text-2xl text-center font-semibold mb-4">
                    Profile
                  </h2>
                  {authUser.role == 'HR' && 
                  <div>
                  <button 
                    onClick={() => navigate("/config")}
                    className="px-6 py-2 bg-black text-white border rounded-2xl">
                    Mail-config
                  </button>
                  <button
                     onClick={() => navigate("/game/config")}
                    className="px-6 py-2 bg-black text-white border rounded-2xl">
                    Game-Config
                  </button>
                  </div>
                  }
                </div>
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
              </div>
            </div>
          </div>
      </div>
    </>
  );
}

export default Profile;
