import apiClient from "./apiClient";
const CONTROLLER = "/notifications";

export const getAllNotifications = (empId , pageNo = 0, pageSize = 10) => {
    return apiClient.get(`${CONTROLLER}/${empId}?pageNo=${pageNo}&pageSize=${pageSize}`);
}
export const marksAsRead = (notificationId) => {
    return apiClient.patch(`${CONTROLLER}/${notificationId}/read`);
}
export const getUnreadCount = (empId) =>{
    return apiClient.get(`${CONTROLLER}/unread-count/${empId}`);
}