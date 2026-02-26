import { Outlet } from "react-router-dom";
import Navbar from "../Layout/Navbar";

export default function Layout() {
  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <main className="overflow-x-auto  flex-1 bg-gray-100 pt-14">
        <Outlet />
      </main>
    </div>
  );
}
