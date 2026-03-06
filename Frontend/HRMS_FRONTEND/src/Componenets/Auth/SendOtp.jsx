import React, { useRef, useState } from "react";
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
import { changePassowrd, sendOtp } from "../../Services/authService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { toast } from "react-toastify";
function SendOtp() {
  const closeRef = useRef(null);

  const [forgotEmail, setForgotEmail] = useState("");
  const [passowrd, setPassword] = useState("");
  const [otp, setOtp] = useState(0);
  const [loading, setLoading] = useState(false);
  const [showChangePass, setShowChangePass] = useState(false);
  const handleSendOtp = async () => {
    try {
      setLoading(true);
      await sendOtp(forgotEmail);
      toast.success("otp sent successFully!");
      setShowChangePass(true);
    } catch (err) {
      handleGlobalError(err);
    } finally {
      setLoading(false);
    }
  };
  const handleChangePassword = async () => {
    try {
      setLoading(true);
      if (otp < 1000 || otp > 9999) {
        toast.error("otp must be of 4 digits");
        // setLoading(false)
        return;
      }
      if (passowrd.length < 8) {
        toast.error("passowrd length must be greter or equal to 8 characters");
        return;
      }
      const payload = {
        newPassword: passowrd,
        otp: otp,
      };

      await changePassowrd(forgotEmail, payload);
      toast.success("password changed succssFully!!");
      setOtp(0);
      setForgotEmail("");
      setPassword("");
    } catch (e) {
      handleGlobalError(e);
    } finally {
      setLoading(false);
    }
  };
  return (
    <Dialog>
      <DialogTrigger>
        <p className="underline text-blue-400 font-light">Forgot passowrd</p>
      </DialogTrigger>
      <DialogContent>
        <label className="block text-sm font-medium mb-1">Email-id</label>
        <input
          className="border rounded px-3 py-2 w-full"
          type="text"
          placeholder="Email...."
          required={true}
          value={forgotEmail}
          onChange={(e) => {
            setForgotEmail(e.target.value);
          }}
        />
        {showChangePass && (
          <Dialog>
            <DialogTrigger>
              <p className="text-start underline text-blue-400 font-light">
                Change passowrd
              </p>
            </DialogTrigger>
            <DialogContent>
              <label className="block text-sm font-medium">Email-id</label>
              <input
                className="border rounded px-3 py-2 w-full"
                type="text"
                value={forgotEmail}
              />
              <label className="block font-medium">New-Password</label>
              <input
                type="password"
                placeholder="Enter a new password"
                className="w-full border border-black rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-gray-500"
                value={passowrd}
                onChange={(e) => setPassword(e.target.value)}
              />
              <label className="block font-medium">Otp</label>
              <input
                type="number"
                placeholder="Enter a otp..."
                className="w-full border border-black rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-gray-500"
                value={otp}
                onChange={(e) => setOtp(parseInt(e.target.value))}
              />
              <div className="flex gap-2">
                <Button onClick={handleChangePassword} disabled={loading}>
                  {loading ? "changing..." : "change-password"}
                </Button>
                <DialogFooter className="sm:justify-start">
                  <DialogClose asChild>
                    <Button disabled={loading} type="button" className="w-full">
                      Close
                    </Button>
                  </DialogClose>
                </DialogFooter>
              </div>
            </DialogContent>
          </Dialog>
        )}

        <div className="flex gap-2">
          <Button onClick={handleSendOtp} disabled={loading}>
            {loading ? "sending..." : "send-otp"}
          </Button>
          <DialogFooter className="sm:justify-start">
            <DialogClose asChild>
              <Button disabled={loading} type="button" className="w-full">
                Close
              </Button>
            </DialogClose>
          </DialogFooter>
        </div>
      </DialogContent>
    </Dialog>
  );
}

export default SendOtp;
