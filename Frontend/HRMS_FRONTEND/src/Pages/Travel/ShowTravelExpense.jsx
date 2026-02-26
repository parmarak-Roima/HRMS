import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import {
  fetchTravelExpense,
  submitExpenseById,
} from "../../Services/TravelExpenseService";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useForm } from "react-hook-form";
import { repondToExpense } from "../../Services/TravelExpenseService";
import { getTravelAssignmentId } from "../../Services/TravelAssignment";
import { Loader } from "../../components/ui/Loader";
import { handleGlobalError } from "../../Services/GlobalExceptionService";

function ShowTravelExpenses() {
  const [travelExpenseId, setTravelExpenseId] = useState(null);
  const { travelId, empId } = useParams();
  const [travelExpenses, setTravelExpenses] = useState([]);
  const [travelAssignmentId, setTravelAssignmentId] = useState();
  const [totalExpense, settotalExpense] = useState(null);
  const { authUser, setAuthUser } = useAuthUserContext();
  const [loading, setLoanding] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const getAllExpenses = async () => {
      setLoanding(true);
      try {
        const response = await getTravelAssignmentId(travelId, empId);
        setTravelAssignmentId(response.data);
        if (travelAssignmentId) {
          const response = await fetchTravelExpense(travelAssignmentId);
          let totalApprovedExpense = 0;
          response?.data?.map((travelExpense) => {
            if (travelExpense?.status == "APPROVED") {
              totalApprovedExpense += travelExpense.amount;
            }
          });
          settotalExpense(totalApprovedExpense);
          setTravelExpenses(response.data);
          setFilteredExpenses(response.data)
        }
      } catch (err) {
        setLoanding(false);
       handleGlobalError(err)
      } finally {
        setLoanding(false);
      }
    };
    getAllExpenses();
  }, [travelAssignmentId]);

  const [filters, setFilters] = useState({
    status: "",
    startDate: "",
    endDate: "",
  });

  const [filteredExpenses, setFilteredExpenses] = useState(null);
 
  useEffect(() => {
    const filtered = travelExpenses.filter((expense) => {
      if (filters.status && expense.status !== filters.status) {
        return false;
      }
      const expenseDate = new Date(expense.date);
      if (filters.startDate && expenseDate < new Date(filters.startDate)) {
        return false;
      }
      if (filters.endDate && expenseDate > new Date(filters.endDate)) {
        return false;
      }
      return true;
    });

    setFilteredExpenses(filtered);
  }, [travelExpenses, filters]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const resetFilters = () => {
    setFilters({
      status: "",
      startDate: "",
      endDate: "",
    });
  };

  const submitExpense = async (travelExpenseId) => {
    try {
      await submitExpenseById(travelExpenseId);
      toast.success("Submitted Successfully!!");
      const updatedResponse = await fetchTravelExpense(travelAssignmentId);
      setTravelExpenses(updatedResponse.data);
    } catch (err) {
      toast.error(err?.data?.message);
    }
  };

  const {
    register,
    handleSubmit,
    reset,
    formState: { isSubmitting, errors },
  } = useForm({
    defaultValues: {
      remarks: "",
    },
  });

  const approveExpense = async (travelExpenseId) => {
    try {
      const payload = {
        status: "APPROVED",
      };
      await repondToExpense(travelExpenseId, payload);
      const updatedResponse = await fetchTravelExpense(travelAssignmentId);
      setTravelExpenses(updatedResponse.data);
    } catch (err) {
      handleGlobalError(err)
    }
  };

  const rejectExpense = async (data) => {
    try {
      const payload = {
        remarks: data?.remarks,
        status: "REJECTED",
      };
      await repondToExpense(travelExpenseId, payload);
      setTravelExpenseId(null);
      const updatedResponse = await fetchTravelExpense(travelAssignmentId);
      setTravelExpenses(updatedResponse.data);
    } catch (err) {
      handleGlobalError(err)
    }
  };

  return (
    <>
      {loading ? (
        <Loader size={32} />
      ) : (
        <>
          {travelExpenseId ? (
            <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow mt-10">
              <h2 className="text-2xl font-bold mb-4">Reject expense</h2>
              <form
                onSubmit={handleSubmit(rejectExpense)}
                className="space-y-4 p-4 max-w-md border rounded"
              >
                <div>
                  <input
                    className="border rounded px-3 py-2 w-full"
                    type="text"
                    placeholder="add remarks...."
                    {...register("remarks", {
                      required: "Remarks is required",
                    })}
                  />
                  {errors.remarks && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.remarks.message}
                    </p>
                  )}
                </div>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-4 py-2 bg-black text-white rounded hover:bg-gray-700"
                >
                  {isSubmitting ? "Rejecting..." : "Reject"}
                </button>
              </form>
            </div>
          ) : (
            <div className="w-full bg-gray-100 p-6">
              <div className="max-w-4xl mx-auto space-y-6">
                <div className="bg-white rounded-2xl shadow p-6">
                  <div className="grid grid-cols-1">
                    <h2 className="text-2xl text-center font-semibold mb-4 flex justify-around">
                      <span>Travel Expenses</span>{" "}
                      <span className="font-normal">
                        Approved Amount {totalExpense}
                      </span>
                    </h2>
                    {/* filters */}
                     <div className="bg-gray-50 p-4 rounded-lg mb-6 border border-gray-200">
                        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                            <select
                                name="status"
                                value={filters.status}
                                onChange={handleFilterChange}
                                className="border rounded px-3 py-2 text-sm bg-white"
                            >
                                <option value="">All Statuses</option>
                                <option value="DRAFT">Draft</option>
                                <option value="SUBMITTED">Submitted</option>
                                <option value="APPROVED">Approved</option>
                                <option value="REJECTED">Rejected</option>
                            </select>
                            <input
                                type="date"
                                name="startDate"
                                value={filters.startDate}
                                onChange={handleFilterChange}
                                className="border rounded px-3 py-2 text-sm"
                                title="Start Date"
                            />
                            <input
                                type="date"
                                name="endDate"
                                value={filters.endDate}
                                onChange={handleFilterChange}
                                className="border rounded px-3 py-2 text-sm"
                                title="End Date"
                            />
                            <button 
                                onClick={resetFilters}
                                className="bg-gray-200 hover:bg-gray-300 text-gray-800 text-sm font-medium py-2 px-4 rounded transition"
                            >
                                Reset Filters
                            </button>
                        </div>
                    </div>
                    <div className="flex justify-center mb-3">
                      {authUser.role == "EMPLOYEE" && (
                        <button
                          onClick={() => {
                            navigate(
                              `/travel-expense/${travelAssignmentId}/Create`,
                            );
                          }}
                          className="w-25 bg-black text-white font-medium py-2  px-3 rounded-2xl "
                        >
                          upload Expense
                        </button>
                      )}
                    </div>
                  </div>
                  <div className="space-y-4">
                    {filteredExpenses.length === 0 ? (
                      <div className="text-center py-10 text-gray-500">
                         {travelExpenses.length === 0 
                            ? "No travel expenses yet!!!" 
                            : "No expenses match your filters."}
                      </div>
                    ) : (
                      filteredExpenses?.map((travelExpense) => (
                        <div
                          key={travelExpense.id}
                          className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                        >
                          <div className="flex-1">
                            <p className="text-sm text-gray-500">Type</p>
                            <p className="font-medium text-gray-800">
                              {travelExpense.expenseTypeName}
                            </p>
                            <p className="text-sm text-gray-500">
                              Employee-Name
                            </p>
                            <p className="font-medium text-gray-800">
                              {travelExpense.employeeName}
                            </p>
                            <p className="text-sm text-gray-500">Description</p>
                            <p className=" font-medium text-gray-800 text-justify">
                              {travelExpense.description}
                            </p>
                            <p className="text-sm text-gray-500">Amount</p>
                            <p className="font-medium text-gray-800">
                              {travelExpense.amount}
                            </p>
                            <p className="text-sm text-gray-500">Date</p>
                            <p className="font-medium text-gray-800">
                              {travelExpense.date}
                            </p>
                            <p className="text-sm text-gray-500">Status</p>
                            <p className="font-medium text-gray-800">
                              {travelExpense.status}
                            </p>
                            {travelExpense.status == "REJECTED" && (
                              <div>
                                <p className="text-sm text-gray-500">
                                  Remarks:
                                </p>
                                <p className="font-medium text-gray-800">
                                  {travelExpense.remarks}
                                </p>
                              </div>
                            )}
                          </div>
                          <div className="flex flex-col justify-center">
                            <a
                              href={travelExpense.proofUrl}
                              target="_blank"
                              className="px-6 py-1 bg-black text-white text-sm rounded hover:bg-gray-700"
                            >
                              View-Proof
                            </a>
                            {authUser?.role == "EMPLOYEE" &&
                              travelExpense.status == "DRAFT" && (
                                <button
                                  onClick={() =>
                                    submitExpense(travelExpense.id)
                                  }
                                  className="w-full bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                >
                                  Submit-Expense
                                </button>
                              )}
                            {authUser?.role == "HR" &&
                              travelExpense.status == "SUBMITTED" && (
                                <div>
                                  <button
                                    onClick={() => {
                                      approveExpense(travelExpense.id);
                                    }}
                                    className="w-full bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                  >
                                    Approve
                                  </button>
                                  <button
                                    onClick={() => {
                                      setTravelExpenseId(travelExpense.id);
                                    }}
                                    className="w-full bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                  >
                                    Reject
                                  </button>
                                </div>
                              )}
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </>
  );
}

export default ShowTravelExpenses;
