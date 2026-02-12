import apiClient from "./apiClient";
const CONTROLLER = "/assignments";

export const fetchEmployeeTravel = (empId) => {
  return apiClient.get(`${CONTROLLER}/employee/${empId}`);
};
export const getTeamTravel = (managerId) => {
    return apiClient.get(`${CONTROLLER}/manager/${managerId}`)
}
export const getTravelAssignmentId = (travelId,empId) => {
    return apiClient.get(`${CONTROLLER}/travelAssignmentId/${travelId}/${empId}`)
}