import apiClient from "./apiClient";
const CONTROLLER = "/auth";

export const loginUser = (payload) => {
   
  return apiClient.post(`${CONTROLLER}/login`, payload);
};
