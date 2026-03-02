import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { fetchAllEmployee, fetchAllHrs } from "../../Services/authService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { createJobOpening } from "../../Services/jobService";
import { toast } from "react-toastify";
import Select from "react-select";
import { useMutation, useQueryClient } from "@tanstack/react-query";

function CreateJob() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [hrs, setHrs] = useState([]);
  const [cvReviewers,setCvReviewers] = useState([]);
  const queryClient = useQueryClient();
  useEffect(() => {
    getAllEmployees();
    getAllHrs();
  }, []);

 const options = employees.map((emp) => ({
    value: emp.id,
    label: emp.email,
  }));

  const handleChange = (selected) => {
    setCvReviewers(selected);
  };

  const getAllEmployees = async () => {
    try {
      const res = await fetchAllEmployee();
      setEmployees(res.data);
    } catch (e) {
      handleGlobalError(e);
    }
  };

  const getAllHrs = async () => {
    try {
      const res = await fetchAllHrs();
      console.log(res);
      setHrs(res.data);
    } catch (e) {
      handleGlobalError(e);
    }
  };

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();

 const mutation = useMutation({
    mutationFn: createJobOpening,
    onSuccess:async () => {
      await queryClient.invalidateQueries({ queryKey: ['job-Openings'] });
       toast.success("Job Created SuccessFully!!!");
      navigate("/jobOpening");
    },
    onError: (error) => {
      handleGlobalError(error);
    }
  });

  const onSubmit = async (data) => {
      const formData = new FormData();
      formData.append("title", data.title);
      formData.append("summary", data.summary);
      formData.append("description", data.description);
      formData.append("hrOwnerId", parseInt(data.hrOwnerId));
      formData.append("jdFile", data.jdFile[0]);
      cvReviewers.map((option) => option.value).forEach(
        cvReviewerId => {
          formData.append("cvReviewerIds",cvReviewerId)
        }
      )
      mutation.mutate(formData);
  };

  const toggleEmployee = (id) => {
    setCvReviewerIds((prev) =>
      prev.includes(id) ? prev.filter((empId) => empId !== id) : [...prev, id],
    );
  };

  if (authUser.role != "HR") {
    return <p>Only hr can access this page !!</p>;
  }
  
  return (
    <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow">
      <h2 className="text-2xl font-bold mb-4 text-center">
        Create Job Opening
      </h2>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="space-y-4 p-4 max-w-md border rounded"
      >
        <div>
          <label className="block text-sm font-medium mb-1">Title(Role)</label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="text"
            placeholder="Title...."
            {...register("title", {
              required: "title is required",
            })}
          />
          {errors.title && (
            <p className="text-red-500 text-sm mt-1">{errors.title.message}</p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">summary</label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="text"
            placeholder="Summry of job Opening...."
            {...register("summary", {
              required: "Summry is required",
            })}
          />
          {errors.summary && (
            <p className="text-red-500 text-sm mt-1">
              {errors.summary.message}
            </p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Description</label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="text"
            placeholder="description of the job opening...."
            {...register("description", {
              required: "Description is required",
            })}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Jd-File</label>
          <input
            className="border rounded px-3 py-2 w-full"
            type="file"
            accept=".jpg,.png,.pdf"
            {...register("jdFile", { required: "File is required" })}
          />
          {errors.jdFile && (
            <p className="text-red-500 text-sm mt-1">{errors.jdFile.message}</p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium mb-2">Set hr-owner</label>
          <select
            className="p-3 px-5 border-2 rounded-full"
            {...register("hrOwnerId")}
          >
            <option>Choose</option>
            {hrs.map((hr) => (
              <option value={hr.id}>{hr.email}</option>
            ))}
          </select>
        </div>
        <div>
            <label className="block text-sm font-medium mb-2">
              Assign cv reviewers
            </label>
            <Select
            isMulti
            options={options}
            onChange={handleChange}
            value={cvReviewers}
          />
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

export default CreateJob;
