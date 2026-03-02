import { toast } from "react-toastify";
import { fetchEmployeeById } from "../Services/authService";
import { handleGlobalError } from "../Services/GlobalExceptionService";

const loader = async ({ params }) => {
  try {
    console.log(params)
    if( params?.id == "undefined" ){
      toast.error("please login")
      return;
    }
    const data = await fetchEmployeeById(params?.id);
    console.log(data.data);
    return data.data;
  } catch (e) {
    console.log(e)
    handleGlobalError(e)
  } finally {
  }
};
export default loader;
