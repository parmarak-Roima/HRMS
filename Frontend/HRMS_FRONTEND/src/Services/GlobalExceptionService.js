import { toast } from "react-toastify";

export const handleGlobalError = (error) => {
  let validationErrors = error?.data?.validationErrors || [];
  let message = error?.data?.message;
  const showErrorsSequentially = (errors, delay = 1500) => {
    errors.forEach((err, index) => {
      setTimeout(() => {
        toast.error(err);
      }, index * delay);
    });
  };
  if (validationErrors) {
    console.log("hello");
    showErrorsSequentially(validationErrors);
  } else if (message) {
    toast.error(message);
  }
};
