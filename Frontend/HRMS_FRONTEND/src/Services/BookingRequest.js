import apiClient from "./apiClient";
const CONTROLLER = "/game/booking";

export const getMyBookingHistory = () => {
  return apiClient.get(`${CONTROLLER}/history`);
};