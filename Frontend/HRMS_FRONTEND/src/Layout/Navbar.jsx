import { NavigationMenu, NavigationMenuList, NavigationMenuItem, NavigationMenuLink } from "@/components/ui/navigation-menu";
import { Button } from "@/components/ui/button";
import { NavLink, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useAuthUserContext } from "../Contexts/AuthUserContext";

export default function Navbar() {
  const {authUser,setAuthUser} = useAuthUserContext();
  const Navigate = useNavigate();
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
                to="/argChart"
                className={({ isActive }) =>
                  `px-3 py-2 rounded-md transition-colors ${
                    isActive
                      ? "bg-gray-700 text-white"
                      : "text-gray-300 hover:bg-gray-700 hover:text-white"
                  }`
                }
              >
                Arg-Chart
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
                to={`/notification/${authUser.id}`}
                className={({ isActive }) =>
                  `px-3 py-2 rounded-md transition-colors ${
                    isActive
                      ? "bg-gray-700 text-white"
                      : "text-gray-300 hover:bg-gray-700 hover:text-white"
                  }`
                }
              >
                Notifications
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
              className="bg-blue-500 hover:bg-blue-600"
              onClick={()=> Navigate("/login")}
            >
              Login
            </Button>
          </>
        ) : (
          <Button
            variant="destructive"
            onClick={() => {setAuthUser(null)
              localStorage.clear("token");
              Navigate("/login")
            }
            }
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
