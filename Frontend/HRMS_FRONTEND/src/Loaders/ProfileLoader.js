import { toast } from "react-toastify";
import { fetchEmployeeById } from "../Services/authService";

const loader = async ({ params }) => {
  try {
    
    const data = await fetchEmployeeById(params?.id);
    console.log(data.data);
    return data.data;
  } catch (e) {
    toast.error(e.data.message);
  } finally {
  }
};
export default loader;
