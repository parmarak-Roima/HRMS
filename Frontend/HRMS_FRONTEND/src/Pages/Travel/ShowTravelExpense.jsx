import React, { useEffect, useState } from "react";
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
function ShowTravelExpenses() {
  const [travelExpenseId, setTravelExpenseId] = useState(null);
  const { travelId, empId } = useParams();
  const [travelExpenses, setTravelExpenses] = useState([]);
  const [travelAssignmentId, setTravelAssignmentId] = useState();
  const [totalExpense, settotalExpense] = useState(null);
  const { authUser, setAuthUser } = useAuthUserContext();
  const [loading,setLoanding] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const getAllExpenses = async () => {
        setLoanding(true);
      try {
        const response= await getTravelAssignmentId(travelId, empId);
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
        }
      } catch (err) {
        setLoanding(false);
        toast.error(err?.data?.message);
      }finally{
        setLoanding(false);
      }
    };
    getAllExpenses();
  }, [travelAssignmentId]);

  const submitExpense = async (travelExpenseId) => {
    try {
      await submitExpenseById(travelExpenseId);
      toast.success("Submitted Successfully!!");
      setTravelExpenses(response.data);
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
      toast.error(err?.data?.message);
    }
  };
  const rejectExpense = async (data) => {
    try {
      const payload = {
        remarks: data?.remarks,
        status: "REJECTED",
      };
      await repondToExpense(travelExpenseId, payload);
    } catch (err) {
      toast.error(err?.data?.message);
    }
  };
  if(travelExpenses.length == 0 ){
    return <p>No travel expenses yet!!!</p>
  }
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
                <div className="flex justify-center mb-3">
                  {authUser.role == "EMPLOYEE" && (
                    <button
                      onClick={() => {
                        navigate(
                          `/travel-expense/${travelExpenses[0]?.travelAssignmentId}/Create`,
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
                { travelExpenses.length == 0 ?
                    (<p>No travel expenses yet!!!</p>):(
                travelExpenses?.map((travelExpense) => (
                  <div
                    key={travelExpense.id}
                    className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                  >
                    <div className="flex-1">
                      <p className="text-sm text-gray-500">Type</p>
                      <p className="font-medium text-gray-800">
                        {travelExpense.expenseTypeName}
                      </p>
                      <p className="text-sm text-gray-500">Employee-Name</p>
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
                          <p className="text-sm text-gray-500">Remarks:</p>
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
                            onClick={() => submitExpense(travelExpense.id)}
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
                )))}
              </div>
            </div>
          </div>
        </div>
      )}
    </>)}
    </>
  );
}

export default ShowTravelExpenses;
