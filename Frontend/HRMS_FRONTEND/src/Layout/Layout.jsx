import { Outlet, ScrollRestoration } from "react-router-dom";
import Navbar from "../Layout/Navbar";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "../Componenets/app-sidebar"
export default function Layout() {
  return (
    <div className="flex flex-col min-h-screen">
      {/* <Navbar /> */}
      <SidebarProvider>
      <AppSidebar />
      <main className="overflow-x-auto  flex-1 bg-gray-100">
         <SidebarTrigger />
        <Outlet />
        
      </main>
      </SidebarProvider>
      <ScrollRestoration />
    </div>
  );
}
 
     
  