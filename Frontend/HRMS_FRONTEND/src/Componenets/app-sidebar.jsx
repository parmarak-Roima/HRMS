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
  Columns3Cog,
  Bolt,
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
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
  CommandShortcut,
} from "@/components/ui/command";
import { Calculator, CreditCard, Smile, User } from "lucide-react";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";
import { useAuthUserContext } from "../Contexts/AuthUserContext";
import { useNavigate } from "react-router-dom";
import { getUnreadCount } from "../Services/NotificationService";
import { useEffect, useState } from "react";
import HRMS from "../assets/HRMS.jpg";
import { handleGlobalError } from "../Services/GlobalExceptionService";

export function AppSidebar() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const [notificationCount, setNotificationCount] = useState();

  useEffect(() => {
    fetchUnreadCount();
  }, [authUser]);

  const fetchUnreadCount = async () => {
    try {
      if (!authUser?.id) {
        return;
      }
      const res = await getUnreadCount(authUser?.id);
      setNotificationCount(res.data);
    } catch (e) {
      console.log(e);
      handleGlobalError(e);
    }
  };
  const Navigate = useNavigate();
  const items = [
    {
      title: "Profile",
      url: `/profile/${authUser?.id}`,
      icon: User2,
      heading: "Profile",
    content:
      "You can see your details and today's birthday and todays's joining aniversary..",
    },
    {
      title: "Travel",
      url: "/travel",
      icon: Helicopter,
       heading: "Travel",
    content:
      "You can see your travels , travel assignments , uploaded documents or expenses..",
    },
    {
      title: "Org-Chart",
      url: `/orgChart/${authUser?.id}`,
      icon: Pyramid,
       heading: "Org-Chart",
    content:
      "You can see your or any employees organization chart..",
    },
    {
      title: "Game Booking",
      url: `/game/booking`,
      icon: Joystick,
       heading: "Game-Booking",
    content:
      "You can Book a upcoming slots..",
    },
    {
      title: "Achivements",
      url: "/achievements",
      icon: Trophy,
       heading: "Achivements",
    content:
      "You can see Achivement post from your collegue..",
    },
    {
      title: "Job-Openings",
      url: "/jobOpening",
      icon: CircleFadingPlus,
       heading: "Job-Opening",
    content:
      "You can see job opening , share or referr to your friend..",
    },
  ];

  let notificationItem = {
    title: "Notifications",
    url: `/notification/${authUser?.id}`,
    icon: Bell,
    heading: "Notifications",
    content:
      "You can see your notification related to travel , achivements , gaming , org etc..",
  };
  let configs = {
    title: "Config",
    url: `/config`,
    icon: Columns3Cog,
    heading: "All Configuration",
    content: "You can see or change configurations...",
  };
  let GameConfig = {
    title: "Game-Config",
    url: `/game/config`,
    icon: Bolt,
    heading: "Game-Configuration",
    content:
      "You can see games configuration or update them and you can monitor slots...",
  };
  return (
    <>
      <Sidebar>
        <SidebarContent>
          <SidebarGroup>
            <SidebarGroupLabel>
              <div className="text-xl font-bold">
                <img src={HRMS} alt="" width={140} />
              </div>
            </SidebarGroupLabel>
            <SidebarGroupContent className=" mt-4">
              <SidebarMenu>
                <Command className="max-w-sm rounded-lg border">
                  <CommandInput placeholder="Type a command or search..." />
                  <CommandList>
                    <CommandEmpty>No results found.</CommandEmpty>
                    <CommandGroup heading="Suggestions">
                      {items.map((item) => (
                        <HoverCard openDelay={10} closeDelay={100}>
                        <HoverCardTrigger asChild>
                          <SidebarMenuItem key={item.title}>
                          <SidebarMenuButton asChild>
                            <a href={item.url}>
                              <CommandItem>
                                <item.icon />
                                <span>{item.title}</span>
                              </CommandItem>
                            </a>
                          </SidebarMenuButton>
                        </SidebarMenuItem>
                        </HoverCardTrigger>
                        <HoverCardContent side="left"  className="flex w-64 flex-col gap-0.5">
                          <div className="font-semibold">
                            {item.heading}
                          </div>
                          <div>{item.content}</div>
                        </HoverCardContent>
                      </HoverCard>
                        
                      ))}
                      <HoverCard openDelay={10} closeDelay={100}>
                        <HoverCardTrigger asChild>
                          <SidebarMenuButton asChild>
                            <a href={notificationItem.url}>
                              <CommandItem>
                                <notificationItem.icon />

                                <span className="w-22">
                                  {notificationItem.title}
                                </span>
                                <span className="mt-1">
                                  {notificationCount}
                                </span>
                              </CommandItem>
                            </a>
                          </SidebarMenuButton>
                        </HoverCardTrigger>
                        <HoverCardContent side="left" className="flex w-64 flex-col gap-0.5">
                          <div className="font-semibold">
                            {notificationItem.heading}
                          </div>
                          <div>{notificationItem.content}</div>
                        </HoverCardContent>
                      </HoverCard>

                      <SidebarMenuItem key={configs.title}>
                        {authUser?.role == "HR" && (
                          <>
                            <HoverCard openDelay={10} closeDelay={100}>
                              <HoverCardTrigger asChild>
                                <SidebarMenuButton asChild>
                                  <a href={configs.url}>
                                    <CommandItem>
                                      <configs.icon />

                                      <span className="w-22">
                                        {configs.title}
                                      </span>
                                    </CommandItem>
                                  </a>
                                </SidebarMenuButton>
                              </HoverCardTrigger>
                              <HoverCardContent side="left" className="flex w-64 flex-col gap-0.5">
                                <div className="font-semibold">
                                  {configs.heading}
                                </div>
                                <div>{configs.content}</div>
                              </HoverCardContent>
                            </HoverCard>
                            <HoverCard openDelay={10} closeDelay={100}>
                              <HoverCardTrigger asChild>
                                <SidebarMenuButton asChild>
                                  <a href={GameConfig.url}>
                                    <CommandItem>
                                      <GameConfig.icon />

                                      <span className="w-22">
                                        {GameConfig.title}
                                      </span>
                                    </CommandItem>
                                  </a>
                                </SidebarMenuButton>
                              </HoverCardTrigger>
                              <HoverCardContent side="left" className="flex w-64 flex-col gap-0.5">
                                <div className="font-semibold">
                                  {GameConfig.heading}
                                </div>
                                <div>{GameConfig.content}</div>
                              </HoverCardContent>
                            </HoverCard>
                          </>
                        )}
                      </SidebarMenuItem>
                    </CommandGroup>
                  </CommandList>
                </Command>
              </SidebarMenu>
              <SidebarMenu></SidebarMenu>
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
    </>
  );
}
