import apiClient from "./apiClient";
const CONTROLLER = "/notifications";

export const getAllNotifications = (empId) => {
    return apiClient.get(`${CONTROLLER}/${empId}`);
}
export const marksAsRead = (notificationId) => {
    return apiClient.patch(`${CONTROLLER}/${notificationId}/read`);
}
export const getUnreadCount = (empId) =>{
    return apiClient.get(`${CONTROLLER}/unread-count/${empId}`);
}