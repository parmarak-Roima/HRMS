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
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
function ShowTravelExpenses() {
  const [travelExpenseId, setTravelExpenseId] = useState(null);
  const { travelId, empId } = useParams();
  const [travelExpenses, setTravelExpenses] = useState([]);
  const [travelAssignmentId, setTravelAssignmentId] = useState();
  const [totalExpense, settotalExpense] = useState(null);
  const { authUser, setAuthUser } = useAuthUserContext();
  const [loading, setLoanding] = useState(true);
  const navigate = useNavigate();

  const { data, error, isPending, isError, isSuccess } = useQuery({
    queryKey: ["travel-expenses", parseInt(travelAssignmentId)],
    queryFn: () => {
      return fetchTravelExpense(travelAssignmentId);
    },
    staleTime: 5 * 60 * 200,
  });
  if (isError) {
    if( travelAssignmentId ){
    handleGlobalError(error);
    }
  }
  useEffect(() => {
    getAllExpenses();
    if (travelAssignmentId) {
      let totalApprovedExpense = 0;
      data?.data.map((travelExpense) => {
        if (travelExpense?.status == "APPROVED") {
          totalApprovedExpense += travelExpense.amount;
        }
      });
      settotalExpense(totalApprovedExpense);
    }
    setTravelExpenses(data?.data);
    setFilteredExpenses(data?.data);
  }, [data, travelAssignmentId]);

  const getAllExpenses = async () => {
    setLoanding(true);
    try {
      const response = await getTravelAssignmentId(travelId, empId);
      setTravelAssignmentId(response.data);
    } catch (err) {
      setLoanding(false);
      handleGlobalError(err);
    } finally {
      setLoanding(false);
    }
  };
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState({
    status: "",
    startDate: "",
    endDate: "",
  });

  const [filteredExpenses, setFilteredExpenses] = useState(null);

  useEffect(() => {
    const filtered = travelExpenses?.filter((expense) => {
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
      queryClient.invalidateQueries({
        queryKey: ["travel-expenses", travelAssignmentId],
      });
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
      queryClient.invalidateQueries({
        queryKey: ["travel-expenses", travelAssignmentId],
      });
      toast.success("approved successFully!!");
    } catch (err) {
      handleGlobalError(err);
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
      toast.success("rejected successFully!!");
      queryClient.invalidateQueries({
        queryKey: ["travel-expenses", travelAssignmentId],
      });
      reset();
    } catch (err) {
      handleGlobalError(err);
    }
  };

  return (
    <>
      {loading || isPending ? (
        <Loader size={32} />
      ) : (
        <>
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
                    {authUser.role == "EMPLOYEE" ||
                      (authUser.role == "MANAGER" && (
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
                      ))}
                  </div>
                </div>
                <div className="space-y-4">
                  {filteredExpenses?.length === 0 ? (
                    <div className="text-center py-10 text-gray-500">
                      {travelExpenses?.length === 0
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
                              <div className="flex flex-col gap-2">
                                <Dialog>
                                  <DialogTrigger className="w-full bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 ">
                                    Approve
                                  </DialogTrigger>
                                  <DialogContent>
                                    <DialogHeader>
                                      <DialogTitle>
                                        <h2 className="text-2xl font-bold mb-4">
                                          Approve expense
                                        </h2>
                                      </DialogTitle>
                                      <DialogDescription>
                                        Are you sure, you want to approve
                                        expense ?
                                      </DialogDescription>
                                      <button
                                        onClick={async () => {
                                          await approveExpense(
                                            travelExpense.id,
                                          );
                                          queryClient.invalidateQueries({
                                            queryKey: [
                                              "travel-expenses",
                                              parseInt(travelAssignmentId),
                                            ],
                                          });
                                        }}
                                        className="bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                      >
                                        Approve
                                      </button>
                                    </DialogHeader>
                                    <DialogFooter className="sm:justify-start">
                                      <DialogClose asChild>
                                        <Button
                                          type="button"
                                          className="w-full"
                                        >
                                          Close
                                        </Button>
                                      </DialogClose>
                                    </DialogFooter>
                                  </DialogContent>
                                </Dialog>

                                <Dialog>
                                  <DialogTrigger
                                    className="w-full bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                                    onClick={() => {
                                      setTravelExpenseId(travelExpense.id);
                                    }}
                                  >
                                    Reject
                                  </DialogTrigger>
                                  <DialogContent>
                                    <DialogHeader>
                                      <DialogTitle>
                                        <h2 className="text-2xl font-bold mb-4">
                                          Reject expense
                                        </h2>
                                      </DialogTitle>
                                      <DialogDescription>
                                        After Rejecting they need to re-upload ,
                                        Are you sure ?
                                      </DialogDescription>
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
                                          {isSubmitting
                                            ? "Rejecting..."
                                            : "Reject"}
                                        </button>
                                      </form>
                                    </DialogHeader>
                                    <DialogFooter className="sm:justify-start">
                                      <DialogClose asChild>
                                        <Button type="button">Close</Button>
                                      </DialogClose>
                                    </DialogFooter>
                                  </DialogContent>
                                </Dialog>
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
        </>
      )}
    </>
  );
}

export default ShowTravelExpenses;
