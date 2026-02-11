import { Controller, useForm } from "react-hook-form";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { createTravelSchema } from "../../ValidationSchemas/createTravelSchema";
import { toast } from "react-toastify";
import { createTravel } from "../../Services/TravelService";
import { zodResolver } from "@hookform/resolvers/zod/src/zod.js";
import { useEffect, useState } from "react";
import { fetchAllEmployee } from "../../Services/authService";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";

function CreateTravel() {
    const { authUser, setAuthUser } = useAuthUserContext();
  const [employees, setEmployees] = useState([]);
  const [selectedEmployeeIds, setSelectedEmployeeIds] = useState([]);
  useEffect(() => {
    const getAllEmployee = async () => {
      try {
        const response = await fetchAllEmployee();
        setEmployees(response.data);
      } catch (err) {
        toast.error(err?.data?.message);
      }
    };
    getAllEmployee();
  }, []);

  const form = useForm({
    defaultValues: {
      destination: "",
      description: "",
      startDate: "",
      endDate: "",
      status: "SCHEDULED",
      requiredDocs: "",
    },
    resolver: zodResolver(createTravelSchema),
  });
  const toggleEmployee = (id) => {
    setSelectedEmployeeIds((prev) =>
      prev.includes(id) ? prev.filter((empId) => empId !== id) : [...prev, id],
    );
  };
  const onSubmit = async (values) => {
    try {
      const payload = {
        ...values,
        employeeIdsToAssign: selectedEmployeeIds,
      };
      const res = await createTravel(payload);
      toast.success("Travel created successfully!");
      form.reset();
    } catch (err) {
      console.error(err);
      toast.error("Error creating travel");
    }
  };
  if( authUser.role != "HR" )return <p>you can not access this page !!</p>
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
          <div>
            <label className="block text-sm font-medium mb-2">
              Assign Employees
            </label>
            <div className="border rounded p-3 max-h-48 overflow-y-auto space-y-2">
              {employees.map((emp) => (
                <label key={emp.id} className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    checked={selectedEmployeeIds.includes(emp.id)}
                    onChange={() => toggleEmployee(emp.id)}
                    className="h-4 w-4"
                  />
                  <span>{emp.email}</span>
                </label>
              ))}
            </div>
          </div>

          <Button type="submit" className="w-full">
            Create Travel
          </Button>
        </form>
      </Form>
    </div>
  );
}

export default CreateTravel;
