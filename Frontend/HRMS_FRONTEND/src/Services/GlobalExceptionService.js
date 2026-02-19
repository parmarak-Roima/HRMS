import { toast } from "react-toastify";

export const handleGlobalError = (error) => {
  let validationErrors = error?.data?.validationErrors || [];
  let message = error?.data?.message;
  console.log(message);
  console.log(validationErrors)
  const showErrorsSequentially = (errors, delay = 1500) => {
    errors.forEach((err, index) => {
      setTimeout(() => {
        toast.error(err);
      }, index * delay);
    });
  };
  if (validationErrors.length > 0) {
    console.log("hello");
    showErrorsSequentially(validationErrors);
  } else if (message) {
    toast.error(message);
  }
};
