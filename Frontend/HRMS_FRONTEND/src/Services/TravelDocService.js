import apiClient from "./apiClient";
const CONTROLLER = "/travelDoc";

export const fetchAllDocs = (travelId , empId) => {
  return apiClient.get(`${CONTROLLER}/${travelId}/employee/${empId}`);
}

export const uploadTravelDocument = (data) => {
    return apiClient.post(`${CONTROLLER}`,data,
       { headers: {
      "Content-Type": "multipart/form-data",
    },}
    )
}