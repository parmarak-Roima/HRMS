import { z } from "zod";

const today = new Date();
today.setHours(0, 0, 0, 0);
export const createTravelSchema = z.object({
  destination: z
    .string()
    .min(1, { message: "Destination is required" }),

  description: z
    .string()
    .optional(),

  startDate: z
    .string()
    .refine((val) => {
      const date = new Date(val);
      return date > today;
    }, { message: "Start Date must be in the future" }),

  endDate: z
    .string()
    .refine((val) => {
      const date = new Date(val);
      return date > today;
    }, { message: "End Date must be in the future" }),

  requiredDocs: z
    .string()
    .optional(),
});