import React, { useState } from "react";
import {
  Heart,
  MessageCircle,
  Pencil,
  Trash2,
  Tag,
  Cake,
  PartyPopper,
  Paperclip,
} from "lucide-react";
import { toast } from "react-toastify";
import { toggleLike, deletePost } from "../../Services/AchievementService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import CommentSection from "./CommentSection";
import DeleteConfirmModal from "./DeleteConfirmModal";

const PostCard = ({ post, authUser, onEdit, onDeleted, onLikeToggled }) => {
  const [showComments, setShowComments] = useState(false);
  const [likedByMe, setLikedByMe] = useState(post.likedByMe);
  const [likeCount, setLikeCount] = useState(post.likeCount);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [likeLoading, setLikeLoading] = useState(false);

  const isOwner = authUser?.id === post.authorId;
  const isHR = authUser?.role === "HR";
  const canEdit = isOwner && !post.isSystemGenerated;
  const canDelete = (isOwner || isHR) && !post.isSystemGenerated;

  const handleLike = async () => {
    if (likeLoading) return;
    // Optimistic update
    setLikedByMe((prev) => !prev);
    setLikeCount((prev) => (likedByMe ? prev - 1 : prev + 1));
    setLikeLoading(true);
    try {
      const response = await toggleLike(post.id);
      setLikedByMe(response.data.liked);
      setLikeCount(response.data.likeCount);
      onLikeToggled(post.id, response.data);
    } catch (e) {
      // Rollback
      setLikedByMe((prev) => !prev);
      setLikeCount((prev) => (likedByMe ? prev + 1 : prev - 1));
      handleGlobalError(e);
    } finally {
      setLikeLoading(false);
    }
  };

  const handleDelete = async (reason) => {
    try {
      await deletePost(post.id, reason);
      onDeleted(post.id);
      setShowDeleteModal(false);
    } catch (e) {
      handleGlobalError(e);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    return new Date(dateStr).toLocaleDateString("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  };

  const SystemBadge = () => {
    if (!post.isSystemGenerated) return null;
    const isBirthday = post.systemEventType === "BIRTHDAY";
    return (
      <span
        className={`inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full ${
          isBirthday
            ? "bg-pink-100 text-pink-700"
            : "bg-purple-100 text-purple-700"
        }`}
      >
        {isBirthday ? <Cake size={12} /> : <PartyPopper size={12} />}
        {isBirthday ? "Birthday" : "Work Anniversary"}
      </span>
    );
  };

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
      {/* Top row: Avatar + Author + Date + Actions */}
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-3">
          {post.authorProfileUrl ? (
            <img
              src={post.authorProfileUrl}
              alt={post.authorName}
              className="w-10 h-10 rounded-full object-cover"
            />
          ) : (
            <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-semibold text-sm">
              {post.authorName?.charAt(0).toUpperCase() || "S"}
            </div>
          )}
          <div>
            <p className="font-semibold text-gray-900 text-sm">
              {post.authorName}
            </p>
            <p className="text-xs text-gray-400">{formatDate(post.createdAt)}</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <SystemBadge />
          {canEdit && (
            <button
              onClick={() => onEdit(post)}
              className="text-gray-400 hover:text-gray-700 transition"
              title="Edit post"
            >
              <Pencil size={16} />
            </button>
          )}
          {canDelete && (
            <button
              onClick={() => setShowDeleteModal(true)}
              className="text-gray-400 hover:text-red-500 transition"
              title="Delete post"
            >
              <Trash2 size={16} />
            </button>
          )}
        </div>
      </div>

      {/* Title & Description */}
      <h2 className="text-base font-bold text-gray-900 mb-1">{post.title}</h2>
      <p className="text-sm text-gray-600 mb-3 leading-relaxed">
        {post.description}
      </p>

      {/* Tags */}
      {post.tags?.length > 0 && (
        <div className="flex flex-wrap gap-2 mb-3">
          {post.tags.map((tag) => (
            <span
              key={tag}
              className="inline-flex items-center gap-1 text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded-full"
            >
              <Tag size={10} />
              {tag}
            </span>
          ))}
        </div>
      )}

      {/* Attachments */}
      {post.attachmentUrls?.length > 0 && (
        <div className="flex flex-wrap gap-2 mb-3">
          {post.attachmentUrls.map((url, i) => (
            <a
              key={i}
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-1 text-xs text-blue-600 hover:underline"
            >
              <Paperclip size={12} />
              Attachment {i + 1}
            </a>
          ))}
        </div>
      )}

      {/* Divider */}
      <div className="border-t border-gray-100 my-3" />

      {/* Actions Row */}
      <div className="flex items-center gap-5 text-sm text-gray-500">
        <button
          onClick={handleLike}
          className={`flex items-center gap-1.5 transition ${
            likedByMe ? "text-red-500" : "hover:text-red-400"
          }`}
        >
          <Heart size={18} fill={likedByMe ? "currentColor" : "none"} />
          <span>{likeCount}</span>
        </button>

        <button
          onClick={() => setShowComments((prev) => !prev)}
          className="flex items-center gap-1.5 hover:text-gray-700 transition"
        >
          <MessageCircle size={18} />
          <span>{post.commentCount}</span>
        </button>
      </div>

      {/* Comment Section */}
      {showComments && (
        <CommentSection
          postId={post.id}
          authUser={authUser}
        />
      )}

      {/* Delete Confirm Modal */}
      {showDeleteModal && (
        <DeleteConfirmModal
          isHR={isHR && !isOwner}
          onConfirm={handleDelete}
          onClose={() => setShowDeleteModal(false)}
        />
      )}
    </div>
  );
};

export default PostCard;
