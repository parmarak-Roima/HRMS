import React, { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Loader } from "../components/ui/Loader";
import { useState } from "react";
import { fetchEmployeeById } from "../Services/authService";
import { toast } from "react-toastify";
import { useAuthUserContext } from "../Contexts/AuthUserContext";
function Profile() {
  let { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [Employee, setEmployee] = useState(null);
  const {authUser , setAuthUser} = useAuthUserContext();
  const navigate = useNavigate()
  useEffect(() => {
    const fetchEmployee = async () => {
      try {
        setLoading(true);
        const data = await fetchEmployeeById(id);
        console.log(data.data);
        setEmployee(data.data);
      } catch (e) {
        toast.error(e.data.message);
        setLoading(false);
      } finally {
        setLoading(false);
      }
    };

    fetchEmployee();
  }, []);
  return (
    <>
      <div className="flex flex-col items-center gap-4 p-6">
        {loading ? (
          <Loader size={32} />
        ) : (
          <div className="w-full min-h-screen bg-gray-100 p-6">
            <div className="max-w-4xl mx-auto space-y-6">
              <div className="bg-white rounded-2xl shadow p-6">
                <div className="flex justify-around items-center">
                  <h2 className="text-2xl text-center font-semibold mb-4">
                    Profile
                  </h2>
                  {authUser.role == 'HR' && 
                  <button 
                    onClick={() => navigate("/config")}
                    className="px-6 py-2 bg-black text-white border rounded-2xl">
                    Config
                  </button>
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
        )}
      </div>
    </>
  );
}

export default Profile;
