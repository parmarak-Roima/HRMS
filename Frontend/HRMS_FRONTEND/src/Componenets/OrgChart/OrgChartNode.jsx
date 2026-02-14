import React from "react";
import { User } from "lucide-react";

const OrgChartNode = ({ employee, type, onClick }) => {
  const isFocus = type === "focus";

  return (
    <div
      onClick={() => onClick(employee.id)}
      className={`
        flex flex-col items-center justify-center 
        p-4 border rounded-lg  bg-white
        ${isFocus ? "border-2 border-black shadow-md w-64" : "border-gray-200 hover:border-gray-400 w-48"}
       }`}
    >
      <div
        className={`
        rounded-full flex items-center justify-center mb-3 overflow-hidden h-16 w-16 bg-black text-white
      `}
      >
        {employee.profileUrl ? (
          <img
            src={employee.profileUrl}
            className="h-full w-full object-cover"
          />
        ) : (
          <User size={24} />
        )}
      </div>
      <div className="text-center">
        <h3 className={`font-bold text-gray-900 text-lg`}>{employee.name}</h3>
        <p
          className={`text-gray-500 uppercase tracking-wide text-sm font-semibold`}
        >
          {employee.designation}
        </p>
      </div>
    </div>
  );
};

export default OrgChartNode;
