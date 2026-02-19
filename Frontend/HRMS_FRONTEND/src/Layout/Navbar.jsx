import {
  NavigationMenu,
  NavigationMenuList,
  NavigationMenuItem,
  NavigationMenuLink,
} from "@/components/ui/navigation-menu";
import { Button } from "@/components/ui/button";
import { NavLink, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAuthUserContext } from "../Contexts/AuthUserContext";
import { getUnreadCount } from "../Services/NotificationService";
import { handleGlobalError } from "../Services/GlobalExceptionService";

export default function Navbar() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const Navigate = useNavigate();

  const [notificationCount, setNotificationCount] = useState();
  useEffect(() => {
    fetchUnreadCount();
  }, [authUser]);

  const fetchUnreadCount = async () => {
    try{
    const res = await getUnreadCount(authUser.id);
    setNotificationCount(res.data);
    }catch(e){
      handleGlobalError(e);
    }
  };

  return (
    <nav className="fixed top-0 left-0 w-full z-50  bg-gray-800 text-white px-6 py-3 flex items-center justify-between shadow mb-7.5">
      <div className="text-xl font-bold">HRMS</div>
      {authUser && (
        <NavigationMenu>
          <NavigationMenuList className="flex space-x-4">
            <NavigationMenuItem>
              <NavigationMenuLink asChild>
                <NavLink
                  to={`/profile/${authUser?.id}`}
                  className={({ isActive }) =>
                    `px-3 py-2 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-700 text-white"
                        : "text-gray-300 hover:bg-gray-700 hover:text-white"
                    }`
                  }
                >
                  Profile
                </NavLink>
              </NavigationMenuLink>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <NavigationMenuLink asChild>
                <NavLink
                  to="/travel"
                  className={({ isActive }) =>
                    `px-3 py-2 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-700 text-white"
                        : "text-gray-300 hover:bg-gray-700 hover:text-white"
                    }`
                  }
                >
                  Travel
                </NavLink>
              </NavigationMenuLink>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <NavigationMenuLink asChild>
                <NavLink
                  to={`/orgChart/${authUser.id}`}
                  className={({ isActive }) =>
                    `px-3 py-2 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-700 text-white"
                        : "text-gray-300 hover:bg-gray-700 hover:text-white"
                    }`
                  }
                >
                  Org-Chart
                </NavLink>
              </NavigationMenuLink>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <NavigationMenuLink asChild>
                <NavLink
                  to="/game-booking"
                  className={({ isActive }) =>
                    `px-3 py-2 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-700 text-white"
                        : "text-gray-300 hover:bg-gray-700 hover:text-white"
                    }`
                  }
                >
                  Game Booking
                </NavLink>
              </NavigationMenuLink>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <NavigationMenuLink asChild>
                <NavLink
                  to="/Achivements"
                  className={({ isActive }) =>
                    `px-3 py-2 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-700 text-white"
                        : "text-gray-300 hover:bg-gray-700 hover:text-white"
                    }`
                  }
                >
                  Achivements
                </NavLink>
              </NavigationMenuLink>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <NavigationMenuLink asChild>
                <NavLink
                  to="/jobOpening"
                  className={({ isActive }) =>
                    `px-3 py-2 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-700 text-white"
                        : "text-gray-300 hover:bg-gray-700 hover:text-white"
                    }`
                  }
                >
                  Job-Openings
                </NavLink>
              </NavigationMenuLink>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <NavigationMenuLink asChild>
                <NavLink
                  to={`/notification/${authUser.id}`}
                  className={({ isActive }) =>
                    `px-3 py-2 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-700 text-white"
                        : "text-gray-300 hover:bg-gray-700 hover:text-white"
                    }`
                  }
                >
                  <div className="flex justify-center">
                  <span className={
                     notificationCount != 0 && "text-red-500"
                  }>Notifications</span>
                  {/* <span className={
                    notificationCount != 0 && "text-red-500"
                  }
                  >({notificationCount})</span> */}
                  </div>
                </NavLink>
              </NavigationMenuLink>
            </NavigationMenuItem>
          </NavigationMenuList>
        </NavigationMenu>
      )}
      <div className="space-x-2 flex ">
        {!authUser ? (
          <>
            <Button
              variant="default"
              className="bg-black hover:bg-gray-600"
              onClick={() => Navigate("/login")}
            >
              Login
            </Button>
          </>
        ) : (
          <Button
            variant="destructive"
            onClick={() => {
              setAuthUser(null);
              localStorage.clear("token");
              Navigate("/login");
            }}
          >
            Logout
          </Button>
        )}
        <p className="flex justify-center px-2.5 py-2 bg-white text-black rounded-full">
          {authUser?.role}
        </p>
      </div>
    </nav>
  );
}
