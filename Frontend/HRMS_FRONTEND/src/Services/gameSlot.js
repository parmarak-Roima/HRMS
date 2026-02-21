import apiClient from "./apiClient";
const CONTROLLER = "/game/slot";

export const getAllUpcomingSlot = (gameId) => {
    return apiClient.get(`${CONTROLLER}/${gameId}`);
}
