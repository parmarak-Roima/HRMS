import { useState, useEffect } from "react";
import { Clock } from "lucide-react";
import { Loader } from "../../components/ui/Loader";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";
import {
  getAllNotifications,
  marksAsRead,
} from "../../Services/NotificationService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useQuery } from "@tanstack/react-query";

const NotificationPage = () => {
  const { userId } = useParams();
  const [notifications, setNotifications] = useState([]);
 

  const { data, error, isPending, isError, isSuccess } = useQuery({
    queryKey: ["notifications",userId],
    queryFn:async () => getAllNotifications(userId),
    staleTime: 5*20*600,
  });

  useEffect(() => {
    setNotifications(data?.data)
  }, [data]);

  if( isError ){
    handleGlobalError(error)
  }

 
  const handleNotificationClick = async (id, read) => {
    if (read) return;
    try {
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n)),
      );
      await marksAsRead(id);
    } catch (error) {
        handleGlobalError(error)
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6 min-h-screen bg-white">
      <div className="flex items-center justify-between mb-8 border-b pb-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Notifications</h1>
        </div>
      </div>
      <div className="space-y-4">
        {isPending ? (
          <Loader size={32} />
        ) : notifications?.length === 0 ? (
          <p className="text-center text-gray-500 py-10">
            No notifications found for you.
          </p>
        ) : (
          notifications?.map((notification) => (
            <div
              key={notification.id}
              onClick={() =>
                handleNotificationClick(notification.id, notification.read)
              }
              className={`
                relative p-4 rounded-lg border 
                ${
                  notification.read
                    ? "bg-white border-gray-200 text-gray-600"
                    : "bg-blue-50 border-blue-200 shadow-sm text-gray-900"
                }
                hover:shadow-md
              `}
            >
              <div className="flex items-start space-x-4">
                <div className="flex-1">
                  <p
                    className={`text-sm font-medium ${notification.read ? "" : "font-semibold"}`}
                  >
                    {notification.message}
                  </p>
                  <div className="flex items-center mt-1 text-xs text-gray-400">
                    <Clock size={12} className="mr-1" />
                    {notification.createdAt}
                    <span className="mx-2">•</span>
                    <span className="uppercase tracking-wide">
                      {notification.type}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default NotificationPage;
