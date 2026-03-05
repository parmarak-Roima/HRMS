import React, { useEffect, useState } from "react";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useNavigate, useParams } from "react-router-dom";
import { createExpense, fetchAllExpenseType } from "../../Services/TravelExpenseService";
import { toast } from "react-toastify";
import { useForm } from "react-hook-form";
import { handleGlobalError } from "../../Services/GlobalExceptionService";

function CreateExpense() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const { travelAssignmentId } = useParams();
  const [expenseTypes, setExpenseTypes] = useState([]);
  const navigate = useNavigate()

  useEffect(() => {
    const getAllExpenseType = async () => {
      try {
        const response = await fetchAllExpenseType();
        console.log(response);
        setExpenseTypes(response.data);
      } catch (err) {
        handleGlobalError(err)
      }
    };
    getAllExpenseType();
  }, []);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
    reset,
  } = useForm();

  const expenseTypeId = watch("expenseTypeId");

  const onSubmit = async (data) => {
    try {
      const formData = new FormData();
      formData.append("travelAssignmentId", travelAssignmentId);
      formData.append("expenseTypeId", data.expenseTypeId);
      formData.append("amount", data.amount);
      formData.append("date", data.date);
      formData.append("description", data.description || "");
      formData.append("file", data.file[0]);
      await createExpense(formData);
      toast.success("Expense created successfully!");
      reset();
      navigate(`/travel`)
    } catch (error) {
      console.log(error);
      handleGlobalError(error);
    }
  };

  if (authUser.role == "HR") {
    return <p>You can not create expense !!</p>;
  }
  
  return (
    <>
      <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow">
        <h2 className="text-2xl font-bold mb-4 text-center">
          Create Travel expense
        </h2>
        <form
          onSubmit={handleSubmit(onSubmit)}
          className="space-y-4 p-4 max-w-md border rounded"
        >
          <div>
            <label className="block text-sm font-medium mb-1">
              Expense Type
            </label>
            <select
              className="border rounded px-3 py-2 w-full"
              {...register("expenseTypeId", {
                required: "Expense Type is required",
              })}
            >
              <option value="">Select Expense Type</option>
              {expenseTypes.map((type) => (
                <option key={type.id} value={type.id}>
                  {type.type}
                </option>
              ))}
            </select>
            {errors.expenseTypeId && <p>{errors.expenseTypeId.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Amount</label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="number"
              step="1"
              placeholder={ expenseTypeId && "Daily Limit for "+expenseTypes[expenseTypeId-2]?.type + " is "+ expenseTypes[expenseTypeId-2]?.dailyLimit}
              {...register("amount", {
                required: "Amount is required",
                min: {
                  value: 0.01,
                  message: "Amount must be greater than zero",
                },
              })}
            />
            {errors.amount && (
              <p className="text-red-500 text-sm mt-1">
                {errors.amount.message}
              </p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Date</label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="date"
              {...register("date", { required: "Date is required" })}
            />
            {errors.date && (
              <p className="text-red-500 text-sm mt-1">{errors.date.message}</p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">
              Description
            </label>
            <textarea
              className="border rounded px-3 py-2 w-full"
              {...register("description")}
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">File</label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="file"
              accept=".jpg,.png,.pdf"
              {...register("file", { required: "File is required" })}
            />
            <span className="font-extralight text-gray-400">*if more then one docuement then share in pdf form</span>
            {errors.file && (
              <p className="text-red-500 text-sm mt-1">{errors.file.message}</p>
            )}
          </div>
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 bg-black text-white rounded hover:bg-gray-700"
          >
            {isSubmitting ? "Submiting..." : "Submit"}
          </button>
        </form>
      </div>
    </>
  );
}

export default CreateExpense;
