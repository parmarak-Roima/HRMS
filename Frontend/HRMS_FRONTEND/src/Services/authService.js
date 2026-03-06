import apiClient from "./apiClient";
const CONTROLLER = "/auth";

export const loginUser = (payload) => {
  return apiClient.post(`${CONTROLLER}/login`, payload, {
    headers: { "Content-Type": "application/json" },
  });
};
export const fetchEmployeeById = (id) => {
  return apiClient.get(`${CONTROLLER}/employee/${id}`);
};
export const fetchAllEmployee = () => {
  return apiClient.get(`${CONTROLLER}/employee`);
};
export const fetchBirthDayEmployee = () => {
  return apiClient.get(`${CONTROLLER}/birthday`);
};
export const fetchJoiningDayEmployee = () => {
  return apiClient.get(`${CONTROLLER}/joiningAniversary`);
};
export const fetchAllHrs = () => {
  return apiClient.get(`${CONTROLLER}/hr`);
};
export const sendOtp = (email) => {
  return apiClient.post(
    `${CONTROLLER}/forgot-password/${email}`,
  );
};
export const changePassowrd = (email, payload) => {
  return apiClient.post(`${CONTROLLER}/new-passoword/${email}`, payload, {
    headers: { "Content-Type": "application/json" },
  });
};
