import React from "react";
import { useForm } from "react-hook-form";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { uploadTravelDocument } from "../../Services/TravelDocService";
export default function UploadDocument() {
  const { ownerId, travelId } = useParams();
  const { authUser, setAuthUser } = useAuthUserContext();
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
  const onSubmit = async (data) => {
    try {
      const formData = new FormData();
      formData.append("docTypeStr", data.docTypeStr);
      formData.append("file", data.file[0]);
      if (authUser.role !== "HR") {
        formData.append("ownerId", ownerId);
      }
      formData.append("travelId", travelId);
      await uploadTravelDocument(formData);
      toast.success("Document uploaded successfully!");
      reset();
    } catch (err) {
      console.error(err);
      toast.error("Error uploading document");
    }
  };
  
  return (
    <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow mt-[40px]">
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
            {...register("file", { required: "File is required" })}
            className="border rounded px-3 py-2 w-full"
          />
          {errors.file && (
            <p className="text-red-500 text-sm mt-1">{errors.file.message}</p>
          )}
        </div>
        <button
          type="submit"
          disabled={isSubmitting}
          className="px-4 py-2 bg-black text-white rounded hover:bg-gray-700"
        >
          {isSubmitting ?   "Uploading..." : "upload"}
        </button>
      </form>
    </div>
  );
}
