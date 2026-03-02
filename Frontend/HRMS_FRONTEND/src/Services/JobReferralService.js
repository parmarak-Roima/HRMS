import apiClient from "./apiClient";
const CONTROLLER = "/referrals";

export const createReferral = (data) => {
  return apiClient.post(`${CONTROLLER}`, data,{ 
        headers: {
          "Content-Type": "multipart/form-data",
        },
    });
};

export const getReferralByJobId = (jobId) => {
  return apiClient(`${CONTROLLER}/${jobId}`);
};
export const getReferralByJobIdAndReferrar = (jobId) => {
  return apiClient(`${CONTROLLER}/Employee/${jobId}`);
};

export const updateJobReferralStatus = (jobReferrarlId, status) => {
  return apiClient.patch(
    `${CONTROLLER}/${jobReferrarlId}/${status}`,
    {},
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    },
  );
};
