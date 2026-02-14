import apiClient from "./apiClient";
const CONTROLLER = "/org-chart";

export const getOrgChartByEmpId = (empId) => {
    return apiClient(`${CONTROLLER}/id/${empId}`);
}
export const getOrgChartByEmailId = (emailId) => {
    return apiClient(`${CONTROLLER}/email/${emailId}`);
}