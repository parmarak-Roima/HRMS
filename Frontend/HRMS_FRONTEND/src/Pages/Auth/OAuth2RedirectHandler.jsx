import React, { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { Loader } from "../../components/ui/Loader";
import axios from "axios";

const OAuth2RedirectHandler = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { setAuthUser } = useAuthUserContext();

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    if (token) {
      localStorage.setItem("token", token);
     //fetch autheuser from token
      axios
        .get("http://localhost:8080/auth", {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          setAuthUser(response.data.data);
          navigate(`/profile/${response.data.data.id}`);
        });      
    } else {
      navigate("/login");
    }
  }, [location, setAuthUser]);

  return (
    <div className="flex justify-center items-center h-screen">
      <Loader size={48} />
      <p className="ml-4 text-gray-600">Authenticating...</p>
    </div>
  );
};
export default OAuth2RedirectHandler;
