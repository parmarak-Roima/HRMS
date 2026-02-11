import apiClient from "./apiClient";
const CONTROLLER = "/auth";

export const loginUser = (payload) => {
  return apiClient.post(`${CONTROLLER}/login`, payload,
     {
    headers: { "Content-Type": "application/json" },
  }
  );
};
export const fetchEmployeeById = (id) => {
  return apiClient.get(`${CONTROLLER}/employee/${id}`
  );
}
export const fetchAllEmployee = () => {
  return apiClient.get(`${CONTROLLER}/employee`);
}
