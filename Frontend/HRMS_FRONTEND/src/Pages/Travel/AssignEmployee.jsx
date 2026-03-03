import React, { useEffect, useState } from "react";

import Select from "react-select";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { fetchAllEmployee } from "../../Services/authService";
import { createTravelAssignment } from "../../Services/TravelAssignment";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";

function AssignEmployee() {
  const { travelId } = useParams();
  const [employees, setEmployees] = useState([]);
  const [selectedEmployees, setSelectedEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  useEffect(() => {
    const getAllEmployee = async () => {
      try {
        const response = await fetchAllEmployee();
        setEmployees(response.data);
        setLoading(false);
      } catch (err) {
        handleGlobalError(err);
      }
    };
    getAllEmployee();
  }, []);

  const options = employees.map((emp) => ({
    value: emp.id,
    label: emp.email,
  }));
  const handleChange = (selected) => {
    setSelectedEmployees(selected);
  };
  const assignEmployee = async () => {
    try {
      setLoading(true);
      selectedEmployees.forEach(async (employee) => {
        try {
          await createTravelAssignment(travelId, employee.value);
          toast.success(employee.label + " assigned successfully!!");
        } catch (e) {
          handleGlobalError(e);
        }
      });
      setLoading(false);
      navigate("/travel");
    } catch (e) {
      setLoading(false);
      console.log(e);
      handleGlobalError(e);
    }
  };
  return (
    <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow mt-10">
      <h2 className="text-2xl font-bold mb-4">Assign-Employee</h2>

      <div>
        <Select
          isMulti
          options={options}
          onChange={handleChange}
          value={selectedEmployees}
        />
        <button
          disabled={loading}
          onClick={() => assignEmployee()}
          className=" w-full px-4 py-1.5 mt-4 bg-black text-white rounded hover:bg-gray-700"
          type="submit"
        >
          submit
        </button>
      </div>
    </div>
  );
}

export default AssignEmployee;
