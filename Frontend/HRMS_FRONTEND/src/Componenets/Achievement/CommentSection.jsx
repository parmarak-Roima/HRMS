import React, { useState, useEffect } from "react";
import { fetchComments, addComment } from "../../Services/AchievementService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { Loader } from "../../components/ui/Loader";
import CommentItem from "./CommentItem";
import { Send } from "lucide-react";

const CommentSection = ({ postId, authUser }) => {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [commentText, setCommentText] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const loadComments = async () => {
      try {
        setLoading(true);
        const response = await fetchComments(postId);
        setComments(response.data);
      } catch (e) {
        handleGlobalError(e);
      } finally {
        setLoading(false);
      }
    };
    loadComments();
  }, [postId]);

  const handleAddComment = async () => {
    if (!commentText.trim()) return;
    try {
      setSubmitting(true);
      const response = await addComment(postId, { text: commentText });
      setComments((prev) => [...prev, response.data]);
      setCommentText("");
    } catch (e) {
      handleGlobalError(e);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCommentDeleted = (commentId) => {
    setComments((prev) => prev.filter((c) => c.id !== commentId));
  };

  const handleCommentUpdated = (updated) => {
    setComments((prev) =>
      prev.map((c) => (c.id === updated.id ? updated : c))
    );
  };

  const handleReplyAdded = (parentCommentId, reply) => {
    setComments((prev) =>
      prev.map((c) =>
        c.id === parentCommentId
          ? { ...c, replies: [...(c.replies || []), reply] }
          : c
      )
    );
  };

  return (
    <div className="mt-4">
      <div className="border-t border-gray-100 pt-4">
        {loading ? (
          <Loader size={20} />
        ) : (
          <div className="space-y-3 mb-4">
            {comments.length === 0 && (
              <p className="text-xs text-gray-400 text-center">
                No comments yet. Be the first!
              </p>
            )}
            {comments.map((comment) => (
              <CommentItem
                key={comment.id}
                comment={comment}
                postId={postId}
                authUser={authUser}
                onDeleted={handleCommentDeleted}
                onUpdated={handleCommentUpdated}
                onReplyAdded={handleReplyAdded}
              />
            ))}
          </div>
        )}

        {/* Add Comment Input */}
        <div className="flex gap-2 items-center mt-2">
          <div className="w-7 h-7 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-semibold text-xs flex-shrink-0">
            {authUser?.name?.charAt(0).toUpperCase()}
          </div>
          <input
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleAddComment()}
            className="flex-1 border border-gray-200 rounded-full px-4 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-black"
            placeholder="Write a comment..."
          />
          <button
            onClick={handleAddComment}
            disabled={submitting || !commentText.trim()}
            className="text-gray-400 hover:text-black transition disabled:opacity-30"
          >
            <Send size={18} />
          </button>
        </div>
      </div>
    </div>
  );
};

export default CommentSection;
