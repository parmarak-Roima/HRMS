import axios from "axios";
import React, { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export const AuthUserContext = createContext();

export default function AuthUserContextProvider({ children }) {
// email: "test2@gmail.com"
// id: 1
// name: "test2"
// role: "HR"

  const [authUser, setAuthUser] = useState(null);
  const [loading, setLoading] = useState(true);
    const navigate =  useNavigate();
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get("http://localhost:8080/auth", {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
            console.log(response.data.data)
             
          setAuthUser(response.data.data);
        })
        .catch((error) => {
            toast.error("login again");
            navigate("/login")
          setAuthUser(null);
        })
        .finally(() => setLoading(false));
    } else {
      navigate("/login")
      setLoading(false);
    }
  }, []);

  return (
    <AuthUserContext.Provider value={{ authUser, setAuthUser }}>
      {!loading && children} 
    </AuthUserContext.Provider>
  );
}

export const useAuthUserContext = () => useContext(AuthUserContext);