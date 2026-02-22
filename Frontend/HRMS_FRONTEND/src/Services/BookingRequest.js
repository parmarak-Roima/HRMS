import apiClient from "./apiClient";
const CONTROLLER = "/game/booking";

export const getMyBookingHistory = () => {
  return apiClient.get(`${CONTROLLER}/history`);
};

export const cancelBooking = (bookingId) => {
  return apiClient.delete(`${CONTROLLER}/${bookingId}`);
};

export const makeBookingRequest = (payload) => {
  return apiClient.post(`${CONTROLLER}`, payload, {
    headers: { "Content-Type": "application/json" },
  });
};

export const getRequestsForSlot = (slotId) => {
  return apiClient(`${CONTROLLER}/slot/${slotId}`);
};