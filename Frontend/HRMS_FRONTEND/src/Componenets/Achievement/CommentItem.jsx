import React, { useState } from "react";
import { Pencil, Trash2, CornerDownRight, Send, X, Check } from "lucide-react";
import {
  deleteComment,
  updateComment,
  addComment,
} from "../../Services/AchievementService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";

const CommentItem = ({
  comment,
  postId,
  authUser,
  onDeleted,
  onUpdated,
  onReplyAdded,
  isReply = false,
}) => {
  const [editing, setEditing] = useState(false);
  const [editText, setEditText] = useState(comment.text);
  const [showReplyInput, setShowReplyInput] = useState(false);
  const [replyText, setReplyText] = useState("");
  const [submittingEdit, setSubmittingEdit] = useState(false);
  const [submittingReply, setSubmittingReply] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const isOwner = authUser?.id === comment.authorId;
  const isHR = authUser?.role === "HR";

  const handleDelete = async () => {
    try {
      await deleteComment(comment.id);
      onDeleted(comment.id);
    } catch (e) {
      handleGlobalError(e);
    }
  };

  const handleUpdate = async () => {
    if (!editText.trim()) return;
    try {
      setSubmittingEdit(true);
      const response = await updateComment(comment.id, { text: editText });
      onUpdated(response.data);
      setEditing(false);
    } catch (e) {
      handleGlobalError(e);
    } finally {
      setSubmittingEdit(false);
    }
  };

  const handleReply = async () => {
    if (!replyText.trim()) return;
    try {
      setSubmittingReply(true);
      const response = await addComment(postId, {
        text: replyText,
        parentCommentId: comment.id,
      });
      onReplyAdded(comment.id, response.data);
      setReplyText("");
      setShowReplyInput(false);
    } catch (e) {
      handleGlobalError(e);
    } finally {
      setSubmittingReply(false);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    return new Date(dateStr).toLocaleDateString("en-IN", {
      day: "numeric",
      month: "short",
    });
  };

  return (
    <div className={`${isReply ? "ml-8 border-l-2 border-gray-100 pl-3" : ""}`}>
      <div className="flex items-start gap-2">
        {/* Avatar */}
        <div className="w-7 h-7 rounded-full bg-gray-200 flex items-center justify-center text-xs font-semibold text-gray-600 flex-shrink-0">
          {comment.authorName?.charAt(0).toUpperCase()}
        </div>

        <div className="flex-1">
          {/* Comment bubble */}
          <div className="bg-gray-50 rounded-xl px-3 py-2">
            <div className="flex items-center justify-between mb-0.5">
              <span className="text-xs font-semibold text-gray-800">
                {comment.authorName}
              </span>
              <span className="text-xs text-gray-400">
                {formatDate(comment.createdAt)}
              </span>
            </div>

            {editing ? (
              <div className="flex gap-2 items-center mt-1">
                <input
                  value={editText}
                  onChange={(e) => setEditText(e.target.value)}
                  className="flex-1 border border-gray-300 rounded-lg px-2 py-1 text-xs focus:outline-none focus:ring-1 focus:ring-black"
                  autoFocus
                />
                <button
                  onClick={handleUpdate}
                  disabled={submittingEdit}
                  className="text-green-600 hover:text-green-700 transition"
                >
                  <Check size={14} />
                </button>
                <button
                  onClick={() => { setEditing(false); setEditText(comment.text); }}
                  className="text-gray-400 hover:text-gray-600 transition"
                >
                  <X size={14} />
                </button>
              </div>
            ) : (
              <p className="text-sm text-gray-700">{comment.text}</p>
            )}
          </div>

          {/* Actions */}
          <div className="flex items-center gap-3 mt-1 ml-1">
            {!isReply && (
              <button
                onClick={() => setShowReplyInput((prev) => !prev)}
                className="text-xs text-gray-400 hover:text-gray-700 flex items-center gap-1 transition"
              >
                <CornerDownRight size={12} />
                Reply
              </button>
            )}
            {isOwner && !editing && (
              <button
                onClick={() => setEditing(true)}
                className="text-xs text-gray-400 hover:text-gray-700 flex items-center gap-1 transition"
              >
                <Pencil size={12} />
                Edit
              </button>
            )}
            {(isOwner || isHR) && (
              <button
                onClick={() => setShowDeleteConfirm(true)}
                className="text-xs text-gray-400 hover:text-red-500 flex items-center gap-1 transition"
              >
                <Trash2 size={12} />
                Delete
              </button>
            )}
          </div>

          {/* Delete confirm inline */}
          {showDeleteConfirm && (
            <div className="mt-1 ml-1 text-xs text-gray-600 flex items-center gap-2">
              <span>Delete this comment?</span>
              <button
                onClick={handleDelete}
                className="text-red-500 font-medium hover:underline"
              >
                Yes
              </button>
              <button
                onClick={() => setShowDeleteConfirm(false)}
                className="text-gray-400 hover:underline"
              >
                No
              </button>
            </div>
          )}

          {/* Reply Input */}
          {showReplyInput && (
            <div className="flex gap-2 items-center mt-2 ml-1">
              <input
                value={replyText}
                onChange={(e) => setReplyText(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleReply()}
                className="flex-1 border border-gray-200 rounded-full px-3 py-1 text-xs focus:outline-none focus:ring-1 focus:ring-black"
                placeholder={`Reply to ${comment.authorName}...`}
                autoFocus
              />
              <button
                onClick={handleReply}
                disabled={submittingReply || !replyText.trim()}
                className="text-gray-400 hover:text-black transition disabled:opacity-30"
              >
                <Send size={14} />
              </button>
              <button
                onClick={() => setShowReplyInput(false)}
                className="text-gray-400 hover:text-gray-600 transition"
              >
                <X size={14} />
              </button>
            </div>
          )}

          {/* Nested replies */}
          {comment.replies?.length > 0 && (
            <div className="mt-2 space-y-2">
              {comment.replies.map((reply) => (
                <CommentItem
                  key={reply.id}
                  comment={reply}
                  postId={postId}
                  authUser={authUser}
                  onDeleted={onDeleted}
                  onUpdated={onUpdated}
                  onReplyAdded={onReplyAdded}
                  isReply={true}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CommentItem;
