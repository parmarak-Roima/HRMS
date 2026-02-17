import apiClient from "./apiClient";
const CONTROLLER = "/config";

export const fetchAllConfiguration = () => {
  return apiClient(`${CONTROLLER}`);
};
export const fetchAllKey = () => {
  return apiClient.get(`${CONTROLLER}/keys`);
};
export const updateConfig = (configId, data) => {
  return apiClient.patch(`${CONTROLLER}/${configId}`, data, {
    headers: { "Content-Type": "application/json" },
  });
};
