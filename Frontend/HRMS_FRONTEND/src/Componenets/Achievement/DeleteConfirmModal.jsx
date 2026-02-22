import React, { useState } from "react";
import { AlertTriangle, X } from "lucide-react";

const DeleteConfirmModal = ({ isHR, onConfirm, onClose }) => {
  const [reason, setReason] = useState("");

  const handleConfirm = () => {
    onConfirm(isHR ? reason : undefined);
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6">
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center gap-2 text-red-500">
            <AlertTriangle size={20} />
            <h3 className="font-bold text-gray-900">Delete Post</h3>
          </div>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-700">
            <X size={18} />
          </button>
        </div>

        <p className="text-sm text-gray-600 mb-4">
          {isHR
            ? "As HR, deleting this post will send a warning email to the author."
            : "Are you sure you want to delete this post? This cannot be undone."}
        </p>

        {isHR && (
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Reason (optional)
            </label>
            <textarea
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              rows={3}
              className="border border-gray-300 rounded-lg px-3 py-2 w-full text-sm focus:outline-none focus:ring-2 focus:ring-black resize-none"
              placeholder="Reason for removal..."
            />
          </div>
        )}

        <div className="flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 border border-gray-300 text-gray-700 font-medium py-2 rounded-lg hover:bg-gray-50 transition text-sm"
          >
            Cancel
          </button>
          <button
            onClick={handleConfirm}
            className="flex-1 bg-red-500 text-white font-medium py-2 rounded-lg hover:bg-red-600 transition text-sm"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmModal;
