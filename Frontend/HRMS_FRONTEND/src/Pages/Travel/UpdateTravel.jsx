import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchTravelById, updateTravel } from "../../Services/TravelService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useForm } from "react-hook-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { createTravelSchema } from "../../ValidationSchemas/createTravelSchema";
import { zodResolver } from "@hookform/resolvers/zod/src/zod.js";
import { toast } from "react-toastify";
import Select from "react-select";

const statuses = ["SCHEDULED", "CANCELLED"];

function UpdateTravel() {
  const { travelId } = useParams();
  const { authUser, setAuthUser } = useAuthUserContext();
  const [travel, settravel] = useState({});
  const [travelStatus, setTravelStatus] = useState(travel?.status);
  const navigate = useNavigate();
  const { data, error, isPending, isError, isSuccess } = useQuery({
    queryKey: ["travel", parseInt(travelId)],
    queryFn: () => {
      return fetchTravelById(travelId);
    },
    staleTime: 5 * 60 * 200,
  });

  const form = useForm({
    defaultValues: {
      ...travel,
    },
    resolver: zodResolver(createTravelSchema),
  });

  const options = statuses.map((status) => ({
    value: status,
    label: status,
  }));
  const handleChange = (selected) => {
    setTravelStatus(selected);
  };
  const queryClient = useQueryClient();
  const mutation = useMutation({
    mutationFn: updateTravel,
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["my-travels"] }),
        queryClient.invalidateQueries({ queryKey: ["travel",parseInt(travelId)] }),
      ]);
      toast.success("Travel updated successfully!");
      form.reset();
      navigate(`/travel/${travelId}/${authUser.id}`);
    },
    onError: (error) => {
      handleGlobalError(error);
    },
  });
  const onSubmit = async (data) => {
    if (data.startDate > data.endDate) {
      toast.warn("end date should be after start date");
      return;
    }
    const payload = {
      ...data,
      status: travelStatus.value,
      travelId: travelId,
    };
    mutation.mutate(payload);
  };

  useEffect(() => {
    settravel(data?.data);
    form.reset(data?.data);
    setTravelStatus({
      value: data?.data.status,
      label: data?.data.status,
    });
  }, [data]);

  if (isError) {
    handleGlobalError(error);
  }
  if (isSuccess) {
    console.log(travel);
  }
  return (
    <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow">
      <h2 className="text-2xl font-bold mb-4">Create Travel</h2>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="destination"
            rules={{ required: "Destination is required" }}
            render={({ field }) => (
              <FormItem>
                <FormLabel>Destination</FormLabel>
                <FormControl>
                  <Input placeholder="Enter destination" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="description"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Description</FormLabel>
                <FormControl>
                  <Textarea placeholder="Enter description" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="startDate"
            rules={{ required: "Start date is required" }}
            render={({ field }) => (
              <FormItem>
                <FormLabel>Start Date</FormLabel>
                <FormControl>
                  <Input type="date" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="endDate"
            rules={{ required: "End date is required" }}
            render={({ field }) => (
              <FormItem>
                <FormLabel>End Date</FormLabel>
                <FormControl>
                  <Input type="date" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="requiredDocs"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Required Documents</FormLabel>
                <FormControl>
                  <Input placeholder="Enter required documents" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <label className="block text-sm font-medium mb-2">Status</label>
          <Select
            options={options}
            onChange={handleChange}
            value={travelStatus}
          />
          <button
            // disabled = {mutation.isPending}
            className=" w-full px-4 py-1.5 mt-4 bg-black text-white rounded hover:bg-gray-700"
            type="submit"
          >
            {/* { mutation.isPending ? "Creating travel..." : "Create Travel" }  */}
            Update
          </button>
        </form>
      </Form>
    </div>
  );
}

export default UpdateTravel;
