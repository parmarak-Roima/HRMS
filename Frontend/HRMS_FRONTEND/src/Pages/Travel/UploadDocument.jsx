import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { uploadTravelDocument } from "../../Services/TravelDocService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
export default function UploadDocument() {
  const { ownerId, travelId } = useParams();
  const { authUser, setAuthUser } = useAuthUserContext();
  const [isAll, setIsAll] = useState(false);
  const navigate = useNavigate();
  const handleChange = (e) => {
    setIsAll(e.target.checked);
  };
  const {
    register,
    handleSubmit,
    reset,
    formState: { isSubmitting, errors },
  } = useForm({
    defaultValues: {
      docTypeStr: "",
      file: null,
    },
  });
  const docType = ["TICKET", "POLICY", "PASSPORT", "OTHER"];

   const queryClient = useQueryClient()

   const mutation = useMutation({
    mutationFn: uploadTravelDocument,
    onSuccess:async () => {
      await queryClient.invalidateQueries({ queryKey: ["travel-doc",travelId,authUser.id] });
      toast.success("Document uploaded successfully!");
      reset();
      navigate("/travel");
    },
    onError: (error) => {
      handleGlobalError(error);
    }
  });

  const onSubmit = async (data) => {
      const formData = new FormData();
      formData.append("docTypeStr", data.docTypeStr);
      formData.append("file", data.file[0]);
      if (authUser.role !== "HR" || !isAll ) {
        formData.append("ownerId", ownerId);
      }
      formData.append("travelId", travelId);
      mutation.mutate(formData);
  };

  return (
    <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow mt-10">
      <h2 className="text-2xl font-bold mb-4">Upload Docuements</h2>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="space-y-4 p-4 max-w-md border rounded"
      >
        <div>
          <label className="block text-sm font-medium mb-1">
            Document Type
          </label>
          <select
            {...register("docTypeStr", {
              required: "Document type is required",
            })}
            className="border rounded px-3 py-2 w-full"
          >
            <option value="">Document type</option>
            {docType?.map((doc) => {
              return <option value={doc}>{doc}</option>;
            })}
          </select>
          {errors.docTypeStr && (
            <p className="text-red-500 text-sm mt-1">
              {errors.docTypeStr.message}
            </p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">File</label>
          <input
            type="file"
            accept=".jpg,.png,.pdf"
            {...register("file", { required: "File is required" })}
            className="border rounded px-3 py-2 w-full"
          />
          {errors.file && (
            <p className="text-red-500 text-sm mt-1">{errors.file.message}</p>
          )}
        </div>
        {authUser.role == "HR" && authUser.id != ownerId && (
          <div>
            <label>
              <input type="checkbox" checked={isAll} onChange={handleChange} />
              want to upload for All?
            </label>
          </div>
        )}

        <button
          type="submit"
          disabled={mutation.isPending}
          className="px-4 py-2 bg-black text-white rounded hover:bg-gray-700"
        >
          {mutation.isPending ? "Uploading..." : "upload"}
        </button>
      </form>
    </div>
  );
}
