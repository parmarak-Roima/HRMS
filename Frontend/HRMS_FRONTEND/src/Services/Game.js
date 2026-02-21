import apiClient from "./apiClient";
const CONTROLLER = "/game/config";

export const gameById = (gameId) => {
  return apiClient(`${CONTROLLER}/${gameId}`);
};

export const getAllGameConfig = () => {
  return apiClient(`${CONTROLLER}`);
};

export const updateGameConfig = (gameId , payload) => {
  return apiClient.patch(`${CONTROLLER}/${gameId}`, payload, {
    headers: { "Content-Type": "application/json" },
  });
};  
