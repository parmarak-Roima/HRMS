import apiClient from "./apiClient";
const CONTROLLER = "/game/interested";

export const getGameInterests = () => {
    return apiClient.get(`${CONTROLLER}`);
}

export const toggelInterest = (gameId) => {
    return apiClient.patch(`${CONTROLLER}/${gameId}`)
}

export const getGameInterestedPlayerByGame = (gameId) => {
    return apiClient.get(`${CONTROLLER}/employee/${gameId}`)
}