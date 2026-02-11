import { Outlet } from "react-router-dom";
import Navbar from "../Layout/Navbar";

export default function Layout() {
  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <main className="flex-1 p-6 bg-gray-100 pt-14">
        <Outlet />
      </main>
    </div>
  );
}
