export const getAllEmployee = async () => {
      try {
        const response = await fetchAllEmployee();
        return response.data
    
      } catch (err) {
        handleGlobalError(err);
      }
    };