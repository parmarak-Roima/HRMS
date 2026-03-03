import apiClient from "./apiClient";
const CONTROLLER = "/assignments";

export const fetchEmployeeTravel = (empId) => {
  return apiClient.get(`${CONTROLLER}/employee/${empId}`);
};
export const getTeamTravel = (managerId) => {
  return apiClient.get(`${CONTROLLER}/manager/${managerId}`);
};
export const getTravelAssignmentId = (travelId, empId) => {
  return apiClient.get(`${CONTROLLER}/travelAssignmentId/${travelId}/${empId}`);
};

export const createTravelAssignment = (travelId, empId) => {
  const payload = {
    travelId: travelId,
    employeeId: empId,
  };
  return apiClient.post(`${CONTROLLER}`, payload, {
    headers: { "Content-Type": "application/json" },
  });
};

export const cancelAssignment = (travelId , empId) => {
  const payload = {
    status: "CANCELLED",
  };
  return apiClient.patch(`${CONTROLLER}/${travelId}/${empId}`, payload, {
    headers: { "Content-Type": "application/json" },
  });
};
