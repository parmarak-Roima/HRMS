import React, { useState, useEffect, useCallback } from "react";
import { Plus } from "lucide-react";
import { toast } from "react-toastify";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { Loader } from "../../components/ui/Loader";
import { fetchFeed } from "../../Services/AchievementService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import PostCard from "../../Componenets/Achievement/PostCard";
import CreatePostModal from "../../Componenets/Achievement/CreatePostModal";
import FilterBar from "../../Componenets/Achievement/FilterBar";

const AchievementsPage = () => {
  const { authUser } = useAuthUserContext();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingPost, setEditingPost] = useState(null);
  const [filters, setFilters] = useState({
    authorId: "",
    tagName: "",
    from: "",
    to: "",
  });

  const loadFeed = useCallback(async () => {
    try {
      setLoading(true);
      const params = {};
      if (filters.authorId) params.authorId = filters.authorId;
      if (filters.tagName) params.tagName = filters.tagName;
      if (filters.from) params.from = filters.from;
      if (filters.to) params.to = filters.to;
      const response = await fetchFeed(params);
      console.log(response);
      setPosts(response.data);
    } catch (e) {
      handleGlobalError(e);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    loadFeed();
  }, [loadFeed]);

  const handlePostCreated = (newPost) => {
    setPosts((prev) => [newPost.data, ...prev]);
    setShowCreateModal(false);
    toast.success("Post created successfully!");
  };

  const handlePostUpdated = (updatedPost) => {
    setPosts((prev) =>
      prev.map((p) => (p.id === updatedPost.id ? updatedPost : p))
    );
    setEditingPost(null);
    toast.success("Post updated successfully!");
  };

  const handlePostDeleted = (postId) => {
    setPosts((prev) => prev.filter((p) => p.id !== postId));
    toast.success("Post deleted.");
  };

  const handleLikeToggled = (postId, likeData) => {
    setPosts((prev) =>
      prev.map((p) =>
        p.id === postId
          ? { ...p, likeCount: likeData.likeCount, likedByMe: likeData.liked }
          : p
      )
    );
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6 font-sans">
      <div className="max-w-3xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Achievements</h1>
          <button
            onClick={() => setShowCreateModal(true)}
            className="flex items-center gap-2 bg-black text-white font-medium py-2 px-4 rounded-2xl hover:bg-gray-800 transition"
          >
            <Plus size={18} />
            New Post
          </button>
        </div>

        {/* Filters */}
        <FilterBar filters={filters} setFilters={setFilters} />

        {/* Feed */}
        {loading ? (
          <Loader size={32} />
        ) : posts.length === 0 ? (
          <div className="text-center py-20 text-gray-400 text-lg">
            No achievements found. Be the first to post! 🎉
          </div>
        ) : (
          <div className="space-y-4">
            {posts?.map((post) => (
              <PostCard
                key={post.id}
                post={post}
                authUser={authUser}
                onEdit={(post) => setEditingPost(post)}
                onDeleted={handlePostDeleted}
                onLikeToggled={handleLikeToggled}
              />
            ))}
          </div>
        )}
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <CreatePostModal
          onClose={() => setShowCreateModal(false)}
          onSuccess={handlePostCreated}
        />
      )}

      {/* Edit Modal */}
      {editingPost && (
        <CreatePostModal
          existingPost={editingPost}
          onClose={() => setEditingPost(null)}
          onSuccess={handlePostUpdated}
        />
      )}
    </div>
  );
};

export default AchievementsPage;
