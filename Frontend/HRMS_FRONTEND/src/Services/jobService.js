import apiClient from "./apiClient";
const CONTROLLER = "/jobs";

// export const getAllActiveJobs = () => {
//   return apiClient(`${CONTROLLER}/active`);
// };

export const createJobOpening = (data) => {
  return apiClient.post(`${CONTROLLER}`, data, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const updateJobStatus = (jobId, status) => {
  return apiClient.patch(
    `${CONTROLLER}/${jobId}/${status}`,
    {},
    {
      headers: { "Content-Type": "application/json" },
    },
  );
};

export const shareJob = (data) => {
  return apiClient.post(`${CONTROLLER}/share`, data, {
    headers: { "Content-Type": "application/json" },
  });
};

export const getAllActiveJobs = async (pageNo = 0, pageSize = 2) => {
  return await apiClient(`${CONTROLLER}?pageNo=${pageNo}&pageSize=${pageSize}`);
};