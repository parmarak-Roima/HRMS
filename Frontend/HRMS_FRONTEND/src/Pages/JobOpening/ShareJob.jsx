import React from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { shareJob } from "../../Services/jobService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { toast } from "react-toastify";

function ShareJob({jobId}) {
  // const { jobId } = useParams();
  const { authUser, setAuthUser } = useAuthUserContext();
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();

  const onSubmit = async (data) => {
    try {
      const payload = {
        ...data,
        sharedById: authUser.id,
        jobId: jobId,
      };
      const res = await shareJob(payload);
      toast.success("Shared successFully!!!");
    } catch (e) {
      handleGlobalError(e);
    }
  };
  return (
    <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow">
      <h2 className="text-2xl font-bold mb-4 text-center">
        Share A Job
      </h2>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="space-y-4 p-4 max-w-md border rounded"
      >
        <div>
          <label className="block text-sm font-medium mb-1">Email-Id</label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="text"
            placeholder="Email id of receipents...."
            {...register("recipientEmail", {
              required: "recipient's email is required",
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: "Please enter a valid email address",
              },
            })}
          />
          {errors.recipientEmail && (
            <p className="text-red-500 text-sm mt-1">
              {errors.recipientEmail.message}
            </p>
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
  );
}

export default ShareJob;
