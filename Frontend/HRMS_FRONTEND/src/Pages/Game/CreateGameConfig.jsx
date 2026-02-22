import React from "react";
import { useNavigate } from "react-router-dom";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { createGameConfigg } from "../../Services/Game";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";

function CreateGameConfig() {
  const { authUser } = useAuthUserContext();
  const navigate = useNavigate();

  if (authUser.role !== "HR") {
    return <p>you are not authorized for this page !!</p>;
  }
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();

  const onSubmit = async (data) => {
    try {
      const payload = {
        name: data.name,
        minPlayers: parseInt(data.minPlayers),
        maxPlayers: parseInt(data.maxPlayers),
        slotDuration: parseInt(data.slotDuration),
        startTime: data.startTime,
        endTime: data.endTime,
      };

      if (payload.maxPlayers < payload.minPlayers) {
        toast.warn("max player should be greater than min player");
        return;
      }
      if (payload.startTime > payload.endTime) {
        toast.warn("end time should be after start time");
        return;
      }
      await createGameConfigg(payload);
      navigate("/game/config");
      toast.success("game created successfully!!")
    } catch (e) {
      handleGlobalError(e);
    }
  };

  return (
    <>
      <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow">
        <h2 className="text-2xl font-bold mb-4 text-center">
          Create Game Configuration
        </h2>
        <form
          onSubmit={handleSubmit(onSubmit)}
          className="space-y-4 p-4 max-w-md border rounded"
        >
          <div>
            <label className="block text-sm font-medium mb-1">Game Name</label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="text"
              placeholder="Enter game name...."
              {...register("name", {
                required: "Game name is required",
              })}
            />
            {errors.name && (
              <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Start-Time</label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="time"
              placeholder="start time for slots...."
              {...register("startTime", {
                required: "startTime is required",
              })}
            />
            {errors.startTime && (
              <p className="text-red-500 text-sm mt-1">
                {errors.startTime.message}
              </p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">End-Time</label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="time"
              placeholder="End time for slots...."
              {...register("endTime", {
                required: "end-time is required",
              })}
            />
            {errors.endTime && (
              <p className="text-red-500 text-sm mt-1">
                {errors.endTime.message}
              </p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">
              Slot-Duration
            </label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="number"
              placeholder="Duration for slots...."
              {...register("slotDuration", {
                required: "slotDuration is required",
                validate: (value) =>
                  parseFloat(value) > 0 || "Value must be greater than 0",
              })}
            />
            {errors.slotDuration && (
              <p className="text-red-500 text-sm mt-1">
                {errors.slotDuration.message}
              </p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">
              Minimum-Player
            </label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="number"
              placeholder="minimum player for booking request...."
              {...register("minPlayers", {
                required: "minPlayers is required",
                validate: (value) =>
                  parseFloat(value) > 0 || "Value must be greater than 0",
              })}
            />
            {errors.minPlayers && (
              <p className="text-red-500 text-sm mt-1">
                {errors.minPlayers.message}
              </p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">
              Maximum-Player
            </label>
            <input
              className="border rounded px-3 py-2 w-full"
              type="number"
              placeholder="maximum player for booking request...."
              {...register("maxPlayers", {
                required: "maxPlayers is required",
                validate: (value) =>
                  parseFloat(value) > 0 || "Value must be greater than 0",
              })}
            />
            {errors.maxPlayers && (
              <p className="text-red-500 text-sm mt-1">
                {errors.maxPlayers.message}
              </p>
            )}
          </div>
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 bg-black text-white rounded hover:bg-gray-700"
          >
            {isSubmitting ? "Submitting..." : "Submit"}
          </button>
        </form>
      </div>
    </>
  );
}

export default CreateGameConfig;