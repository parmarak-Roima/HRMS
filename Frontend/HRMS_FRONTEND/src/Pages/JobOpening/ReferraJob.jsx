import React from "react";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useNavigate, useParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import { createReferral } from "../../Services/JobReferralService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { toast } from "react-toastify";
import { useMutation, useQueryClient } from "@tanstack/react-query";

function ReferrJob() {
  const { jobId } = useParams();
  const { authUser, setAuthUser } = useAuthUserContext();
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();
  const queryClient = useQueryClient()

 const mutation = useMutation({
    mutationFn: createReferral,
    onSuccess: async () => {
       await queryClient.invalidateQueries({ queryKey: ['job-referrals',jobId] });
      toast.success("Referred SuccessFully!!!");
      navigate("/jobOpening");
    },
    onError: (error) => {
      handleGlobalError(error);
    }
  });

  const onSubmit = async (data) => {
      const formData = new FormData();
      formData.append("jobId", jobId); // Long
      formData.append("referrerId", authUser.id); // Long
      formData.append("candidateName", data.candidateName); // String
      formData.append("candidateEmail", data.candidateEmail); // String
      formData.append("note", data.note || "");
      formData.append("cvFile", data.cvFile[0]);
      mutation.mutate(formData);
  };

  return (
    <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow">
      <h2 className="text-2xl font-bold mb-4 text-center">Referr A Friend</h2>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="space-y-4 p-4 max-w-md border rounded"
      >
        <div>
          <label className="block text-sm font-medium mb-1">
            Candidate Email-id
          </label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="text"
            placeholder="Email id of Candidate...."
            {...register("candidateEmail", {
              required: "Candidate's email is required",
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: "Please enter a valid email address",
              },
            })}
          />
          {errors.candidateEmail && (
            <p className="text-red-500 text-sm mt-1">
              {errors.candidateEmail.message}
            </p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">
            Candidate-Name
          </label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="text"
            placeholder="Name of Candidate...."
            {...register("candidateName", {
              required: "Candidate's Name is required",
            })}
          />
          {errors.candidateName && (
            <p className="text-red-500 text-sm mt-1">
              {errors.candidateName.message}
            </p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Short-Note</label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="text"
            placeholder="Short Note About Candidates...."
            {...register("note")}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Cv-file</label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="file"
            accept=".jpg,.png,.pdf"
            {...register("cvFile", { required: "File is required" })}
          />
          {errors.cvFile && (
            <p className="text-red-500 text-sm mt-1">{errors.cvFile.message}</p>
          )}
        </div>
        <button
          type="submit"
          disabled={mutation.isPending}
          className="px-4 py-2 bg-black text-white rounded hover:bg-gray-700"
        >
          {mutation.isPending ? "Submiting..." : "Submit"}
        </button>
      </form>
    </div>
  );
}

export default ReferrJob;
