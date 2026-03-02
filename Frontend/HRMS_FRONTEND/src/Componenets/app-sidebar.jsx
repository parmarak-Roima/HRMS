import {
  Calendar,
  User2,
  Home,
  Inbox,
  Search,
  Settings,
  Helicopter,
  Pyramid,
  Joystick,
  Trophy,
  CircleFadingPlus,
  Bell,
  LogOut,
} from "lucide-react";

import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarFooter,
} from "@/components/ui/sidebar";
import { useAuthUserContext } from "../Contexts/AuthUserContext";
import { useNavigate } from "react-router-dom";
import { getUnreadCount } from "../Services/NotificationService";
import { useEffect, useState } from "react";
import HRMS from "../assets/HRMS.jpg"
import { handleGlobalError } from "../Services/GlobalExceptionService";
export function AppSidebar() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const [notificationCount, setNotificationCount] = useState();
  
  useEffect(() => {
      fetchUnreadCount();
    }, [authUser]);
  
    const fetchUnreadCount = async () => {
      try{
        if( !authUser?.id ){
          return
        }
      const res = await getUnreadCount(authUser?.id);
      setNotificationCount(res.data);
      }catch(e){
        console.log(e)
        handleGlobalError(e);
      }
    };
  const Navigate = useNavigate()
  const items = [
    {
      title: "Profile",
      url: `/profile/${authUser?.id}`,
      icon: User2,
    },
    {
      title: "Travel",
      url: "/travel",
      icon: Helicopter,
    },
    {
      title: "Org-Chart",
      url: `/orgChart/${authUser?.id}`,
      icon: Pyramid,
    },
    {
      title: "Game Booking",
      url: `/game/booking`,
      icon: Joystick,
    },
    {
      title: "Achivements",
      url: "/achievements",
      icon: Trophy,
    },
    {
      title: "Job-Openings",
      url: "/jobOpening",
      icon: CircleFadingPlus,
    },
  ];

  let notificationItem = {
      title: "Notifications",
      url: `/notification/${authUser?.id}`,
      icon: Bell,
  }

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>
            
            <div className="text-xl font-bold"><img src={HRMS} alt="" width ={140} /></div>
          </SidebarGroupLabel>
          <SidebarGroupContent className= " mt-4">
            <SidebarMenu>
              {items.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <a href={item.url}>
                      <item.icon />
                      <span>{item.title}</span>
                    </a>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
            <SidebarMenu>
                <SidebarMenuItem key={notificationItem.title}>
                  <SidebarMenuButton asChild>
                    <a href={notificationItem.url}>
                      <notificationItem.icon />
                      <span className="w-22">{notificationItem.title}</span><span className="mt-1">{notificationCount}</span>
                    </a>
                  </SidebarMenuButton>
                </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      <SidebarFooter>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton>
              <User2 />{" "}
              {authUser && (
                <div className="flex justify-between">
                  {" "}
                  <span className="w-45"> {authUser?.email}</span>
                  <span
                    className="h-0.5"
                    onClick={() => {
                      setAuthUser(null);
                      localStorage.clear("token");
                      Navigate("/login");
                    }}
                  >
                    {" "}
                    <LogOut />
                  </span>
                </div>
              )}
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
}
