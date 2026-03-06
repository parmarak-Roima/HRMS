import { loginUser, sendOtp } from "../../Services/authService";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import SendOtp from "../../Componenets/Auth/SendOtp";

export default function Login() {
  const {
    register,
    handleSubmit,
    formState: { isSubmitting, errors },
  } = useForm();
  
  const navigate = useNavigate();
  const { authUser, setAuthUser } = useAuthUserContext();
  

  useEffect(() => {
    // localStorage.clear("token");
  }, []);

  const handleLogin = async (data) => {
    try {
      const response = await loginUser(data);
      localStorage.setItem("token", response.token);
      console.log(response.user);
      setAuthUser(response.user);
      toast.success("logged in successfully !!!");
      navigate(`/profile/${response.user.id}`);
    } catch (err) {
      handleGlobalError(err);
    }
  };
  
  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <div className=" bg-gray-370 w-full max-w-sm border border-black rounded-2xl shadow-lg p-6">
        <h2 className="text-2xl font-semibold text-center mb-6 text-gray-800">
          Login
        </h2>

        <form onSubmit={handleSubmit(handleLogin)} className="space-y-4">
          <div>
            <label className="block mb-1 font-medium">Email</label>
            <input
              type="email"
              placeholder="Enter Registered Email"
              className="w-full border border-black rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-gray-500"
              {...register("email", {
                required: "Email is required",
                pattern: {
                  value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                  message: "Invalid email format",
                },
              })}
            />
            {errors.email && (
              <p className="text-red-500 text-sm">{errors.email.message}</p>
            )}
          </div>
          <div className="">
            <label className="block mb-1 font-medium">Password</label>
            <input
              type="password"
              placeholder="Enter password"
              className="w-full border border-black rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-gray-500"
              {...register("password", {
                required: "Password is required",
              })}
            />
            {errors.password && (
              <p className="text-red-500 text-sm">{errors.password.message}</p>
            )}
          </div>
          <SendOtp />
          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full bg-black text-white py-2 rounded-lg hover:bg-gray-600 disabled:opacity-50"
          >
            {isSubmitting ? "Logging in..." : "Login"}
          </button>
        </form>
        <div className="flex justify-center  py-2">
          <button
            onClick={handleGoogleLogin}
            className=" text-white px-4 rounded"
          >
            <img
              className="w-12 m-auto"
              src="https://static.vecteezy.com/system/resources/previews/046/861/647/non_2x/google-logo-transparent-background-free-png.png"
              alt=""
            />
          </button>
        </div>
        <p className="text-center font-black text-muted-foreground">
          Login with google
        </p>
      </div>
    </div>
  );
}
