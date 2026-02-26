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
  const [eventSource, setEventSource] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get("http://localhost:8080/auth", {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          setAuthUser(response.data.data);
          //make a connection with userId
          const sseUrl = `http://localhost:8080/notifications/stream/${response.data.data.id}`;
          const eventSource = new EventSource(sseUrl);
          console.log(eventSource)
          setEventSource(eventSource);
          //add event listner to show notification
          eventSource.addEventListener("new-notification", (event) => {
            try {
              toast.info(JSON.parse(event.data).message)
            } catch (error) {
              console.error("Error parsing SSE data", error);
            }
          });

          //if any error comes in connecting 
          eventSource.onerror = (error) => {
            console.error("SSE connection error", error);
            eventSource.close();
          };

          //clean up the connection
          return () => {
            eventSource.close();
          };
        })
        .catch((error) => {
          toast.error("login again");
          navigate("/login");
          setAuthUser(null);
        })
        .finally(() => setLoading(false));
    } else {
      navigate("/login");
      setLoading(false);
    }
  }, []);

  return (
    <AuthUserContext.Provider value={{ authUser, setAuthUser, eventSource }}>
      {!loading && children}
    </AuthUserContext.Provider>
  );
}

export const useAuthUserContext = () => useContext(AuthUserContext);
