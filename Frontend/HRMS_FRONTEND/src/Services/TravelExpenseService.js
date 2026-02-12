import apiClient from "./apiClient";
const CONTROLLER = "/expenses";

export const fetchAllExpenseType = () => {
  return apiClient.get(`${CONTROLLER}/expenseType`);
}
export const createExpense = (data) => {
  return apiClient.post(`${CONTROLLER}`,
    data,
    { 
        headers: {
          "Content-Type": "multipart/form-data",
        },
    }
  );
}

export const fetchTravelExpense = (travelAssignmentId) => {
  return apiClient.get(`${CONTROLLER}/${travelAssignmentId}`);
}

export const submitExpenseById = (travelExpenseId) => {
  return apiClient.patch(`${CONTROLLER}/${travelExpenseId}/Employee`,{},
    {
        headers: { "Content-Type": "application/json" },
    }
);
}

export const repondToExpense = (travelExpenseId,data) => {
  return apiClient.patch(`${CONTROLLER}/${travelExpenseId}/hr`,data,
    {
        headers: { "Content-Type": "application/json" },
    }
);
}