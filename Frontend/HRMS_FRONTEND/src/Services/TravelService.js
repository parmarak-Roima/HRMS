import apiClient from "./apiClient";
const CONTROLLER = "/travel";

export const fetchTravelById = (id) => {
  return apiClient.get(`${CONTROLLER}/${id}`);
}

export const fetchAllTravel = () => {
    return apiClient.get(`${CONTROLLER}/all`)
}
export const createTravel = (payload) => {
    return apiClient.post(`${CONTROLLER}`,payload,
        {
    headers: { "Content-Type": "application/json" },
  }
    )
}

